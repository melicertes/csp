package eu.europa.csp.vcbadmin.model;

import java.time.ZonedDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import eu.europa.csp.vcbadmin.constants.MeetingScheduledTaskType;

@Entity
@Table(name = "vcb_scheduledtask")
public class MeetingScheduledTask {
	@Id
	@GeneratedValue
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	private Meeting meeting;

	@NotNull
	@Enumerated(EnumType.STRING)
	private MeetingScheduledTaskType taskType;

	@NotNull
	private ZonedDateTime taskTime;

	@NotNull
	private Boolean completed;

	@NotNull
	private Integer failed = 0;

	public static MeetingScheduledTask getNewCompleted(MeetingScheduledTaskType taskType, ZonedDateTime taskTime) {
		MeetingScheduledTask m = new MeetingScheduledTask(taskType, taskTime);
		m.setCompleted(true);
		return m;
	}

	public MeetingScheduledTask() {
	}

	/*
	 * If you already saved the meeting entity in database, it is safe to use
	 * this constructor
	 */
	public MeetingScheduledTask(Meeting meeting, MeetingScheduledTaskType taskType, ZonedDateTime taskTime) {
		this(taskType, taskTime);
		this.meeting = meeting;
	}

	/*
	 * If you already saved the meeting entity in database, it is safe to use
	 * this constructor
	 */
	public MeetingScheduledTask(MeetingScheduledTaskType taskType, ZonedDateTime taskTime) {
		super();
		this.taskType = taskType;
		this.taskTime = taskTime;
		this.completed = false;
	}

	public Boolean getCompleted() {
		return completed;
	}

	public Long getId() {
		return id;
	}

	public Meeting getMeeting() {
		return meeting;
	}

	public ZonedDateTime getTaskTime() {
		return taskTime;
	}

	public Integer getFailed() {
		return failed;
	}

	public void setFailed(Integer failed) {
		this.failed = failed;
	}

	public MeetingScheduledTaskType getTaskType() {
		return taskType;
	}

	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setMeeting(Meeting meeting) {
		this.meeting = meeting;
	}

	public void setTaskTime(ZonedDateTime taskTime) {
		this.taskTime = taskTime;
	}

	public void setTaskType(MeetingScheduledTaskType taskType) {
		this.taskType = taskType;
	}

	@Override
	public String toString() {
		return "MeetingScheduledTask [id=" + id + ", taskType=" + taskType + ", taskTime=" + taskTime + ", completed="
				+ completed + "]";
	}

	public void increaseFailed() {
		++failed;
	}

}
