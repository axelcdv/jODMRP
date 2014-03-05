package net.odmrp.informationBases;

import java.net.InetAddress;
import java.util.HashMap;

public class PreAcknowledgementSet {
	
	private HashMap<MulticastSession, OverheardTuple> _repository;
	
	/**
	 * Constructor for the PreAck set.
	 */
	public PreAcknowledgementSet() {
		_repository = new HashMap<MulticastSession, OverheardTuple>();
	}
	
	/**
	 * Find a tuple corresponding to the given session (group & source addresses).
	 * Return null if no such tuple exists or if it was expired (and hence removed).
	 * @param groupAddress
	 * @param sourceAddress
	 * @return
	 */
	public OverheardTuple getTuple(InetAddress groupAddress,
			InetAddress sourceAddress) {
		MulticastSession key = new MulticastSession(groupAddress, sourceAddress);
		OverheardTuple tuple = _repository.get(key);
		if (tuple != null && tuple.isExpired()) {
			_repository.remove(key);
			return null;
		} else {
			return tuple;
		}
	}
	
	/**
	 * Add an Overheard tuple defined by the following parameters.
	 * @param groupAddress
	 * @param sourceAddress
	 * @param sequenceNumber
	 * @param originatorAddress
	 * @param expirationTime
	 */
	public void addTuple(InetAddress groupAddress,
			InetAddress sourceAddress,
			int sequenceNumber,
			InetAddress originatorAddress,
			long expirationTime) {
		MulticastSession session = new MulticastSession(groupAddress, sourceAddress);
		_repository.put(session, 
				new OverheardTuple(session, 
						sequenceNumber, 
						originatorAddress, 
						expirationTime));
	}
	
	/**
	 * Set the tuple as expired => remove it from the information set.
	 * @param tuple
	 */
	public void expireTuple(OverheardTuple tuple) {
		_repository.remove(tuple.session);
	}

}
