package test.odmrp.router;

import java.net.InetAddress;

import net.odmrp.com.Receiver;
import net.odmrp.com.SenderInterface;
import net.odmrp.informationBases.ForwardingTable;
import net.odmrp.informationBases.MulticastRoutingSet;
import net.odmrp.informationBases.PendingAcknowledgementSet;
import net.odmrp.informationBases.PreAcknowledgementSet;
import net.odmrp.router.Router;

/**
 * Router subclass that exposes its internal fields for testing purposes. 
 * @author Axel Colin de Verdière
 *
 */
public class RevealingRouter extends Router {

	public RevealingRouter(InetAddress ownAddress,
			Class<? extends SenderInterface> senderClass,
			Class<? extends Receiver> receiverClass) throws Exception {
		super(ownAddress, senderClass, receiverClass);
	}
	
	public ForwardingTable getForwardingTable() {
		return _forwardingTable;
	}
	
	public MulticastRoutingSet getMulticastRoutingSet() {
		return _multicastRoutingSet;
	}
	
	public PendingAcknowledgementSet getPendingAcknowledgementSet() {
		return _pendingAckSet;
	}
	
	public PreAcknowledgementSet getPreAcknowledgementSet() {
		return _preAckSet;
	}

}
