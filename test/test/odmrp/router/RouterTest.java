package test.odmrp.router;

import static org.junit.Assert.fail;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import net.odmrp.constants.Constants;
import net.odmrp.messaging.JoinQuery;
import net.odmrp.messaging.Packet;
import net.odmrp.router.Router;

import org.junit.BeforeClass;
import org.junit.Test;

import test.odmrp.comm.MockReceiver;
import test.odmrp.comm.MockSender;

public class RouterTest {
	
	private static InetAddress _ownAddress;
	private static InetAddress _groupAddress;
	private static Router _router;
	private static MockSender _mockSender;
//	private static MockReceiver _mockReceiver;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		_ownAddress = InetAddress.getByName("130.129.155.106");
		
		_groupAddress = InetAddress.getByName(Constants.GROUP_ADDRESS_STRING);
//		_mockSender = new MockSender(Constants.DEFAULT_PORT, _groupAddress);
//		_mockReceiver = new MockReceiver(Constants.DEFAULT_PORT, router)
		
		_router = new Router(_ownAddress,
				MockSender.class,
				MockReceiver.class);
		
		_mockSender = (MockSender)_router.getSender();
		
		_router.initialize();
	}

	@Test
	public void testProcessJoinQuery() {
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("IOException");
		}
	}

}
