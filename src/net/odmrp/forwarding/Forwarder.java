package net.odmrp.forwarding;

import java.io.IOException;
import java.util.logging.Logger;

import net.odmrp.com.Sender;
import net.odmrp.messaging.Message;
import net.odmrp.messaging.Packet;

public class Forwarder {
	
	protected Sender _sender;
	protected Logger _logger;
	
	public Forwarder(Sender s) {
		_sender = s;
		_logger = Logger.getLogger(this.getClass().getName());
	}
	
	public void forwardPacket(Packet p) {
		try {
			_sender.send(p);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			_logger.warning("IOException while sending packet: " + p + "\nException: " + e);
			e.printStackTrace();
		}
	}

	/**
	 * Forward a Message; encapsulates it first in a Packet.
	 * Overriding this method could allow the forwarder to decide to aggregate Messages
	 * and/or piggyback control message transmissions.
	 * @param m
	 */
	public void forwardMessage(Message m) {
		try {
			_sender.send(new Packet(m));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			_logger.warning("IOException while sending message: " + m + "\nException: " + e);
			e.printStackTrace();
		}
	}
}
