package test.odmrp.comm;

import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Logger;

import net.odmrp.com.Sender;
import net.odmrp.messaging.Packet;

public class MockSender extends Sender {
	
	private Packet _lastSentPacket;

	public MockSender(int port, InetAddress groupAddress) throws IOException {
		super(port, groupAddress);
		
		_logger = Logger.getLogger(MockSender.class.getName());
	}
	
	@Override
	public void send(Packet p) throws IOException {
		_logger.info("Logging sending packet: " + p);
		
		_lastSentPacket = p;
	}
	
	public Packet getLastSentPacket() {
		return _lastSentPacket;
	}

}
