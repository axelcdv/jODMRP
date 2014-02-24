package net.odmrp.com;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;

import net.odmrp.messaging.Packet;

public class Sender {
	
	private MulticastSocket _socket;
	private InetAddress _groupAddress;
	private int _port;
	
	public Sender(int port, InetAddress groupAddress) throws IOException {
			_socket = new MulticastSocket(new InetSocketAddress(InetAddress.getByName("::"), port));
			
			_port = port;
			_groupAddress = groupAddress;
	}
	
	public void send(Packet packet) throws IOException {
		byte[] payload = packet.toBytes();
		System.out.println("Sending packet: " + packet + ", length: " + packet.toBytes().length);
		DatagramPacket dp = new DatagramPacket(payload, payload.length, _groupAddress, _port);
		_socket.send(dp);
	}

}
