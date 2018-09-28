package com.fraunhofer.csp.rt.domain.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by Majid Salehi on 4/8/17.
 */
@Entity
public class RTuuid {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	// ticket uuid
	String uuid;

	// ticketid
	String tid;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Origin{");
		sb.append("id=").append(id);
		sb.append(", uuid='").append(uuid).append('\'');
		sb.append(", ticketid='").append(tid).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
