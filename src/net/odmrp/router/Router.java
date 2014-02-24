package net.odmrp.router;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import net.odmrp.com.Receiver;
import net.odmrp.com.Sender;
import net.odmrp.constants.Constants;
import net.odmrp.messaging.JoinQuery;
import net.odmrp.messaging.Packet;

public class Router {
	
	private Logger _logger;
	protected Sender _sender;
	protected Receiver _receiver;
	
	public Router() throws Exception {
		_logger = Logger.getLogger(Router.class.getName());
		InetAddress groupAddress = InetAddress.getByName(Constants.GROUP_ADDRESS_STRING);
		_sender = new Sender(Constants.DEFAULT_PORT, groupAddress);
		_receiver = new Receiver(Constants.DEFAULT_PORT, this);
	}
	
	public void handlePacket(Packet p) {
		_logger.info(p.toString());
	}
	
	public void initialize() {
//		_receiver.start();
		byte[] sourceAddr = new byte[16];
		sourceAddr[15] = 1;
		byte[] groupAddr = new byte[16];
		groupAddr[15] = 12;
		
		JoinQuery jq = null;
		
		try {
			jq = new JoinQuery(InetAddress.getByName("::1"), 
					InetAddress.getByName("ff02::1"), 12);
			
			System.out.println(jq);
			byte[] toBytes = jq.toBytes();
			byte[] payload = new byte[1 + toBytes.length];
			payload[0] = (byte)0; // RFC5444 Version
			for (int i = 1; i < payload.length; i++) {
				payload[i] = toBytes[i - 1];
			}
			_sender.send(new Packet(payload));
			
			_receiver.start();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try {
			Router router = new Router();
			router.initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		byte[] sourceAddr = new byte[16];
//		sourceAddr[15] = 1;
//		
//		byte[] groupAddr = new byte[16];
//		groupAddr[15] = 12;
//		
//		JoinQuery jq = null;
//		
//		try {
//			jq = new JoinQuery(Inet6Address.getByAddress("localhost",
//					sourceAddr,
//					Constants.DEFAULT_IPV6_SCOPE), 
//					Inet6Address.getByAddress("group", 
//							groupAddr, 
//							Constants.DEFAULT_IPV6_SCOPE), 12);
//			
//			System.out.println(jq);
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			
//			return;
//		}
//		
//		try {
//			JoinQuery other = new JoinQuery(jq.toBytes(), 0);
//			System.out.println(jq.toBytes().equals(other.toBytes()));
//			System.out.println(other);
//			
//			byte[] jqBytes = jq.toBytes();
//			byte[] otherBytes = other.toBytes();
//			
//			System.out.println("Length: " + jqBytes.length + ", " + otherBytes.length);
//			
//			int numDiff = 0;
//			for (int i = 0; i < jqBytes.length; i++) {
//				if (jqBytes[i] != otherBytes[i]) {
//					numDiff++;
//					System.out.println(i + " is different");
//				}
//			}
//			
//			System.out.println(numDiff + " differences");
//			
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (NotSupportedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (PacketFormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
