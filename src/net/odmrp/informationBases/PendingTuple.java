package net.odmrp.informationBases;

import java.net.InetAddress;


public class PendingTuple {
	
	public MulticastSession multicastSession;
	public int sequenceNumber;
	public InetAddress nextHopAddress;
	public int transmissionCounter; // P_nth_time
	public long expirationTime;
	public boolean acknowledged;
	
	public PendingTuple(MulticastSession multicastSession, 
			int sequenceNumber,
			InetAddress nextHopAddress, 
			int retransmissionCounter,
			long expirationTime, 
			boolean acknowledged) {
		super();
		this.multicastSession = multicastSession;
		this.sequenceNumber = sequenceNumber;
		this.nextHopAddress = nextHopAddress;
		this.transmissionCounter = retransmissionCounter;
		this.expirationTime = expirationTime;
		this.acknowledged = acknowledged;
	}

	public PendingTuple(MulticastSession multicastSession, int sequenceNumber,
			InetAddress nextHopAddress, int retransmissionCounter,
			long expirationTime) {
		super();
		this.multicastSession = multicastSession;
		this.sequenceNumber = sequenceNumber;
		this.nextHopAddress = nextHopAddress;
		this.transmissionCounter = retransmissionCounter;
		this.expirationTime = expirationTime;
		this.acknowledged = false;
	}
	
	public boolean isExpired() {
		return this.expirationTime < System.currentTimeMillis();
	}

}
