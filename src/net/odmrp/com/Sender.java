package net.odmrp.com;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

import net.odmrp.messaging.Packet;

public class Sender {
	
	private MulticastSocket _socket;
	
	public Sender(int port) throws IOException {
		_socket = new MulticastSocket(port);
	}
	
	public void send(Packet packet) throws IOException {
		byte[] payload = packet.toBytes();
		_socket.send(new DatagramPacket(payload, payload.length));
	}

}
