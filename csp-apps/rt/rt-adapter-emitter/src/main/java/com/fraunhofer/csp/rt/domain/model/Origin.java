package com.fraunhofer.csp.rt.domain.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by Majid Salehi on 4/8/17.
 */
@Entity
public class Origin {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	String originCspId;
	String originApplicationId;
	String originRecordId;
	String cspId;
	String applicationId;
	String recordId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOriginCspId() {
		return originCspId;
	}

	public void setOriginCspId(String originCspId) {
		this.originCspId = originCspId;
	}

	public String getOriginApplicationId() {
		return originApplicationId;
	}

	public void setOriginApplicationId(String originApplicationId) {
		this.originApplicationId = originApplicationId;
	}

	public String getOriginRecordId() {
		return originRecordId;
	}

	public void setOriginRecordId(String originRecordId) {
		this.originRecordId = originRecordId;
	}

	public String getCspId() {
		return cspId;
	}

	public void setCspId(String cspId) {
		this.cspId = cspId;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getRecordId() {
		return recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Origin{");
		sb.append("id=").append(id);
		sb.append(", originCspId='").append(originCspId).append('\'');
		sb.append(", originApplicationId='").append(originApplicationId).append('\'');
		sb.append(", originRecordId='").append(originRecordId).append('\'');
		sb.append(", cspId='").append(cspId).append('\'');
		sb.append(", applicationId='").append(applicationId).append('\'');
		sb.append(", recordId='").append(recordId).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
