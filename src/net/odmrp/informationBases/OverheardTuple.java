package net.odmrp.informationBases;

import java.net.InetAddress;

public class OverheardTuple {

	public MulticastSession session;
	public int sequenceNumber;
	public InetAddress originatorAddress;
	public long expirationTime;
	
	/**
	 * 
	 * @param session
	 * @param sequenceNumber
	 * @param originatorAddress
	 * @param expirationTime
	 */
	public OverheardTuple(MulticastSession session, 
			int sequenceNumber,
			InetAddress originatorAddress, 
			long expirationTime) {
		super();
		this.session = session;
		this.sequenceNumber = sequenceNumber;
		this.originatorAddress = originatorAddress;
		this.expirationTime = expirationTime;
	}
	
	/**
	 * 
	 * @param groupAddress
	 * @param sourceAddress
	 * @param sequenceNumber
	 * @param originatorAddress
	 * @param expirationTime
	 */
	public OverheardTuple(InetAddress groupAddress,
			InetAddress sourceAddress,
			int sequenceNumber,
			InetAddress originatorAddress, 
			long expirationTime) {
		super();
		this.session = new MulticastSession(groupAddress, sourceAddress);
		this.sequenceNumber = sequenceNumber;
		this.originatorAddress = originatorAddress;
		this.expirationTime = expirationTime;
	}
	
	public boolean isExpired() {
		return expirationTime < System.currentTimeMillis();
	}
}
