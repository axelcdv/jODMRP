package net.odmrp.messaging;

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
	
	public Packet(byte[] payload) throws Exception {
		// TODO: Parse the payload into an RFC5444 Packet
		if (payload[0] != Constants.RFC5444_VERSION) {
			throw new PacketFormatException("Incorrect version or flags");
		}
		int start = 1;
		Message message;
		_messages = new LinkedList<Message>();
		
		while (start < payload.length) {
			message = new Message(payload, start);
			start += message.size;
			_messages.add(message);
		}
	}
}
