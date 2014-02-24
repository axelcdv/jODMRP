package net.odmrp.constants;


public class Constants {

	/**
	 * Packet format
	 */
	
	public static byte RFC5444_VERSION = 0;
	
	// RFC5444 Message types
	public static final byte JOINQUERY_TYPE = (byte) 127;
	public static final byte JOINREPLY_TYPE = (byte) 128;

	public static final int DEFAULT_IPV6_SCOPE = 3;
	
	public static final int DEFAULT_PORT = 1212;
	
	public static final byte[] GROUP_ADDRESS_BYTES = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,13};
	public static final String GROUP_ADDRESS_STRING = "255.255.255.255";//"ff02::1"; 
}
