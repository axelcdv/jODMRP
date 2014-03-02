package net.odmrp.informationBases;

import java.net.InetAddress;

public class MulticastSession {
	
	public InetAddress groupAddress;
	public InetAddress sourceAddress;
	
	public MulticastSession(InetAddress groupAddress,
			InetAddress sourceAddress) {
		this.groupAddress = groupAddress;
		this.sourceAddress = sourceAddress;
		
		// DEBUG
		System.out.println("New multicast session, hash code: " + this.hashCode());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!obj.getClass().equals(this.getClass())) {
			System.out.println("Not same class");
			return false;
		}
		MulticastSession session = (MulticastSession)obj;
		System.out.println("session : " + session + ", this: " + this);
		return session.groupAddress.equals(this.groupAddress) &&
				session.sourceAddress.equals(this.sourceAddress);
	}
	
	@Override
	public int hashCode() {
		return (groupAddress.hashCode() << 8) ^ (sourceAddress.hashCode());
//		return groupAddress.hashCode() + sourceAddress.hashCode();
	}
	
	@Override
	public String toString() {
		return super.toString() + groupAddress + sourceAddress;
	}

}
