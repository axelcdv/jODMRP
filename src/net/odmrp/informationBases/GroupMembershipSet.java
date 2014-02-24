package net.odmrp.informationBases;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.logging.Logger;

public class GroupMembershipSet {

	protected Logger _logger;
	protected HashSet<InetAddress> _groups;
	
	public GroupMembershipSet() {
		_groups = new HashSet<InetAddress>();
		
		_logger = Logger.getLogger(this.getClass().getName());
	}
	
	public void addGroup(InetAddress groupAddress) {
		_groups.add(groupAddress);
	}
	
	public boolean isMemberOf(InetAddress groupAddress) {
		return _groups.contains(groupAddress);
	}
	
	public void removeGroup(InetAddress groupAddress) {
		_groups.remove(groupAddress);
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer(super.toString());
		buf.append("\tMember of: ");
		for (InetAddress address : _groups) {
			buf.append(address);
			buf.append(",");
		}
		return buf.toString();
	}
}
