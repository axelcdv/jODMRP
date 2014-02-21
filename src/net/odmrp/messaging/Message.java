package net.odmrp.messaging;

import java.net.Inet6Address;
import java.net.UnknownHostException;

import net.odmrp.constants.*;
import net.odmrp.exceptions.NotSupportedException;
import net.odmrp.exceptions.PacketFormatException;

public class Message {
	
	/**
	 *  Size of the Message, in bytes
	 */
	public int size;
	
	public int type;
	
	public Message() {
		
	}
	
	public Message(byte[] payload, int start) {
		
	}
	
	/**
	 * 
	 * @param fromArray
	 * @param toArray
	 * @param start
	 */
	public void encodeAddress(byte[] fromArray, byte[] toArray, int start) {
		for (int i = 0; i < fromArray.length; i++) {
			toArray[start + i] = fromArray[i];
		}
	}

	// STATIC METHODS
	
	/**
	 * Constructor method
	 * @param payload
	 * @param start
	 * @return
	 * @throws PacketFormatException
	 * @throws NotSupportedException 
	 * @throws UnknownHostException 
	 */
	public static Message Parse(byte[] payload, int start) throws PacketFormatException, UnknownHostException, NotSupportedException {
		switch (payload[start]) {
		case Constants.JOINQUERY_TYPE:
			return new JoinQuery(payload, start);
		case Constants.JOINREPLY_TYPE:
			return new JoinReply(payload, start);
		default:
			throw new PacketFormatException("Unknown packet type: " + payload[start]);
		}
	}
}
