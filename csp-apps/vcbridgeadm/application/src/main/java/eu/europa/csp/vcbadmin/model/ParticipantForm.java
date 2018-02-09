package eu.europa.csp.vcbadmin.model;

import org.hibernate.validator.constraints.Email;

public class ParticipantForm {
	private String name;
	private String surname;
	@Email
	private String email;

	public ParticipantForm() {
	}

	public ParticipantForm(String name, String surname, String email) {
		this.name = name;
		this.surname = surname;
		this.email = email;
	}

	public String getEmail() {
		return email;
	}


	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}
}
