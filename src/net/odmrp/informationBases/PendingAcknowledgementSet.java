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
		if (tuple != null && tuple.isExpired()) {
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
		// TODO: start timer
		MulticastSession session = new MulticastSession(groupAddress, sourceAddress);
		PendingTuple tuple = new PendingTuple(session, 
				sequenceNumber, 
				nextHopAddress, 
				1, 
				expirationTime);
		_repository.put(session, tuple);
	}
	
	/**
	 * 
	 * @param tuple
	 */
	public void updateTuple(PendingTuple tuple) {
		_repository.put(tuple.multicastSession, tuple);
	}
	
	/**
	 * TODO: acknowledge tuple: remove tuple and corresponding timer
	 * @param tuple
	 */
	public void acknowledgeTuple(PendingTuple tuple) {
		// TODO: implement
	}
}
