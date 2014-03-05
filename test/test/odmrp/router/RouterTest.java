package test.odmrp.router;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import net.odmrp.constants.Constants;
import net.odmrp.exceptions.PacketFormatException;
import net.odmrp.informationBases.ForwardingTuple;
import net.odmrp.informationBases.MulticastRoutingTuple;
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
	 * PendingAckSet, Blacklist)
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
	
	@Test
	public void testForwardingTable() {
		fail("TODO");
	}
	
	@Test
	public void testPendingAckSet() {
		fail("TODO");
	}
	
	@Test
	public void testPreAckSet() {
		fail("TODO");
	}

}
