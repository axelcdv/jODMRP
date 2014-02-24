package test.odmrp.messaging;

import static org.junit.Assert.*;

import java.net.InetAddress;

import net.odmrp.messaging.JoinReply;

import org.junit.BeforeClass;
import org.junit.Test;

public class JoinReplyTest {
	
	private static InetAddress _sourceAddress;
	private static InetAddress _groupAddress;
	private static InetAddress _nextHopAddress;
	private static JoinReply _joinReply;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		_sourceAddress = InetAddress.getByName("::12");
		_groupAddress = InetAddress.getByName("ff02::1");
		_nextHopAddress = InetAddress.getByName("::13");
		_joinReply = new JoinReply(_sourceAddress, 
				_groupAddress, 
				12, 
				_nextHopAddress, 
				false);
	}

	@Test
	public void testEqual() throws Exception {
		JoinReply jr = new JoinReply(InetAddress.getByName("::12"), 
				InetAddress.getByName("ff02::1"), 
				12, 
				InetAddress.getByName("::13"), 
				false);
		assertEquals("Two Join Replies with the same fields should be equal", 
				_joinReply, jr);
	}
	
	@Test
	public void testNotEqual() throws Exception {
		JoinReply jr = new JoinReply(_sourceAddress, 
				_groupAddress, 
				15, 
				_nextHopAddress, 
				false);
		assertNotSame("Two Join Replies with at least one differing field should not be equal", 
				_joinReply, jr);
	}
	
	@Test
	public void testEncode() throws Exception {
		System.out.println("Source address: " + _sourceAddress.getAddress());
		
		assertEquals("Encoding then decoding a Join Reply should give back an object with the same fields",
				_joinReply, new JoinReply(_joinReply.toBytes(), 0));
	}
}
