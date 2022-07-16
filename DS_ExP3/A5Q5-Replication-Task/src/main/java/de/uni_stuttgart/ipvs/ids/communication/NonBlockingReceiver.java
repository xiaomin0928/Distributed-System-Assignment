package de.uni_stuttgart.ipvs.ids.communication;

import de.uni_stuttgart.ipvs.ids.replication.VersionedValue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

/**
 * Part b) Extend the method receiveMessages to return all DatagramPackets that
 * were received during the given timeout.
 * 
 * Also implement unpack() to conveniently convert a Collection of
 * DatagramPackets containing ValueResponseMessages to a collection of
 * VersionedValueWithSource objects.
 * 
 */
public class NonBlockingReceiver {

	protected DatagramSocket socket;

	private final int MAX_LEN = 1024; //the length of receive buffer

	public NonBlockingReceiver(DatagramSocket socket) {
		this.socket = socket;
	}

	/**
	 * @method receiveMessages
	 * @discription receive messages without blocking
	 * @param timeoutMillis: the time of timeout
	 * @param expectedMessages: the number of expected messages
	 * @return revList: the list of received messages
	 */
	public Vector<DatagramPacket> receiveMessages(int timeoutMillis, int expectedMessages)
			throws IOException {
		// TODO: Impelement me!
		Vector<DatagramPacket> revList = new Vector<DatagramPacket>();
		//set socket timeout
		this.socket.setSoTimeout(timeoutMillis);
        //limit the number of receive messages
		for(int i = 0; i<expectedMessages; i++ )
		{
			byte[] receiveBuff = new byte[MAX_LEN];
			DatagramPacket datagram = new DatagramPacket(receiveBuff, MAX_LEN);
			try {
				this.socket.receive(datagram);
			} catch (SocketTimeoutException e) {
				//time out
				break;

			}
			revList.add(datagram);
		}
		return revList;
	}

	/**
	 * @method unpack
	 * @discription get objects(messageWithSource) from datagram packet
	 * @param packetCollection: the datagram packet of socket
	 * @return messageWithSourceColl: the list of objects(messageWithSource)
	 */
	public static <T> Collection<MessageWithSource<T>> unpack(
			Collection<DatagramPacket> packetCollection) throws IOException,
			ClassNotFoundException {
		// TODO: Impelement me!
		Collection<MessageWithSource<T>> messageWithSourceColl = new ArrayList<MessageWithSource<T>>();
		Object[] objs = packetCollection.toArray();
		for( int i = 0; i<objs.length; i++)
		{
			//get objects from datagram packet
			DatagramPacket packet = (DatagramPacket)objs[i];
			byte[] receiveBuff = packet.getData();
			ByteArrayInputStream bint = new ByteArrayInputStream(receiveBuff);
			ObjectInputStream oint = new ObjectInputStream(bint);
			T objPacket = (T)oint.readObject();
            //create messageWithSource with objects from datagram packet
			MessageWithSource<T> messageWithSource = new MessageWithSource<T>(packet.getSocketAddress(),objPacket);
			messageWithSourceColl.add(messageWithSource);
            //close input stream
			bint.close();
			oint.close();
		}

		return messageWithSourceColl;
	}
	
}
