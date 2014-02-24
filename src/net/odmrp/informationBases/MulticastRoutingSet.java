package net.odmrp.informationBases;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.logging.Logger;

public class MulticastRoutingSet {
	
	protected HashMap<InetAddress, MulticastRoutingTuple> _repository;
	protected Logger _logger;
	
	public MulticastRoutingSet() {
		_repository = new HashMap<InetAddress, MulticastRoutingTuple>();
		
		_logger = Logger.getLogger(this.getClass().getName());
	}

	public void addTuple(MulticastRoutingTuple t) {
		_logger.info("Adding or updating tuple: " + t);
		_repository.put(t.sourceAddress, t);
	}
	
	public MulticastRoutingTuple findTuple(InetAddress sourceAddress) {
		MulticastRoutingTuple tuple = _repository.get(sourceAddress);
		return tuple != null && tuple.expirationTime > System.currentTimeMillis() ? tuple : null;
	}
}
