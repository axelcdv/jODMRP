package net.odmrp.informationBases;

import java.net.InetAddress;
import java.util.HashMap;

public class ForwardingTable {
	
	protected HashMap<MulticastSession, ForwardingTuple> _repository;

	public ForwardingTable() {
		_repository = new HashMap<MulticastSession, ForwardingTuple>();
	}
	
	/**
	 * Fetch the tuple corresponding to the given group and source addresses. 
	 * Return null if no such tuple exists or if the corresponding 
	 * tuple was expired
	 * @param groupAddress
	 * @param sourceAddress
	 * @return
	 */
	public ForwardingTuple getTuple(InetAddress groupAddress,
			InetAddress sourceAddress) {
		ForwardingTuple tuple = _repository.get(
				new MulticastSession(groupAddress, 
						sourceAddress));
		if (tuple != null && tuple.isExpired()) {
			_repository.remove(tuple); // Call remove on the object or on the key?
			return null;
		} else {
			return tuple;
		}
	}
	
	/**
	 * Add or update the tuple corresponding to the given address
	 * @param groupAddress
	 * @param sourceAddress
	 * @param sequenceNumber
	 * @param expirationTime
	 */
	public void addTuple(InetAddress groupAddress,
			InetAddress sourceAddress, 
			int sequenceNumber, 
			long expirationTime) {
		ForwardingTuple tuple = new ForwardingTuple(groupAddress,
				sourceAddress, 
				sequenceNumber, 
				expirationTime);
		MulticastSession key = new MulticastSession(groupAddress, 
				sourceAddress);
		_repository.put(key, 
				tuple);
	}
	
}
