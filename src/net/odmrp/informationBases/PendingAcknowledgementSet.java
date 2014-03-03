package net.odmrp.informationBases;

import java.net.InetAddress;
import java.util.HashMap;

public class PendingAcknowledgementSet {

	private HashMap<MulticastSession, PendingTuple> _repository;
	
	public PendingAcknowledgementSet() {
		_repository = new HashMap<MulticastSession, PendingTuple>();
	}
	
	public PendingTuple getTuple(InetAddress groupAddress,
			InetAddress sourceAddress) {
		PendingTuple tuple = _repository.get(new MulticastSession(groupAddress, sourceAddress));
		if (tuple.isExpired()) {
			_repository.remove(tuple);
			return null;
		} else {
			return tuple;
		}
	}
	
	public void addTuple(InetAddress groupAddress,
			InetAddress sourceAddress,
			int sequenceNumber,
			InetAddress nextHopAddress,
			long expirationTime) {
		MulticastSession session = new MulticastSession(groupAddress, sourceAddress);
		PendingTuple tuple = new PendingTuple(session, 
				sequenceNumber, 
				nextHopAddress, 
				0, 
				expirationTime);
		_repository.put(session, tuple);
	}
	
}
