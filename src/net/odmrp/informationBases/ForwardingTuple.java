package net.odmrp.informationBases;

import java.net.InetAddress;

public class ForwardingTuple {
	
	public InetAddress multicastSourceAddress;
	public int sequenceNumber;
	public long expirationTime;
	
	public ForwardingTuple(InetAddress multicastSourceAddress,
			int sequenceNumber,
			long expirationTime) {
		this.multicastSourceAddress = multicastSourceAddress;
		this.sequenceNumber = sequenceNumber;
		this.expirationTime = expirationTime;
	}
	
	public boolean isExpired() {
		return this.expirationTime > System.currentTimeMillis();
	}

}
