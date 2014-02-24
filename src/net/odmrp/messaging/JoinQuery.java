package net.odmrp.messaging;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import net.odmrp.constants.Constants;
import net.odmrp.exceptions.NotSupportedException;
import net.odmrp.exceptions.PacketFormatException;

public class JoinQuery extends Message {

	// For now, supports only IPv6 addresses
	
	/**
	 * Length of an address, in bytes. For now only supports 16 (IPv6)
	 */
	private int _addressLength;
	private int _messageLength;
	private InetAddress _sourceAddress;
	private InetAddress _groupAddress;
	private int _sequenceNumber;
	
	
	public JoinQuery(byte[] payload, int start) throws NotSupportedException, PacketFormatException, UnknownHostException {
		super();
		
		System.out.println(payload[0] + ", " + payload[1]);
		
		_addressLength = (payload[++start] & 0x0F) + 1;
		if (_addressLength != 16) {
			throw new NotSupportedException("Only IPv6 addresses are supported: " + _addressLength);
		}
		
		// TODO: check message length and flags
		_messageLength = (int)(payload[++start] << 8) + (int)(payload[++start]);
		
		if (payload.length - start + 3 < _messageLength) {
			throw new PacketFormatException("Incorrect message length");
		}
		
		// TODO: get correct scope
		_sourceAddress = Inet6Address.getByAddress("",
				Arrays.copyOfRange(payload, ++start, start += _addressLength), 
				Constants.DEFAULT_IPV6_SCOPE);
		
		_sequenceNumber = (int)(payload[start] << 8) + (int)(payload[++start]);
		
		start = start + 4; // TLVs length + Num addrs + Flags
		
		// Multicast group address
		System.out.println("Getting multicast group address, pointer: " + start);
		_groupAddress = Inet6Address.getByAddress("",
				Arrays.copyOfRange(payload, ++start, start += _addressLength),
				Constants.DEFAULT_IPV6_SCOPE);
	}
	
	public JoinQuery(InetAddress sourceAddress, InetAddress groupAddress, int sequenceNumber) {
		super();
		_addressLength = 16;
		_sourceAddress = sourceAddress;
		_groupAddress = groupAddress;
		_sequenceNumber = sequenceNumber;
		_messageLength = 15 + 2 * _addressLength;
	}

	public byte[] toBytes() {
		byte[] encoding = new byte[_messageLength];
		
		int pointer = 0;
		
		// Message Type
		encoding[pointer] = Constants.JOINQUERY_TYPE;
		
		// Flags + Message address length
		byte flagAddrLength = (byte)(((1 << 3 + 1) << 4) + _addressLength - 1);
		encoding[++pointer] = flagAddrLength;
		
		// Message length (2 bytes)
		encoding[++pointer] = (byte)(_messageLength >> 8);
		encoding[++pointer] = (byte)(_messageLength & 0xFF);
		
		// Source address
		pointer++;
		encodeAddress(_sourceAddress.getAddress(),
				encoding, pointer);
		pointer += _addressLength;
		
		// Sequence number (2 bytes)
		encoding[pointer] = (byte)(_sequenceNumber >> 8);
		encoding[++pointer] = (byte)(_sequenceNumber & 0xFF);
		
		// TLVs Length = 0 (2 bytes)
		encoding[++pointer] = 0;
		encoding[++pointer] = 0;
		
		// Num addresses (1 byte)
		encoding[++pointer] = (byte)1;
		
		// Flags (1 byte)
		encoding[++pointer] = 0;
		
		System.out.println("Before multicast group address, pointer = " + pointer);
		
		// Multicast group address
		pointer++;
		encodeAddress(_groupAddress.getAddress(),
				encoding,
				pointer);
		pointer += _addressLength;
		
		// Address TLV block length (2 bytes)
		encoding[pointer] = 0;
		encoding[++pointer] = 3;
		
		System.out.println("After tlv block length, pointer = " + pointer);
		
		// Address TLV Type
		encoding[++pointer] = 0;
		
		// Flags
		encoding[++pointer] = (byte)(1 << 7);
		
		// TLV Type extension
		encoding[++pointer] = 0;
		
		return encoding;
	}

	// Getters and setters
	
	public int getAddressLength() {
		return _addressLength;
	}

	public void setAddressLength(int _addressLength) {
		this._addressLength = _addressLength;
	}

	@Override
	public int getMessageLength() {
		return _messageLength;
	}

	// TODO: shouldn't be necessary
	public void setMessageLength(int _messageLength) {
		this._messageLength = _messageLength;
	}

	public InetAddress getSourceAddress() {
		return _sourceAddress;
	}

	public void setSourceAddress(Inet6Address _sourceAddress) {
		this._sourceAddress = _sourceAddress;
	}

	public InetAddress getGroupAddress() {
		return _groupAddress;
	}

	public void setGroupAddress(Inet6Address _groupAddress) {
		this._groupAddress = _groupAddress;
	}

	public int getSequenceNumber() {
		return _sequenceNumber;
	}

	public void setSequenceNumber(int _sequenceNumber) {
		this._sequenceNumber = _sequenceNumber;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer(super.toString());
		buf.append("Join Query:");
		buf.append("\nAddress length: " + _addressLength);
		buf.append("\nSource Address: " + _sourceAddress.toString());
		buf.append("\nGroup address: " + _groupAddress.toString());
		buf.append("\nSequence number: " + _sequenceNumber);
		buf.append("\nMessage length: " + _messageLength);
		
		return buf.toString();
	}
}
