package net.odmrp.messaging;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import net.odmrp.constants.Constants;
import net.odmrp.exceptions.NotSupportedException;
import net.odmrp.exceptions.PacketFormatException;

public class JoinReply extends Message {
	
	// Fields
	private InetAddress _sourceAddress;
	private InetAddress _multicastGroupAddress;
	private InetAddress _nextHopAddress;
	private int _sequenceNumber;
	private boolean _ackRequired;
	
	// Computed
	private int _addressLength;

	/**
	 * Parse a byte buffer into a Join Reply message
	 * @param payload
	 * @param start
	 * @throws PacketFormatException 
	 * @throws UnknownHostException 
	 * @throws NotSupportedException 
	 */
	public JoinReply(byte[] payload, int start) throws PacketFormatException, UnknownHostException, NotSupportedException {
		super(payload, start);
		
		if (Constants.JOINREPLY_TYPE != (_type = payload[start])) {
			throw new PacketFormatException("Wrong packet type for Join Reply: " + payload[start]);
		}
		
		int pointer = start + 1;
		
		// Flags & address length
		long flagAddrLength = payload[pointer++] & 0xFF;
		System.out.println("Flagaddrlength: " + flagAddrLength);
		if (flagAddrLength >> 4 != (1 + (1 << 3))) {
			throw new PacketFormatException("Incorrect flags: " + (flagAddrLength >> 4) + ", should be: " + (1 + (1 << 3)));
		}
		_addressLength = (int) (flagAddrLength & 0x0F) + 1;
		System.out.println("Address length: " + _addressLength);
		
		_messageLength = (payload[pointer++] << 8) + payload[pointer++];
		
		_sourceAddress = InetAddress.getByAddress(Arrays.copyOfRange(payload, pointer, pointer + _addressLength));
		pointer += _addressLength;
		
		_sequenceNumber = (payload[pointer++] << 8) + payload[pointer++];
		
		int tlvsLength = (payload[pointer++] << 8) + payload[pointer++];
		if (tlvsLength != 0) {
			throw new NotSupportedException("TLVs not yet supported");
		}
		
		// Address blocks
		while(pointer - start < _messageLength) {
			int numAddrs = payload[pointer++];
			if (numAddrs > 1) {
				throw new PacketFormatException("Address blocks in Join Reply should contain only one address");
			}
			// Flags
			if (payload[pointer++] != 0) {
				throw new NotSupportedException("Only address flags 0000 are supported");
			}
			
			// Decode address
			InetAddress address = InetAddress.getByAddress(Arrays.copyOfRange(payload, 
					pointer, 
					pointer + _addressLength));
			pointer += _addressLength;
			
			// Address TLV block length
			if ((payload[pointer++] << 8) + payload[pointer++] != 3)  {
				throw new PacketFormatException("Wrong size: Address blocks in Join Reply should only contain one ADDR-TYPE TLV of size 3");
			}
			
			if (payload[pointer++] != Constants.JOINQUERY_ADDR_TYPE_TYPE) {
				throw new NotSupportedException("Wrong address block TLV type: " + payload[pointer - 1]);
			}
			if ((payload[pointer++] & 0xFF) != (1 << 7)) {
				throw new PacketFormatException("Wrong flags for address block TLV: " + payload[pointer - 1]);
			}
			switch (payload[pointer++]) {
			case Constants.JR_MULTICAST_GROUP_ADDRESS_TYPE:
				System.out.println("Decoded address: " + address + ", type: " + Constants.JR_MULTICAST_GROUP_ADDRESS_TYPE);
				_multicastGroupAddress = address;
				break;
				
			case Constants.JR_NEXT_HOP_ADDRESS_TYPE:
				System.out.println("Decoded address: " + address + ", type: " + Constants.JR_NEXT_HOP_ADDRESS_TYPE);
				_nextHopAddress = address;
				break;

			default:
				throw new PacketFormatException("Wrong or unsupported address type: " + payload[pointer - 1]);
			}
		}
	}
	
	/**
	 * Generates a Join Reply message from the following fields.
	 * @param sourceAddress
	 * @param multicastGroupAddress
	 * @param sequenceNumber
	 * @param nextHopAddress
	 * @param ackRequired
	 * @throws PacketFormatException
	 */
	public JoinReply(InetAddress sourceAddress,
			InetAddress multicastGroupAddress,
			int sequenceNumber,
			InetAddress nextHopAddress,
			boolean ackRequired) throws PacketFormatException {
		_addressLength = sourceAddress.getAddress().length;
		
		if (multicastGroupAddress.getAddress().length != _addressLength ||
				nextHopAddress.getAddress().length != _addressLength) {
			throw new PacketFormatException("Incompatible address lengths");
		}
		
		_messageLength = 4 + 6 + 7 + 5 + 3 * _addressLength;
		
		_type = Constants.JOINREPLY_TYPE;
		_sourceAddress = sourceAddress;
		_multicastGroupAddress = multicastGroupAddress;
		_sequenceNumber = sequenceNumber;
		_nextHopAddress = nextHopAddress;
		_ackRequired = ackRequired;	
	}
	
	public JoinReply(JoinQuery jq, InetAddress nextHopAddress) throws PacketFormatException {
		this(jq.getSourceAddress(),
				jq.getGroupAddress(),
				jq.getSequenceNumber(),
				nextHopAddress,
				false);
	}
	
	// Getters and Setters
	
	public InetAddress getSourceAddress() {
		return _sourceAddress;
	}

	public void setSourceAddress(InetAddress _sourceAddress) {
		this._sourceAddress = _sourceAddress;
	}

	public InetAddress getMulticastGroupAddress() {
		return _multicastGroupAddress;
	}

	public void setMulticastGroupAddress(InetAddress _multicastGroupAddress) {
		this._multicastGroupAddress = _multicastGroupAddress;
	}

	public InetAddress getNextHopAddress() {
		return _nextHopAddress;
	}

	public void setNextHopAddress(InetAddress _nextHopAddress) {
		this._nextHopAddress = _nextHopAddress;
	}

	public int getSequenceNumber() {
		return _sequenceNumber;
	}

	public void setSequenceNumber(int _sequenceNumber) {
		this._sequenceNumber = _sequenceNumber;
	}

	public boolean isAckRequired() {
		return _ackRequired;
	}

	public void setAckRequired(boolean _ackRequired) {
		this._ackRequired = _ackRequired;
	}
	
	// Encoding

	@Override
	public byte[] toBytes() {
		byte[] encoded = new byte[getMessageLength()];
		int pointer = 0;
		
		// Type
		encoded[pointer++] = Constants.JOINREPLY_TYPE;
		
		// Flags + address length
		encoded[pointer++] =  (byte)(_addressLength - 1 + ((1 + (1 << 3)) << 4));
		System.out.println("Encoded flags + address length: " + (encoded[pointer - 1] & 0xFF));
		
		// Message length
		encoded[pointer++] = (byte)(getMessageLength() >> 8);
		encoded[pointer++] = (byte)(getMessageLength());
		
		// Multicast source address
		encodeAddress(_sourceAddress.getAddress(),
				encoded,
				pointer);
		pointer += _addressLength;
		
		// Sequence number
		encoded[pointer++] = (byte)(_sequenceNumber >> 8);
		encoded[pointer++] = (byte)(_sequenceNumber);
		
		// TLVs length
		encoded[pointer++] = encoded[pointer++] = 0;

		int type = 0;
		// Address blocks
		for (byte[] addr : new byte[][] {_multicastGroupAddress.getAddress(), _nextHopAddress.getAddress()}) {
			// Num addrs
			encoded[pointer++] = 1;
			
			// Flags
			encoded[pointer++] = 0;
			
			// Multicast group address
			encodeAddress(addr, 
					encoded, 
					pointer);
			pointer += _addressLength;
			
			// Address block tlv
			// Length
			encoded[pointer++] = 0;
			encoded[pointer++] = 3;
			
			// Type
			encoded[pointer++] = Constants.JOINREPLY_ADDR_TYPE_TYPE;
			
			// Flags
			encoded[pointer++] = (byte)(1 << 7);
			
			// TLV type extension
			encoded[pointer++] = type == 0 ? 
					Constants.JR_MULTICAST_GROUP_ADDRESS_TYPE : 
						Constants.JR_NEXT_HOP_ADDRESS_TYPE;
			
			System.out.println("Encoded address: " + addr + ", type: " + encoded[pointer - 1]);
			type++;
		}
		
		return encoded;
	}

	@Override
	public boolean equals(Object obj) {
		if (!obj.getClass().equals(this.getClass())) {
			return false;
		}
		JoinReply jr = (JoinReply)obj;
		
		return _ackRequired == jr.isAckRequired() &&
				_messageLength == jr.getMessageLength() &&
				_multicastGroupAddress.equals(jr.getMulticastGroupAddress()) &&
				_sourceAddress.equals(jr.getSourceAddress()) &&
				_nextHopAddress.equals(jr.getNextHopAddress()) &&
				_sequenceNumber == jr.getSequenceNumber();
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer(super.toString());
		buf.append("Join Reply (" + _type + "):");
		buf.append("\nAddress length: " + _addressLength);
		buf.append("\nSource Address: " + _sourceAddress.toString());
		buf.append("\nMulticast group address: " + _multicastGroupAddress.toString());
		buf.append("\nNext hop address: " + _nextHopAddress.toString());
		buf.append("\nSequence number: " + _sequenceNumber);
		buf.append("\nMessage length: " + _messageLength);
		return buf.toString();
	}
}
