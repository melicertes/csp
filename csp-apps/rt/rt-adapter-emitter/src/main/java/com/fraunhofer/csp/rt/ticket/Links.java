package com.fraunhofer.csp.rt.ticket;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Majid Salehi on 4/8/17.
 */
public class Links {
	private static final Logger LOG = LoggerFactory.getLogger(Links.class);

	@Key("id")
	private String ticketid;
	@Key("Members")
	private List<String> members;

	public Links() {
		this.members = new ArrayList<String>();
	}

	public String getId() {
		return this.ticketid;
	}

	public List<String> getMembers() {
		return this.members;
	}

	@Override
	public String toString() {
		return String.format("Links for %s, number %d", getId(), (null == getMembers() ? 0 : getMembers().size()));
	}

	public void addLink(final String strLink) {
		this.members.add(strLink);
	}

	public List<String> getMembersExtern(final String strProtocol) {
		List<String> rv = null;
		do {
			if (null == strProtocol || 0 == strProtocol.length()) {
				LOG.debug("wrong input parameter: <strProtocol>");
				break;
			}
			if (null == this.members) {
				LOG.warn("no links found at all");
				break;
			}
			rv = new ArrayList<String>(this.members.size());
			for (ListIterator<String> iter = this.members.listIterator(); iter.hasNext();) {
				final String strTmp = iter.next();
				rv.add(strTmp.replace("fsck.com-rt", strProtocol));
			}
		} while (false);

		return rv;
	}
}
