package net.odmrp.informationBases;

import java.net.InetAddress;

public class ForwardingTuple {
	
	public InetAddress multicastGroupAddress;
	public InetAddress multicastSourceAddress;
	public int sequenceNumber;
	public long expirationTime;
	
	public ForwardingTuple(InetAddress multicastGroupAddress,
			InetAddress multicastSourceAddress,
			int sequenceNumber,
			long expirationTime) {
		this.multicastGroupAddress = multicastGroupAddress;
		this.multicastSourceAddress = multicastSourceAddress;
		this.sequenceNumber = sequenceNumber;
		this.expirationTime = expirationTime;
	}
	
	public boolean isExpired() {
		return this.expirationTime < System.currentTimeMillis();
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer(super.toString());
		buf.append(":\nGroup address: " + multicastGroupAddress);
		buf.append("\nSource address: " + multicastSourceAddress);
		buf.append("\nSequence number: " + sequenceNumber);
		buf.append("\nExpiration time: " + expirationTime);
		
		return buf.toString();
	}

}
