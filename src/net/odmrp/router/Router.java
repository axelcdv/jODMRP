package net.odmrp.router;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import net.odmrp.com.Receiver;
import net.odmrp.com.Sender;
import net.odmrp.constants.Constants;
import net.odmrp.exceptions.PacketFormatException;
import net.odmrp.forwarding.Forwarder;
import net.odmrp.informationBases.Blacklist;
import net.odmrp.informationBases.ForwardingTable;
import net.odmrp.informationBases.ForwardingTuple;
import net.odmrp.informationBases.GroupMembershipSet;
import net.odmrp.informationBases.MulticastRoutingSet;
import net.odmrp.informationBases.MulticastRoutingTuple;
import net.odmrp.messaging.JoinQuery;
import net.odmrp.messaging.JoinReply;
import net.odmrp.messaging.Message;
import net.odmrp.messaging.Packet;

public class Router {
	
	private Logger _logger;
	protected InetAddress _ownAddress;
	protected Sender _sender;
	protected Receiver _receiver;
	protected Forwarder _forwarder;
	
	// Information bases
	protected GroupMembershipSet _groupMembershipSet;
	protected MulticastRoutingSet _multicastRoutingSet;
	protected ForwardingTable _forwardingTable;
	protected Blacklist _blacklist;
	
	public Router() throws Exception {
		_logger = Logger.getLogger(Router.class.getName());
		
		_ownAddress = InetAddress.getByName("localhost");
		InetAddress groupAddress = InetAddress.getByName(Constants.GROUP_ADDRESS_STRING);
		_sender = new Sender(Constants.DEFAULT_PORT, groupAddress);
		_receiver = new Receiver(Constants.DEFAULT_PORT, this);
		_forwarder = new Forwarder(_sender);
		
		// Set up information bases
		_multicastRoutingSet = new MulticastRoutingSet();
		_groupMembershipSet = new GroupMembershipSet();
		_forwardingTable = new ForwardingTable();
		_blacklist = new Blacklist();
	}
	
	// Setters and getters
	
	public InetAddress getOwnAddress() {
		return _ownAddress;
	}
	
	// Handle Packets and Messages
	
	public void handlePacket(Packet p, InetAddress fromAddress) {
		_logger.info(p.toString());
		for (Message m : p.getMessages()) {
			handleMessage(m, fromAddress);
		}
	}
	
	public void handleMessage(Message m, InetAddress fromAddress) {
		switch (m.getType()) {
		case Constants.JOINQUERY_TYPE:
			handleJoinQuery((JoinQuery)m, fromAddress);
			break;
			
		case Constants.JOINREPLY_TYPE:
			handleJoinReply((JoinReply)m, fromAddress);
			break;

		default:
			_logger.warning("Invalid message type received: " + m.getType());
			break;
		}
	}
	
	/**
	 * Process a Join Query message according to Section 9.
	 * @param jq
	 */
	public void handleJoinQuery(JoinQuery jq, InetAddress fromAddress) {
		
		_logger.info("Handling Join Query from: " + fromAddress);
		
		// Find out if the tuple is valid (9.1)
		// TODO: Check address length
		if (_blacklist.isBlacklisted(fromAddress)) {
			_logger.info("Dropping Join Query from blacklisted address: " + fromAddress);
			return;
		}
		MulticastRoutingTuple matchingTuple = 
				_multicastRoutingSet.findTuple(jq.getSourceAddress());
		if (matchingTuple != null && 
				matchingTuple.sequenceNumber >= jq.getSequenceNumber()) {
			// 9.1: The Multicast Routing set contains a tuple for which [...]
			_logger.info("Dropping old Join Query, sequence number: " +
					jq.getSequenceNumber());
			return;
		}
		
		// Create or update the matching routing tuple
		matchingTuple = new MulticastRoutingTuple(jq.getSourceAddress(), 
				fromAddress, 
				jq.getSequenceNumber(),
				System.currentTimeMillis() + Constants.ROUTING_TIMEOUT);
		_multicastRoutingSet.addTuple(matchingTuple);
		
		// Consider the message for forwarding
		_forwarder.forwardPacket(new Packet(jq));
		
		// 9.1.3.5.  If this Router is a member of the Multicast Group, addressed by
		// JQ.MulticastGroupAddress
		if (_groupMembershipSet.isMemberOf(jq.getGroupAddress())) {
			// create a new Join Reply according to
			//  Section 9.2 and transmit it to all of this Router's neighbors
			_logger.info("This router is a member of: " + jq.getGroupAddress() + 
					", generating a Join Reply in return");
			generateJoinReply(jq, fromAddress);
		}
	}
	
	public void handleJoinReply(JoinReply jr, InetAddress fromAddress) {
		// TODO: implement
		_logger.info("Handling Join Reply from: " + fromAddress);
		// 9.2.1 Invalid join replies
		// TODO: Check address length
		ForwardingTuple matchingTuple = _forwardingTable.getTuple(jr.getMulticastGroupAddress(),
					jr.getSourceAddress());
		if (matchingTuple != null && matchingTuple.sequenceNumber > jr.getSequenceNumber()) {
			_logger.info("Dropping older Join Reply with outdated sequence number: " + jr);
			return;
		}
		
		// 9.2.3 Processing
		// 1. If JR.NextHop is an address of this router
		if (jr.getNextHopAddress().equals(getOwnAddress())) {
			boolean newJr = false;
			// 1.1. Find the matching forwarding tuple
			if (matchingTuple == null) {
				// 1.2. set new-jr to TRUE
				newJr = true;
				// Create the matching FG tuple
				_forwardingTable.addTuple(jr.getMulticastGroupAddress(),
					jr.getSourceAddress(),
					jr.getSequenceNumber(), 
					System.currentTimeMillis() + Constants.FG_TIMEOUT);
			} else {
				// 1.3. new-jr is set to TRUE if JR.SequenceNumber > F_seq_num
				newJr = jr.getSequenceNumber() > matchingTuple.sequenceNumber;
				// Update the matching FG tuple
				if (newJr) {
					_forwardingTable.addTuple(jr.getMulticastGroupAddress(),
							jr.getSourceAddress(),
							jr.getSequenceNumber(), 
							System.currentTimeMillis() + Constants.FG_TIMEOUT);
				}
			}
			// 1.4. If new-jr == TRUE or ackRequired is set, consider the JR
			// for forwarding
			if (newJr || jr.isAckRequired()) {
				handleJoinReplyForwarding(jr);
			}
		} else {
			// 2. Else, find the matching multicast routing tuple
			MulticastRoutingTuple mRoutingTuple = _multicastRoutingSet.findTuple(jr.getSourceAddress());
			if (mRoutingTuple == null) {
				_logger.warning("Error: received a Join Reply not related to existing routing tuple");
				return;
			}
		}
	}
	
	/**
	 * Update the Join Reply and pass it to the forwarder for transmission
	 * @param jr
	 */
	public void handleJoinReplyForwarding(JoinReply jr) {
		_forwarder.forwardMessage(jr);
	}
	
	public void generateJoinReply(JoinQuery matchingQuery, InetAddress nextHop) {
		// TODO: handle acknowledgements/pre-acknowledgements
		try {
			JoinReply jr = new JoinReply(matchingQuery, nextHop);
			_forwarder.forwardMessage(jr);
		} catch (PacketFormatException e) {
			_logger.warning("Exception while generating a Join Reply: " + e);
			e.printStackTrace();
		}
	}
	
	public void initialize() {
		_receiver.start();
	}
	
	public void generateJoinQuery() {
		_logger.info("Generating Join Query for group: 255.255.255.255");
		try {
			JoinQuery jq = new JoinQuery(_ownAddress,
					InetAddress.getByName("255.255.255.255"),
					0);
			_sender.send(new Packet(jq));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setMemberOf(InetAddress groupAddress) {
		_groupMembershipSet.addGroup(groupAddress);
	}
	
	public static void main(String[] args) {
		try {
			Router router = new Router();
			router.initialize();
			router.setMemberOf(InetAddress.getByName("255.255.255.255"));
			router.setMemberOf(InetAddress.getByName("ff02::1"));
			router.generateJoinQuery();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
