package net.odmrp.informationBases;

import java.net.InetAddress;

public class MulticastRoutingTuple {

	public InetAddress sourceAddress;
	public InetAddress nextHopAddress;
	public int sequenceNumber;
	public long expirationTime;
	
	public MulticastRoutingTuple(InetAddress sourceAddress,
			InetAddress nextHopAddress,
			int sequenceNumber,
			long expirationTime) {
		this.sourceAddress = sourceAddress;
		this.nextHopAddress = nextHopAddress;
		this.sequenceNumber = sequenceNumber;
		this.expirationTime = expirationTime;
	}
	
	public String toString() {
		String s = super.toString();
		return s + "R_Source: " + sourceAddress + " | R_Next_Hop: " + nextHopAddress +
				" | R_Sequence_Number: " + sequenceNumber + 
				" | R_Exp_Time: " + expirationTime;
	}
}
