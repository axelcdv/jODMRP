package test.odmrp.messaging;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

import java.net.Inet6Address;
import java.net.UnknownHostException;

import net.odmrp.constants.Constants;
import net.odmrp.messaging.JoinQuery;

import org.junit.BeforeClass;
import org.junit.Test;

public class JoinQueryTest {

	private static Inet6Address _sourceAddress;
	private static Inet6Address _groupAddress;
	private static int _sequenceNumber;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
			
			_sequenceNumber = 12;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void encodeDecode() {
		try {
			JoinQuery joinQuery = new JoinQuery(_sourceAddress, _groupAddress, _sequenceNumber);
			byte[] jqBytes = joinQuery.toBytes();
			assertArrayEquals("Encoding then decoding should give back the same JQ",
					jqBytes,
					new JoinQuery(jqBytes, 0).toBytes());
		} catch (Exception e) {
			e.printStackTrace();
			fail("error: " + e);
		}
	}

}
