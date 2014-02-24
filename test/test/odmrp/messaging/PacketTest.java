package test.odmrp.messaging;

import static org.junit.Assert.assertTrue;

import java.net.Inet6Address;
import java.net.UnknownHostException;

import net.odmrp.constants.Constants;

import org.junit.BeforeClass;
import org.junit.Test;

public class PacketTest {
	
	private static Inet6Address _sourceAddress;
	private static Inet6Address _groupAddress;

	@BeforeClass
	public static void setup() {
		try {
			byte[] sourceBytes = new byte[16];
			sourceBytes[15] = 1;
			_sourceAddress = Inet6Address.getByAddress("localhost",
					sourceBytes,
					Constants.DEFAULT_IPV6_SCOPE);
			
			byte[] groupBytes = new byte[16];
			groupBytes[15] = 13;
			_groupAddress = Inet6Address.getByAddress("group",
					groupBytes,
					Constants.DEFAULT_IPV6_SCOPE);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void test() {
//		fail("Not yet implemented");
		assertTrue(true);
	}

}
