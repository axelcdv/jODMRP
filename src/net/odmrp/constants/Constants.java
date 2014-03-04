package net.odmrp.constants;


public class Constants {

	/**
	 * Packet format
	 */
	
	public static byte RFC5444_VERSION = 0;
	
	// RFC5444 Message types
	public static final byte JOINQUERY_TYPE = (byte) 127;
	public static final byte JOINREPLY_TYPE = (byte) 128;
	
	// Message-type-specific Address block TLV types
	public static final byte JOINQUERY_ADDR_TYPE_TYPE = (byte)0;
	public static final byte JOINREPLY_ADDR_TYPE_TYPE = (byte)0;
	
	// JQ-Specific Address block type extension for ADDR-TYPE 
	public static final byte JQ_MULTICAST_GROUP_ADDRESS_TYPE = 0;
	
	// JR-Specific Address block type extension for ADDR-TYPE
	public static final byte JR_MULTICAST_GROUP_ADDRESS_TYPE = 0;
	public static final byte JR_NEXT_HOP_ADDRESS_TYPE = 1;

	public static final int DEFAULT_IPV6_SCOPE = 3;
	
	public static final int DEFAULT_PORT = 1212;
	
	public static final byte[] GROUP_ADDRESS_BYTES = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,13};
	public static final String GROUP_ADDRESS_STRING = "255.255.255.255";//"ff02::1"; 
	
	// Well-known addresses
	public static final String LL_MANET_ROUTERS_V4 = "224.0.0.109";
	public static final String LL_MANET_ROUTERS_v6 = "FF02:0:0:0:0:0:0:6D";
	
	// Time constants
	public static final long SECOND = 1000;
	public static final long MINUTE = 60 * SECOND;
	public static final long HOUR = 60 * MINUTE;
	public static final long DAY = 24 * HOUR;
	
	// Default timeouts
	public static final long ROUTING_TIMEOUT = 1 * MINUTE; 
	public static final long FG_TIMEOUT = 2 * MINUTE;
	public static final long ACK_TIMEOUT = 30 * SECOND;
	public static final long PRE_ACK_TIMEOUT = 30 * SECOND;
}
