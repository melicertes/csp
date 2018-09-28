package com.fraunhofer.csp.rt.app;

/**
 * Created by Majid Salehi on 4/8/17.
 */
public enum IncidentCustomFields {
	CF_LINKED_EVENTS("Linked events"), CF_LINKED_THREATS("Linked threats"), CF_LINKED_VULNERABILITIES(
			"Linked vulnerabilities"), CF_RESOLUTION("Resolution"), CF_FUNCTION("Function"), CF_CLASSIFICATION(
					"Classification"), CF_DESCRIPTION("Description"), CF_RT_UUID("RT_UUID"), CF_SHARING_POLICY(
							"Sharing policy"), CF_ADDITIONAL_DATA("Additional data"), CF_IP("IP"), CF_ORIGINATOR_CSP(
									"Originator CSP"), CF_LAST_UPDATE_DONE_BY("Last update done by");

	private final String field;

	/**
	 * @param field
	 */
	private IncidentCustomFields(final String field) {
		this.field = field;
	}

	@Override
	public String toString() {
		return field;
	}
}
