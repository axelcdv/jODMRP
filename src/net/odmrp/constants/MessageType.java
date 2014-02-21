package net.odmrp.constants;

public enum MessageType {
	JOINQUERY(127),
	JOINREPLY(128);
	
	private int value;
	
	private MessageType(int value) {
		this.value = value;
	}
}
