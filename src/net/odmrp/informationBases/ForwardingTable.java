package net.odmrp.informationBases;

import java.net.InetAddress;
import java.util.HashMap;

public class ForwardingTable {
	
	protected HashMap<InetAddress, ForwardingTuple> _repository;

	public ForwardingTable() {
		_repository = new HashMap<InetAddress, ForwardingTuple>();
	}
	
	/**
	 * Fetch the tuple corresponding to the given address. Return null
	 * if no such tuple exists or if the corresponding tuple was expired
	 * @param sourceAddress
	 * @return
	 */
	public ForwardingTuple getTuple(InetAddress sourceAddress) {
		ForwardingTuple tuple = _repository.get(sourceAddress);
		if (tuple.isExpired()) {
			_repository.remove(tuple); // Call remove on the object or on the key?
			return null;
		} else {
			return tuple;
		}
	}
	
	/**
	 * Add or update the tuple corresponding to the given address
	 * @param sourceAddress
	 * @param sequenceNumber
	 * @param expirationTime
	 */
	public void addTuple(InetAddress sourceAddress, 
			int sequenceNumber, 
			long expirationTime) {
		ForwardingTuple tuple = new ForwardingTuple(sourceAddress, 
				sequenceNumber, 
				expirationTime);
		_repository.put(sourceAddress, tuple);
	}
	
}
