package test.odmrp.router;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import net.odmrp.constants.Constants;
import net.odmrp.exceptions.PacketFormatException;
import net.odmrp.informationBases.ForwardingTuple;
import net.odmrp.informationBases.MulticastRoutingTuple;
import net.odmrp.informationBases.OverheardTuple;
import net.odmrp.informationBases.PendingTuple;
import net.odmrp.messaging.JoinQuery;
import net.odmrp.messaging.JoinReply;
import net.odmrp.messaging.Packet;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.odmrp.comm.MockReceiver;
import test.odmrp.comm.MockSender;

/**
 * Test class for Router and Router state (information sets).
 * @author Axel Colin de Verdire
 *
 */
public class RouterTest {
	
	/**
	 * TODO:
	 * - Setup/tear down Router before/after each test case => DONE
	 * - Add test cases to check that the information sets are being filled and
	 * emptied correctly (FT, MulticastRoutingSet, PreAckSet, 
	 * PendingAckSet, Blacklist) => filled: done, except blacklist
	 * 
	 */
	
	private static InetAddress _ownAddress;
	private static InetAddress _groupAddress;
	private static RevealingRouter _router;
	private static MockSender _mockSender;
//	private static MockReceiver _mockReceiver;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		_ownAddress = InetAddress.getByName("130.129.155.106");
		
		_groupAddress = InetAddress.getByName(Constants.GROUP_ADDRESS_STRING);
	}

	@Before
	public void setUp() throws Exception {
		_router = new RevealingRouter(_ownAddress,
				MockSender.class,
				MockReceiver.class);
		
		_mockSender = (MockSender)_router.getSender();
		
		_router.initialize();
	}
	
	/**
	 * Create a Join Query based on the given parameters and simulate its
	 * reception by the router.
	 * @param sourceAddressString
	 * @param neighborAddressString
	 * @param sequenceNumber
	 * @param simulateJR 		true to also simulate the reception of a JR
	 * @param receiverAddress	Address of the (virtual) Router that sends the
	 * 							Join Reply. Should be null if simulateJR is 
	 * 							false.
	 * @param nextHopAddress	Address of the next hop contained in the JR.
	 * 							Should be null if simulateJR is false.
	 * @throws UnknownHostException
	 */
	public void simulateReception(InetAddress sourceAddress,
			InetAddress neighborAddress,
			int sequenceNumber,
			boolean simulateJR,
			InetAddress receiverAddress,
			InetAddress nextHopAddress) throws UnknownHostException, PacketFormatException {
		// Create a JQ from sourceAddress transmitted by neighborAddress
		JoinQuery jq = new JoinQuery(sourceAddress, 
				_groupAddress, 
				sequenceNumber);
		Packet p = new Packet(jq);

		// Simulate reception of the JQ
		_router.handlePacket(p, neighborAddress);
		
		if (simulateJR) {
			JoinReply jr = new JoinReply(jq, nextHopAddress);
			Packet jrPkt = new Packet(jr);
			
			_router.handlePacket(jrPkt, receiverAddress);
		}
	}
	
	@Test
	public void testForwardJoinQuery() {
		assertEquals(MockSender.class, _router.getSender().getClass());
		try {
			InetAddress randomAddress = InetAddress.getByName("130.129.155.127");
			int sequenceNumber = 42;
			JoinQuery jq = new JoinQuery(randomAddress, 
					_groupAddress, 
					sequenceNumber);
			Packet p = new Packet(jq);
			System.out.println("Packet: " + p);
			_router.handlePacket(p, randomAddress);
			
			assertEquals(p, _mockSender.getLastSentPacket());
		} catch(UnknownHostException e) {
			e.printStackTrace();
			fail("UnknownHostException");
		}
	}
	
	@Test
	public void testForwardJoinReply() {
		try {
			InetAddress sourceAddress = InetAddress.getByName("130.129.155.120");
			InetAddress receiverAddress = InetAddress.getByName("130.129.155.121");

			// Simulate reception of a JQ from the source to establish correct state
			JoinQuery jq = new JoinQuery(sourceAddress, _groupAddress, 42);
			_router.handlePacket(new Packet(jq), sourceAddress);

			// Simulate reception of a JR from a receiver
			JoinReply jr = new JoinReply(jq, _ownAddress);
			_router.handlePacket(new Packet(jr), receiverAddress);

			JoinReply forwardedJR = new JoinReply(jq, sourceAddress);
			
			assertEquals(new Packet(forwardedJR),
					_mockSender.getLastSentPacket());
		} catch(UnknownHostException e) {
			e.printStackTrace();
			fail("UnknownHostException");
		} catch (PacketFormatException e) {
			e.printStackTrace();
			fail("PacketFormatException");
		}
	}
	
	/**
	 * Test that the Multicast Routing set is correctly populated by the
	 * reception of a JQ.
	 */
	@Test
	public void testMulticastRoutingSet() {
		try {
			// Create a JQ from sourceAddress transmitted by neighborAddress
			InetAddress sourceAddress = InetAddress.getByName("130.129.155.119");
			InetAddress neighborAddress = InetAddress.getByName("130.129.155.127");
			int sequenceNumber = 42;
			JoinQuery jq = new JoinQuery(sourceAddress, 
					_groupAddress, 
					sequenceNumber);
			Packet p = new Packet(jq);
			
			// Simulate reception of the JQ
			_router.handlePacket(p, neighborAddress);
			
			MulticastRoutingTuple mRoutingTuple = _router.getMulticastRoutingSet().findTuple(sourceAddress);

			// Test if the Multicast Routing set is correctly populated
			assertNotNull(mRoutingTuple);
			assertEquals("The Multicast Routing tuple should have the correct next hop", 
					neighborAddress, 
					mRoutingTuple.nextHopAddress);
			assertEquals("The Multicast Routing tuple should have the correct sequence number", 
					sequenceNumber, 
					mRoutingTuple.sequenceNumber);
			assertEquals("The Multicast Routing tuple should have the correct source", 
					sourceAddress, 
					mRoutingTuple.sourceAddress);
		} catch(UnknownHostException e) {
			e.printStackTrace();
			fail("UnknownHostException");
		}		
	}
	
	/**
	 * Test that the Forwarding Table is correctly populated by the reception
	 * of a JR with this router as a next hop.
	 */
	@Test
	public void testForwardingTable() {
		try {
			// Setup addresses and sequence number
			InetAddress sourceAddress = InetAddress.getByName("130.129.155.119");
			InetAddress neighborAddress = InetAddress.getByName("130.129.155.127");
			InetAddress receiverAddress = InetAddress.getByName("130.129.155.117");
			int sequenceNumber = 42;
			
			// Simulate reception of both a Join Query and a corresponding JR
			// with this router as next hop
			simulateReception(sourceAddress, 
					neighborAddress, 
					sequenceNumber, 
					true, 
					receiverAddress, 
					_ownAddress);
			
			ForwardingTuple fTuple = _router.getForwardingTable().getTuple(_groupAddress, sourceAddress);
			
			assertNotNull("A forwarding tuple should have been setup by the Join Reply", 
					fTuple);
			assertEquals("The multicast group address should match the Join Reply's", 
					_groupAddress, 
					fTuple.multicastGroupAddress);
			assertEquals("The multicast source address should match the Join Reply's", 
					sourceAddress, 
					fTuple.multicastSourceAddress);
			assertEquals("The sequence number should match the Join Reply's", 
					sequenceNumber, 
					fTuple.sequenceNumber);
		} catch(UnknownHostException e) {
			e.printStackTrace();
			fail("UnknownHostException");
		} catch (PacketFormatException e) {
			e.printStackTrace();
			fail("PacketFormatException");
		}
	}
	
	/**
	 * Test that the Pending Ack set is correctly populated when sending a JR 
	 * that hasn't been pre-acknowledged.
	 */
	@Test
	public void testPendingAckSet() {
		try {
			// Setup addresses and sequence number
			InetAddress sourceAddress = InetAddress.getByName("130.129.155.119");
			InetAddress neighborAddress = InetAddress.getByName("130.129.155.127");
			InetAddress receiverAddress = InetAddress.getByName("130.129.155.117");
			int sequenceNumber = 42;

			// Simulate reception of both a Join Query and a corresponding JR
			// with this router as next hop
			simulateReception(sourceAddress, 
					neighborAddress, 
					sequenceNumber, 
					true, 
					receiverAddress, 
					_ownAddress);
			
			PendingTuple pTuple = _router.getPendingAcknowledgementSet()
					.getTuple(_groupAddress, sourceAddress);
			
			assertNotNull("A pending tuple should have been setup by the Join Reply", 
					pTuple);
			assertEquals("The multicast group address should match the Join Reply's", 
					_groupAddress, 
					pTuple.multicastSession.groupAddress);
			assertEquals("The multicast source address should match the Join Reply's", 
					sourceAddress, 
					pTuple.multicastSession.sourceAddress);
			assertEquals("The sequence number should match the Join Reply's", 
					sequenceNumber, 
					pTuple.sequenceNumber);
			assertFalse("The tuple should not be acknowledged", 
					pTuple.acknowledged);
			assertEquals("The number of transmissions should be 1", 
					1, pTuple.transmissionCounter);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			fail(e.toString());
		} catch (PacketFormatException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	/**
	 * Test that the Pre Ack set is correctly populated by the reception of a
	 * JR that does not list this router as the next hop.
	 */
	@Test
	public void testPreAckSet() {
		try {
			// Setup addresses and sequence number
			InetAddress sourceAddress = InetAddress.getByName("130.129.155.119");
			InetAddress neighborAddress = InetAddress.getByName("130.129.155.127");
			InetAddress receiverAddress = InetAddress.getByName("130.129.155.117");
			InetAddress nextHopAddress = InetAddress.getByName("130.129.155.65");
			int sequenceNumber = 42;

			// Simulate reception of both a Join Query and a corresponding JR
			// with this router as next hop
			simulateReception(sourceAddress, 
					neighborAddress, 
					sequenceNumber, 
					true, 
					receiverAddress, 
					nextHopAddress);
			
			OverheardTuple oTuple = _router.getPreAcknowledgementSet()
					.getTuple(_groupAddress, sourceAddress);
			
			assertNotNull("An overheard tuple should have been setup by the Join Reply", 
					oTuple);
			assertEquals("The multicast group address should match the Join Reply's", 
					_groupAddress, 
					oTuple.session.groupAddress);
			assertEquals("The multicast source address should match the Join Reply's", 
					sourceAddress,
					oTuple.session.sourceAddress);
			assertEquals("The sequence number should match the Join Reply's", 
					sequenceNumber, 
					oTuple.sequenceNumber);
			assertEquals("The originator address should match the one that was setup",
					receiverAddress,
					oTuple.originatorAddress);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			fail(e.toString());
		} catch (PacketFormatException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

}
