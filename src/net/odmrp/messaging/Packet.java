package net.odmrp.messaging;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.odmrp.constants.Constants;
import net.odmrp.exceptions.PacketFormatException;

/**
 * Class representing an RFC5444 Packet
 * @author axelcdv
 *
 */
public class Packet {
	
	private List<Message> _messages;
	
	/**
	 * Generates a Packet containing a single Message m
	 * @param m
	 */
	public Packet(Message m) {
		_messages = new LinkedList<Message>();
		_messages.add(m);
	}
	
	public Packet(byte[] payload) throws Exception {
		this(payload, payload.length);
	}
	
	/**
	 * Parse the payload with given length into an RFC5444 packet
	 * NB: only support Join Query message format for now
	 * @param payload
	 * @param length
	 * @throws Exception
	 */
	public Packet(byte[] payload, int length) throws Exception {
		if (payload[0] != Constants.RFC5444_VERSION) {
			throw new PacketFormatException("Incorrect version or flags");
		}
		int start = 1;
		Message message;
		_messages = new LinkedList<Message>();
		
		while (start < length) {
			message = Message.Parse(payload, start);
			System.out.println("Parsed message: " + message + ", length: " + message.getMessageLength());
			start += message.getMessageLength();
			_messages.add(message);
		}
	}
	
	public List<Message> getMessages() {
		return _messages;
	}
	
	/**
	 * Encode the Packet in a byte array
	 * @return
	 */
	public byte[] toBytes() {
		byte[] result;
		byte[][] messageBytes = new byte[_messages.size()][];
		int length = 1;
		int i = 0;
		for (Message m : _messages) {
			messageBytes[i] = m.toBytes();
			length += messageBytes[i].length;
			i++;
		}
		result = new byte[length];
		result[0] = 0;
		
		length = 1;
		for (int j = 0; j < messageBytes.length; j++) {
			byte[] bs = messageBytes[j];
			for (byte b : bs) {
				result[length++] = b;
			}
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer(super.toString());
		buf.append("\n" + _messages.size() + " message(s)");
		for (Message m : _messages) {
			buf.append("\nMessage: " + m.toString());
		}
		return buf.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!obj.getClass().equals(this.getClass())) {
			return false;
		}
		Packet p = (Packet)obj;
		if (_messages.size() != p.getMessages().size()) {
			return false;
		}
		Iterator<Message> pIterator = p.getMessages().iterator();
		for (Iterator<Message> iterator = _messages.iterator(); iterator.hasNext() && pIterator.hasNext();) {
			if (!((Message)iterator.next()).equals((Message)pIterator.next())) {
				return false;
			}
		}
		return true;
	}
}
