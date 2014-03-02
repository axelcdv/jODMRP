package test.odmrp.informationBases;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.*;

import java.net.InetAddress;

import net.odmrp.constants.Constants;
import net.odmrp.informationBases.ForwardingTable;
import net.odmrp.informationBases.ForwardingTuple;

import org.junit.BeforeClass;
import org.junit.Test;

public class ForwardingTableTest {
	
	private static ForwardingTable _forwardingTable;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		_forwardingTable = new ForwardingTable();
		
		_forwardingTable.addTuple(InetAddress.getByName("224.0.0.1"),
				InetAddress.getByName("192.168.1.18"),
				9,
				System.currentTimeMillis() + 5 * Constants.MINUTE);
		_forwardingTable.addTuple(InetAddress.getByName("224.0.0.1"),
				InetAddress.getByName("192.168.1.20"),
				52,
				System.currentTimeMillis() + 3 * Constants.MINUTE);
		_forwardingTable.addTuple(InetAddress.getByName("255.255.255.255"),
				InetAddress.getByName("192.168.1.18"),
				12,
				System.currentTimeMillis() + 6 * Constants.MINUTE);
		_forwardingTable.addTuple(InetAddress.getByName("255.255.255.255"), 
				InetAddress.getByName("192.168.1.120"), 
				14, 
				System.currentTimeMillis() - 10 * Constants.SECOND);
		
	}

	@Test
	public void testDifferentGroupAddress() throws Exception {
		ForwardingTuple tupleA = _forwardingTable.getTuple(InetAddress.getByName("224.0.0.1"),
				InetAddress.getByName("192.168.1.18"));
		ForwardingTuple tupleB = _forwardingTable.getTuple(InetAddress.getByName("255.255.255.255"),
				InetAddress.getByName("192.168.1.18"));
		System.out.println(tupleA);
		assertNotNull(tupleA);
		assertNotNull(tupleB);
		assertEquals(InetAddress.getByName("224.0.0.1"), tupleA.multicastGroupAddress);
		assertEquals(InetAddress.getByName("255.255.255.255"), tupleB.multicastGroupAddress);
	}

}
