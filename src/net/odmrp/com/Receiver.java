package net.odmrp.com;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

import net.odmrp.exceptions.NotSupportedException;
import net.odmrp.exceptions.PacketFormatException;
import net.odmrp.messaging.Packet;
import net.odmrp.router.Router;

public class Receiver extends Thread{

	protected Router _router;
	private MulticastSocket _socket;
	protected volatile boolean _isRunning;
	
	public Receiver(int port, Router router) throws IOException {
		_socket = new MulticastSocket(port);
		_router = router;
		_isRunning = false;
	}
	
	public void run() {
		if (_isRunning) {
			return;
		}
		_isRunning = true;
		DatagramPacket p;
		while(_isRunning) {
			p = new DatagramPacket(new byte[3000], 3000);
			try {
				_socket.receive(p);
				Packet packet = new Packet(p.getData());
				_router.handlePacket(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (PacketFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void setRunning(boolean running) {
		_isRunning = running;
	}
}
