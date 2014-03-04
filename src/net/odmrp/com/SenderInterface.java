package net.odmrp.com;

import java.io.IOException;

import net.odmrp.messaging.Packet;

public interface SenderInterface {

	public void send(Packet p) throws IOException;
}
