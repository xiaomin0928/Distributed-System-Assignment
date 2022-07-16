package de.uni_stuttgart.ipvs.ids.replication;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import de.uni_stuttgart.ipvs.ids.communication.MessageWithSource;
import de.uni_stuttgart.ipvs.ids.communication.NonBlockingReceiver;
import de.uni_stuttgart.ipvs.ids.communication.ReadRequestMessage;
import de.uni_stuttgart.ipvs.ids.communication.ReleaseReadLock;
import de.uni_stuttgart.ipvs.ids.communication.ReleaseWriteLock;
import de.uni_stuttgart.ipvs.ids.communication.RequestReadVote;
import de.uni_stuttgart.ipvs.ids.communication.RequestWriteVote;
import de.uni_stuttgart.ipvs.ids.communication.ValueResponseMessage;
import de.uni_stuttgart.ipvs.ids.communication.Vote;
import de.uni_stuttgart.ipvs.ids.communication.Vote.State;
import de.uni_stuttgart.ipvs.ids.communication.WriteRequestMessage;

public class MajorityConsensus<T> {

	protected Collection<SocketAddress> replicas;

	protected DatagramSocket socket;
	protected NonBlockingReceiver nbio;

	final static int TIMEOUT = 1000;

	public MajorityConsensus(Collection<SocketAddress> replicas, int port)
			throws SocketException {
		this.replicas = replicas;
		SocketAddress address = new InetSocketAddress("127.0.0.1", port);
		this.socket = new DatagramSocket(address);
		this.nbio = new NonBlockingReceiver(socket);
	}

	/**
	 * Part c) Implement this method.
	 */
	/**
	 * @method requestReadVote
	 * @discription send RequestReadVote to replica
	 * @return votes: the list of votes from all available replicas
	 */
	protected Collection<MessageWithSource<Vote>> requestReadVote() throws IOException, ClassNotFoundException {
		// TODO: Implement me!
		Collection<MessageWithSource<Vote>> votes;
		Vector<DatagramPacket> packet;

		//send RequestReadVote request
		RequestReadVote reqReadVote = new RequestReadVote();
		Object[] objs = this.replicas.toArray();
		for(int i = 0; i<this.replicas.size();i++)
		{
			sendToReplica((SocketAddress)objs[i],reqReadVote);
		}

		//recevie read votes
		packet = nbio.receiveMessages(TIMEOUT,this.replicas.size());
		votes = nbio.unpack(packet);

		return votes;
	}
	
	/**
	 * Part c) Implement this method.
	 */
	/**
	 * @method releaseReadLock
	 * @discription send ReleaseReadLock to replica
	 * @param lockedReplicas: socket address of all replicas with readlock
	 */
	protected void releaseReadLock(Collection<SocketAddress> lockedReplicas) throws IOException {
		// TODO: Implement me!
		//send ReleaseReadLock request
		ReleaseReadLock releaseReadLock = new ReleaseReadLock();
		Object[] objs = lockedReplicas.toArray();
		for(int i = 0; i<lockedReplicas.size();i++)
		{
			sendToReplica((SocketAddress)objs[i],releaseReadLock);
		}
		//receive ACK
		Vector<DatagramPacket> nack = nbio.receiveMessages(TIMEOUT, lockedReplicas.size());
	}
	
	/**
	 * Part d) Implement this method.
	 */
	/**
	 * @method requestWriteVote
	 * @discription send RequestWriteVote to replica
	 * @return votes: the list of votes from all available replicas
	 */
	protected Collection<MessageWithSource<Vote>> requestWriteVote() throws QuorumNotReachedException, IOException, ClassNotFoundException {
		// TODO: Implement me!
		Collection<MessageWithSource<Vote>> votes;
		Vector<DatagramPacket> packet;

		// send RequestWriteVote request
		RequestWriteVote requestWriteVote = new RequestWriteVote();
		Object[] objs = this.replicas.toArray();
		for(int i = 0; i<this.replicas.size();i++)
		{
			sendToReplica((SocketAddress)objs[i],requestWriteVote);
		}

		//recevie write votes
		packet = nbio.receiveMessages(TIMEOUT,this.replicas.size());
		votes = nbio.unpack(packet);

		return votes;
	}
	
	/**
	 * Part d) Implement this method.
	 */
	/**
	 * @method releaseWriteLock
	 * @discription send ReleaseWriteLock to replica
	 * @param lockedReplicas: socket address of all replicas with writelock
	 */
	protected void releaseWriteLock(Collection<SocketAddress> lockedReplicas) throws IOException {
		// TODO: Implement me!
		// send releaseWriteLock
		ReleaseWriteLock releaseWriteLock = new ReleaseWriteLock();
		Object[] objs = lockedReplicas.toArray();
		for(int i = 0; i<lockedReplicas.size();i++)
		{
			sendToReplica((SocketAddress)objs[i],releaseWriteLock);
		}
		//receive ACK
		Vector<DatagramPacket> nack = nbio.receiveMessages(TIMEOUT, lockedReplicas.size());
	}
	
	/**
	 * Part c) Implement this method.
	 */
	/**
	 * @method readReplica
	 * @discription read value from replica
	 * @param replica: socket address of replica read from
	 * @return value: value read from replica
	 */
	protected T readReplica(SocketAddress replica) throws IOException, ClassNotFoundException {
		// TODO: Implement me!
		//send ReadRequestMessage to replica
		ReadRequestMessage readRequestMessage = new ReadRequestMessage();
		sendToReplica(replica,readRequestMessage);
        //get value from replica
		Collection<MessageWithSource<T>> message;
		Vector<DatagramPacket> packet;
		packet = nbio.receiveMessages(TIMEOUT,1);
		message = nbio.unpack(packet);
		Object[] objs = message.toArray();
		MessageWithSource<ValueResponseMessage<T>> res = (MessageWithSource<ValueResponseMessage<T>>)objs[0];
		return (T)res.getMessage().getValue();
	}
	
	/**
	 * Part d) Implement this method.
	 */
	/**
	 * @method writeReplicas
	 * @discription write value to replica
	 * @param lockedReplicas: the list of socket address of replica write to
	 * @param newValue: value write to replica
	 */
	protected void writeReplicas(Collection<SocketAddress> lockedReplicas, VersionedValue<T> newValue) throws IOException {
		// TODO: Implement me!
		WriteRequestMessage writeRequestMessage = new WriteRequestMessage(newValue);
		Object[] objs = lockedReplicas.toArray();
		//send new value to replica
		for(int i = 0; i<lockedReplicas.size();i++)
		{
			sendToReplica((SocketAddress)objs[i],writeRequestMessage);
		}
		try {
			nbio.receiveMessages(TIMEOUT, lockedReplicas.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Part c) Implement this method (and checkQuorum(), see below) to read the
	 * replicated value using the majority consensus protocol.
	 */
	/**
	 * @method get
	 * @discription read value from replica
	 * @return value: value read from replica
	 */
	public VersionedValue<T> get() throws QuorumNotReachedException, IOException, ClassNotFoundException {
		// TODO: Implement me!
		//send RequestReadVote request
		Collection<MessageWithSource<Vote>> votes = null;
		votes = requestReadVote();

		//checkQuorum
		Collection<MessageWithSource<Vote>> voteYes = null;
		voteYes = checkQuorum(votes);
		MessageWithSource<Vote> voteMax = null;
		Collection<SocketAddress> lockedReplicas = new ArrayList<SocketAddress>();
		//find out the replica with the latest version to read from
		if ( voteYes != null )
		{
			Object[] objs = voteYes.toArray();
			voteMax = (MessageWithSource<Vote>)objs[0];
			for(int i=0; i<voteYes.size();i++ )
			{
				if( ((MessageWithSource<Vote>) objs[i]).getMessage().getVersion() > voteMax.getMessage().getVersion() )
				{
					voteMax = (MessageWithSource<Vote>)objs[i];
				}
				lockedReplicas.add(((MessageWithSource<Vote>) objs[i]).getSource());
			}
		}

		VersionedValue<T> versionValue = null;

		if( voteMax != null ) {
			//read value from replica
			T value = readReplica(voteMax.getSource());
			versionValue = new VersionedValue<T>(voteMax.getMessage().getVersion(), value);
			//release readlock
			releaseReadLock(lockedReplicas);
		}

		return versionValue;
	}

	/**
	 * Part d) Implement this method to set the
	 * replicated value using the majority consensus protocol.
	 */
	/**
	 * @method set
	 * @discription write value from replica
	 * @param value: new value write to replica
	 */
	public void set(T value) throws QuorumNotReachedException, IOException, ClassNotFoundException {
		// TODO: Implement me!
		//send RequestWriteVote request
		Collection<MessageWithSource<Vote>> votes = null;
		votes =requestWriteVote();

		//checkQuorum
		Collection<MessageWithSource<Vote>> voteYes = null;
		voteYes = checkQuorum(votes);
		MessageWithSource<Vote> voteMax = null;
		Collection<SocketAddress> lockedReplicas = new ArrayList<SocketAddress>();
		//find out the replica with the latest version
		if ( voteYes != null )
		{
			Object[] objs = voteYes.toArray();
			voteMax = (MessageWithSource<Vote>)objs[0];
			for(int i=0; i<voteYes.size();i++ )
			{
				if( ((MessageWithSource<Vote>) objs[i]).getMessage().getVersion() > voteMax.getMessage().getVersion() )
				{
					voteMax = (MessageWithSource<Vote>)objs[i];
				}
				lockedReplicas.add(((MessageWithSource<Vote>) objs[i]).getSource());
			}
		}

		if( voteMax != null ) {
			//write value to replica
			VersionedValue<T> newValue = new VersionedValue<T>(voteMax.getMessage().getVersion()+1,value );
			writeReplicas(lockedReplicas, newValue);
			//release write lock
			releaseWriteLock(lockedReplicas);
		}
	}

	/**
	 * Part c) Implement this method to check whether a sufficient number of
	 * replies were received. If a sufficient number was received, this method
	 * should return the {@link MessageWithSource}s of the locked {@link Replica}s.
	 * Otherwise, a QuorumNotReachedException must be thrown.
	 * @throws QuorumNotReachedException 
	 */
	/**
	 * @method checkQuorum
	 * @discription check whether a sufficient number of replies were received
	 * @param replies: the votes from replicas
	 */
	protected Collection<MessageWithSource<Vote>> checkQuorum(
			Collection<MessageWithSource<Vote>> replies) throws QuorumNotReachedException {
		// TODO: Implement me!
		int voteYes = 0;
        //get votes from datagram packet
		Collection<MessageWithSource<Vote>> messageSource = new ArrayList<MessageWithSource<Vote>>();
		Collection<SocketAddress> achieved = new ArrayList<SocketAddress>();
		Object[] objs = replies.toArray();
		for(int i = 0; i<replies.size();i++) {
			MessageWithSource<Vote> vote = (MessageWithSource<Vote>)objs[i];
			if( vote.getMessage().getState() == State.YES )
			{
				voteYes++;
				messageSource.add(vote);
				achieved.add(vote.getSource());
			}
		}
        //check if the number of votes larger than number of replicas
		if( voteYes > this.replicas.size()/2 )
		{
			return messageSource;
		}
		else{
			QuorumNotReachedException e = new QuorumNotReachedException(this.replicas.size()/2,achieved);
			throw e;
		}
	}
	/**
	 * @method sendToReplica
	 * @discription send request to client
	 * @param socketAddr: socket address
	 * @param obj: request object
	 * @return void
	 */
	protected void sendToReplica( SocketAddress socketAddr, Object obj ) throws IOException {
        //send request object to replica
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream oout = new ObjectOutputStream(bout);
		oout.writeObject(obj);
		oout.flush();
		byte[] sendBuff = bout.toByteArray();
		DatagramPacket datagram = new DatagramPacket(sendBuff, sendBuff.length,socketAddr);
		this.socket.send(datagram);
        //close output stream
		bout.close();
		oout.close();
	}

}
