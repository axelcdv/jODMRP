package net.odmrp.messaging;

import java.net.Inet6Address;
import java.net.UnknownHostException;
import java.util.Arrays;

import net.odmrp.exceptions.NotSupportedException;
import net.odmrp.exceptions.PacketFormatException;

public class JoinQuery extends Message {

	// For now, supports only IPv6 addresses
	
	/**
	 * Length of an address, in bytes. For now only supports 16 (IPv6)
	 */
	private int _addressLength;
	private Inet6Address _sourceAddress;
	private Inet6Address _groupAddress;
	private int _sequenceNumber;
	
	
	public JoinQuery(byte[] payload, int start) throws NotSupportedException, PacketFormatException, UnknownHostException {
		super(payload, start);
		_addressLength = (payload[++start] & 0x0F) + 1;
		if (_addressLength != 16) {
			throw new NotSupportedException("Only IPv6 addresses are supported");
		}
		
		// TODO: check message length and flags
		start = start + 3;
		
		// TODO: get correct scope
		_sourceAddress = Inet6Address.getByAddress("",
				Arrays.copyOfRange(payload, start, start += _addressLength), 0);
		
		_sequenceNumber = (int)(payload[start] << 8) + (int)(payload[++start]);
		
		start = start + 4; // TLVs length + Num addrs + Flags
		
		_groupAddress = Inet6Address.getByAddress("",
				Arrays.copyOfRange(payload, start, start += _addressLength),
				0);
	}

}
