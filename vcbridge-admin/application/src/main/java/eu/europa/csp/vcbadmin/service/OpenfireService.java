package eu.europa.csp.vcbadmin.service;

import java.util.ArrayList;

import javax.ws.rs.core.Response;

import org.igniterealtime.restclient.RestApiClient;
import org.igniterealtime.restclient.entity.AuthenticationToken;
import org.igniterealtime.restclient.entity.MUCRoomEntity;
import org.igniterealtime.restclient.entity.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import eu.europa.csp.vcbadmin.config.OpenfireProperties;
import eu.europa.csp.vcbadmin.model.Meeting;
import eu.europa.csp.vcbadmin.model.Participant;
import eu.europa.csp.vcbadmin.service.exception.ErrorAddingUserToMeeting;
import eu.europa.csp.vcbadmin.service.exception.ErrorCreatingRoom;
import eu.europa.csp.vcbadmin.service.exception.ErrorCreatingUser;
import eu.europa.csp.vcbadmin.service.exception.ErrorDeletingRoom;
import eu.europa.csp.vcbadmin.service.exception.ErrorDeletingUser;
import eu.europa.csp.vcbadmin.service.exception.OpenfireException;

@Service
public class OpenfireService {
	private static final Logger log = LoggerFactory.getLogger(OpenfireService.class);
	@Autowired
	OpenfireProperties properties;

	@Autowired
	RetryTemplate retryTemplate;

	private void createRoom(RestApiClient restApiClient, String room) throws ErrorCreatingRoom {
		MUCRoomEntity chatRoom = new MUCRoomEntity(room, room, "Some description");
		Response r = restApiClient.createChatRoom(chatRoom);
		if (r.getStatus() != 201) {
			throw new ErrorCreatingRoom(String.format("Error creating room %s", room));
		}
	}

	private void createUser(RestApiClient restApiClient, Participant participant) throws ErrorCreatingUser {
		UserEntity userEntity = new UserEntity(participant.getUsername(), participant.getUsername(),
				participant.getEmail(), participant.getPassword());
		Response r = restApiClient.createUser(userEntity);
		if (r.getStatus() != 201) {
			throw new ErrorCreatingUser(String.format("Error creating user %s", participant.getUsername()));
		}
	}

	private void addUserToRoom(RestApiClient restApiClient, String username, String room)
			throws ErrorAddingUserToMeeting {
		Response r = restApiClient.addMember(room, username);
		if (r.getStatus() != 201) {
			throw new ErrorAddingUserToMeeting(String.format("Error adding user %s to room %s", username, room));
		}
	}

	private void deleteRoom(RestApiClient restApiClient, String room) throws ErrorDeletingRoom {
		Response r = restApiClient.deleteChatRoom(room);
		if (r.getStatus() != 200) {
			throw new ErrorDeletingRoom(String.format("Error deleting room %s", room));
		}
	}

	private void deleteUser(RestApiClient restApiClient, String username) throws ErrorDeletingUser {
		Response r = restApiClient.deleteUser(username);
		if (r.getStatus() != 200) {
			throw new ErrorDeletingUser(String.format("Error deleting user %s", username));
		}
	}

	public void createMeeting(Meeting meeting) throws OpenfireException {
		log.info("Creating meeting...");
		log.info("Open openfire connection...");
		log.info(properties.toString());
		// Basic HTTP Authentication
		AuthenticationToken authenticationToken = new AuthenticationToken(properties.getAuthUsername(),
				properties.getAuthPassword());
		// Shared secret key
		// AuthenticationToken authenticationToken = new
		// AuthenticationToken("a62nV75X8MgB7sxu");
		// Set Openfire settings (9090 is the port of Openfire Admin Console)
		RestApiClient restApiClient = new RestApiClient(properties.getVideobridgeHost(),
				Integer.valueOf(properties.getVideobridgeAdminPort()), authenticationToken);
		log.info("Creating room {}...", meeting.getRoom());
		// Create a new chat room (chatroom id, chatroom name, description).
		// There are more chatroom settings available.
		retryTemplate.execute(new RetryCallback<Void, ErrorCreatingRoom>() {
			@Override
			public Void doWithRetry(RetryContext arg0) throws ErrorCreatingRoom {
				createRoom(restApiClient, meeting.getRoom());
				return null;
			}
		});

		log.info("Room {} created...", meeting.getRoom());
		log.info("Creating {} users for room {}...", meeting.getParticipants().size(), meeting.getRoom());
		ArrayList<String> addedUsers = new ArrayList<>();
		for (Participant participant : meeting.getParticipants()) {
			// Create a new user (username, name, email, passowrd). There are
			// more user settings available.
			log.info("Creating user {}...", participant.getUsername());
			retryTemplate.execute(new RetryCallback<Void, ErrorCreatingUser>() {
				@Override
				public Void doWithRetry(RetryContext arg0) throws ErrorCreatingUser {
					createUser(restApiClient, participant);
					return null;
				}
			}, new RecoveryCallback<Void>() {
				@Override
				public Void recover(RetryContext arg0) throws ErrorDeletingRoom, ErrorDeletingUser, ErrorCreatingUser {
					deleteRoom(restApiClient, meeting.getRoom());
					for (String added_user : addedUsers) {
						deleteUser(restApiClient, added_user);
					}
					throw new ErrorCreatingUser(String.format("Error creating user %s", participant.getUsername()));
				}
			});
			addedUsers.add(participant.getUsername()); // log added users
			log.info("User created, username: {}, email: {} created...", participant.getUsername(),
					participant.getEmail());
			log.info("Adding user {} to room {}", participant.getUsername(), meeting.getRoom());
			// Add user with role "member" to a chat room
			retryTemplate.execute(new RetryCallback<Void, ErrorAddingUserToMeeting>() {
				@Override
				public Void doWithRetry(RetryContext arg0) throws ErrorAddingUserToMeeting {
					addUserToRoom(restApiClient, participant.getUsername(), meeting.getRoom());
					return null;
				}
			}, new RecoveryCallback<Void>() {
				@Override
				public Void recover(RetryContext arg0)
						throws ErrorDeletingRoom, ErrorDeletingUser, ErrorAddingUserToMeeting {
					deleteRoom(restApiClient, meeting.getRoom());
					for (String added_user : addedUsers) {
						deleteUser(restApiClient, added_user);
					}
					throw new ErrorAddingUserToMeeting(String.format("Error adding user %s to room %s",
							participant.getUsername(), meeting.getRoom()));
				}
			});
			log.info("User {} added to room {}", participant.getUsername(), meeting.getRoom());
		}

	}

	public void deleteMeeting(Meeting meeting) {
		// Basic HTTP Authentication
		AuthenticationToken authenticationToken = new AuthenticationToken(properties.getAuthUsername(),
				properties.getAuthPassword());
		// AuthenticationToken authenticationToken = new
		// AuthenticationToken("a62nV75X8MgB7sxu");
		// Set Openfire settings (9090 is the port of Openfire Admin Console)
		RestApiClient restApiClient = new RestApiClient(properties.getVideobridgeHost(),
				Integer.valueOf(properties.getVideobridgeAdminPort()), authenticationToken);
		log.info("Deleting room {}...", meeting.getRoom());
		// Delete a chat room
		try {
			retryTemplate.execute(new RetryCallback<Void, ErrorDeletingRoom>() {
				@Override
				public Void doWithRetry(RetryContext arg0) throws ErrorDeletingRoom {
					deleteRoom(restApiClient, meeting.getRoom());
					return null;
				}
			});
			log.info("Room {} deleted...", meeting.getRoom());
		} catch (ErrorDeletingRoom e) {
			log.error("Error deleting room " + meeting.getRoom() + "... Now trying to delete users...", e);
		}

		log.info("Deleting {} users for room {}...", meeting.getParticipants().size(), meeting.getRoom());
		for (Participant participant : meeting.getParticipants()) {
			// Delete a user
			log.info("Deleting user {}...", participant.getUsername());
			try {
				retryTemplate.execute(new RetryCallback<Void, ErrorDeletingUser>() {
					@Override
					public Void doWithRetry(RetryContext arg0) throws ErrorDeletingUser {
						deleteUser(restApiClient, participant.getUsername());
						return null;
					}
				});
				log.info("User {} deleted...", participant.getUsername());
			} catch (ErrorDeletingUser e) {
				log.error("Error deleting user " + participant.getUsername() + "... Now trying with other users...");
			}
		}
	}
}
