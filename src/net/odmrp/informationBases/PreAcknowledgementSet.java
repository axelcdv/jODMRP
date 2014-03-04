package net.odmrp.informationBases;

import java.net.InetAddress;
import java.util.HashMap;

public class PreAcknowledgementSet {
	
	private HashMap<MulticastSession, OverheardTuple> _repository;
	
	public PreAcknowledgementSet() {
		_repository = new HashMap<MulticastSession, OverheardTuple>();
	}
	
	/**
	 * 
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
	 * 
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
	 * 
	 * @param tuple
	 */
	public void expireTuple(OverheardTuple tuple) {
		_repository.remove(tuple.session);
	}

}
