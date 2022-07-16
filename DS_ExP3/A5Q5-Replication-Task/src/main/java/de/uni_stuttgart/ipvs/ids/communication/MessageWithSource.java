package de.uni_stuttgart.ipvs.ids.communication;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.Vector;

public class MessageWithSource<MessageType> {
	
	final MessageType message;
	final SocketAddress source;
	
	public MessageWithSource(SocketAddress source, MessageType message) {
		this.source = source;
		this.message = message;
	}
	
	public MessageType getMessage() {
		return message;
	}

	public SocketAddress getSource() {
		return source;
	}
	
	public static <MessageType> Collection<SocketAddress> getSources(Collection<MessageWithSource<MessageType>> messagesWithSource) {
		if (messagesWithSource == null) {
			return null;
		}
		
		Collection<SocketAddress> sources = new Vector<SocketAddress>(messagesWithSource.size());
		for (MessageWithSource<MessageType> messageWithSource: messagesWithSource) {
			sources.add(messageWithSource.getSource());
		}
		return sources;
	}

}
