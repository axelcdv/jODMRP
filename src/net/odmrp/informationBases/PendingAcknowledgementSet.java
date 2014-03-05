package net.odmrp.informationBases;

import java.net.InetAddress;
import java.util.HashMap;

public class PendingAcknowledgementSet {

	private HashMap<MulticastSession, PendingTuple> _repository;
	
	public PendingAcknowledgementSet() {
		_repository = new HashMap<MulticastSession, PendingTuple>();
	}
	
	/**
	 * Find a tuple corresponding to the given session (group & source addresses).
	 * Return null if no such tuple exists or if it was expired (and hence removed).
	 * @param groupAddress
	 * @param sourceAddress
	 * @return
	 */
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
	
	/**
	 * Add a PendingTuple corresponding to the given parameters.
	 * TODO: setup a timer corresponding to the expiration of this tuple.
	 * If not cancelled, the timer should trigger adding a tuple to the 
	 * Blacklist.
	 * @param groupAddress
	 * @param sourceAddress
	 * @param sequenceNumber
	 * @param nextHopAddress
	 * @param expirationTime
	 */
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
	 * Update the corresponding tuple, i.e., replace the tuple corresponding
	 * to the same Session in the information set by the one passed as a
	 * parameter.
	 * @param tuple
	 */
	public void updateTuple(PendingTuple tuple) {
		_repository.put(tuple.multicastSession, tuple);
	}
	
	/**
	 * TODO: acknowledge tuple: remove tuple and cancel corresponding timer.
	 * @param tuple
	 */
	public void acknowledgeTuple(PendingTuple tuple) {
		// TODO: implement
		_repository.remove(tuple.multicastSession);
	}
}
