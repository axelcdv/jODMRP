package test.odmrp.informationBases;

import static org.junit.Assert.*;

import java.net.InetAddress;

import net.odmrp.informationBases.MulticastSession;

import org.junit.BeforeClass;
import org.junit.Test;

public class MulticastSessionTest {

	private static MulticastSession _multicastSession;
	private static InetAddress _groupAddress;
	private static InetAddress _sourceAddress;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		_multicastSession = new MulticastSession(InetAddress.getByName("224.0.0.1"),
				InetAddress.getByName("192.168.1.120"));
		_groupAddress = InetAddress.getByName("224.0.0.1");
		_sourceAddress = InetAddress.getByName("192.168.1.120");
	}

	@Test
	public void testEquals() throws Exception {
		MulticastSession otherSession = new MulticastSession(InetAddress.getByName("224.0.0.1"),
				InetAddress.getByName("192.168.1.120"));
		assertEquals(_groupAddress, InetAddress.getByName("224.0.0.1"));
		assertEquals(_sourceAddress, InetAddress.getByName("192.168.1.120"));
		assertEquals(_multicastSession, otherSession);
		
		MulticastSession differentSession = new MulticastSession(InetAddress.getByName("224.0.0.1"),
				InetAddress.getByName("192.168.1.20"));
		assertTrue(!differentSession.equals(_multicastSession));
	}

}
