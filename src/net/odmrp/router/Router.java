package net.odmrp.router;

import java.net.Inet6Address;
import java.net.UnknownHostException;

import net.odmrp.constants.Constants;
import net.odmrp.exceptions.NotSupportedException;
import net.odmrp.exceptions.PacketFormatException;
import net.odmrp.messaging.JoinQuery;

public class Router {
	
	public static void main(String[] args) {
		byte[] sourceAddr = new byte[16];
		sourceAddr[15] = 1;
		
		byte[] groupAddr = new byte[16];
		groupAddr[15] = 12;
		
		JoinQuery jq = null;
		
		try {
			jq = new JoinQuery(Inet6Address.getByAddress("localhost",
					sourceAddr,
					Constants.DEFAULT_IPV6_SCOPE), 
					Inet6Address.getByAddress("group", 
							groupAddr, 
							Constants.DEFAULT_IPV6_SCOPE), 12);
			
			System.out.println(jq);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return;
		}
		
		try {
			JoinQuery other = new JoinQuery(jq.toBytes(), 0);
			System.out.println(jq.toBytes().equals(other.toBytes()));
			System.out.println(other);
			
			byte[] jqBytes = jq.toBytes();
			byte[] otherBytes = other.toBytes();
			
			System.out.println("Length: " + jqBytes.length + ", " + otherBytes.length);
			
			int numDiff = 0;
			for (int i = 0; i < jqBytes.length; i++) {
				if (jqBytes[i] != otherBytes[i]) {
					numDiff++;
					System.out.println(i + " is different");
				}
			}
			
			System.out.println(numDiff + " differences");
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PacketFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
