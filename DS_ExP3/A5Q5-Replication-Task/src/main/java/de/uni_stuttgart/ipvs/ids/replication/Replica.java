package de.uni_stuttgart.ipvs.ids.replication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.util.Random;

import de.uni_stuttgart.ipvs.ids.communication.ReadRequestMessage;
import de.uni_stuttgart.ipvs.ids.communication.ReleaseReadLock;
import de.uni_stuttgart.ipvs.ids.communication.ReleaseWriteLock;
import de.uni_stuttgart.ipvs.ids.communication.RequestReadVote;
import de.uni_stuttgart.ipvs.ids.communication.RequestWriteVote;
import de.uni_stuttgart.ipvs.ids.communication.ValueResponseMessage;
import de.uni_stuttgart.ipvs.ids.communication.Vote;
import de.uni_stuttgart.ipvs.ids.communication.WriteRequestMessage;

public class Replica<T> extends Thread {

    public enum LockType {
        UNLOCKED, READLOCK, WRITELOCK
    }

    ;

    private int id;

    private double availability;
    private VersionedValue<T> value;

    protected DatagramSocket socket = null;

    protected LockType lock;

    private final int MAX_LEN = 1024; //the length of receive buffer

    /**
     * This address holds the address of the client holding the lock. This
     * variable should be set to NULL every time the lock is set to UNLOCKED.
     */
    protected SocketAddress lockHolder;

    public Replica(int id, int listenPort, double availability, T initialValue) throws SocketException {
        super("Replica:" + listenPort);
        this.id = id;
        SocketAddress socketAddress = new InetSocketAddress("127.0.0.1", listenPort);
        this.socket = new DatagramSocket(socketAddress);
        this.availability = availability;
        this.value = new VersionedValue<T>(0, initialValue);
        this.lock = LockType.UNLOCKED;
    }


    /**
     * Part a) Implement this run method to receive and process request
     * messages. To simulate a replica that is sometimes unavailable, it should
     * randomly discard requests as long as it is not locked.
     * The probability for discarding a request is (1 - availability).
     * <p>
     * For each request received, it must also be checked whether the request is valid.
     * For example:
     * - Does the requesting client hold the correct lock?
     * - Is the replica unlocked when a new lock is requested?
     */
    public void run() {
        // TODO: Implement me!

        while (true) {
            //receive messages from client
            byte[] receiveBuff = new byte[MAX_LEN];
            DatagramPacket datagram = new DatagramPacket(receiveBuff, MAX_LEN);
            try {
                //get objects from datagram packet
                this.socket.receive(datagram);
                Object objMess = getObjectFromMessage(datagram);
                SocketAddress socketAddr = datagram.getSocketAddress();
                // UNLOCKED
                if (this.lock == LockType.UNLOCKED) {

                    if (Math.random() < (1 - availability)) {
                        //discard requests,do nothing
                    } else {
                        // RequestReadVote
                        if (objMess instanceof RequestReadVote) {
                            //set lock as readlock
                            this.lock = LockType.READLOCK;
                            //send vote YES
                            sendVote(socketAddr, Vote.State.YES, this.value.version);
                        }
                        // RequestWriteVote
                        else if (objMess instanceof RequestWriteVote) {
                            //set lock as writelock
                            this.lock = LockType.WRITELOCK;
                            //send vote YES
                            sendVote(socketAddr, Vote.State.YES, this.value.version);
                        }
                    }
                }
                // READLOCK
                else if (this.lock == LockType.READLOCK) {
                    // RequestReadVote
                    if (objMess instanceof RequestReadVote) {
                        //send vote YES
                        sendVote(socketAddr, Vote.State.YES, this.value.version);
                    }
                    // RequestWriteVote
                    else if (objMess instanceof RequestWriteVote) {
                        //send vote NO
                        sendVote(socketAddr, Vote.State.NO, this.value.version);
                    }
                    // ReadRequestMessage
                    else if (objMess instanceof ReadRequestMessage) {
                        //send value response to client
                        ValueResponseMessage<T> valueResMess = new ValueResponseMessage<T>(this.value.getValue());
                        sendRes(socketAddr, valueResMess);
                    }
                    // ReleaseReadLock
                    else if (objMess instanceof ReleaseReadLock) {
                        //set lock as unlock
                        this.lock = LockType.UNLOCKED;
                        lockHolder = null;
                        //send vote YES as ACK
                        sendVote(socketAddr, Vote.State.YES, this.value.version);
                    }
                }
                // WRITELOCK
                else if (this.lock == LockType.WRITELOCK) {
                    // RequestReadVote
                    if (objMess instanceof RequestReadVote) {
                        //send vote NO as ACK
                        sendVote(socketAddr, Vote.State.NO, this.value.version);
                    }
                    // RequestWriteVote
                    else if (objMess instanceof RequestWriteVote) {
                        //send vote NO as ACK
                        sendVote(socketAddr, Vote.State.NO, this.value.version);
                    }
                    // WriteRequestMessage
                    else if (objMess instanceof WriteRequestMessage) {
                        //write new value
                        WriteRequestMessage<T> writeReqMess = (WriteRequestMessage<T>) getObjectFromMessage(datagram);
                        if (this.value.version < writeReqMess.getVersion()) {
                            this.value = writeReqMess;
                        }
                        //send vote YES as ACK
                        sendVote(socketAddr, Vote.State.YES, this.value.version);
                    }
                    // ReleaseWriteLock
                    else if (objMess instanceof ReleaseWriteLock) {
                        //set lock as unlock
                        this.lock = LockType.UNLOCKED;
                        lockHolder = null;
                        //send vote YES as ACK
                        sendVote(socketAddr, Vote.State.YES, this.value.version);
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This is a helper method. You can implement it if you want to use it or just ignore it.
     * Its purpose is to send a Vote (YES/NO depending on the state) to the given address.
     */
    /**
     * @method sendVote
     * @discription send vote to client
     * @param address: socket address
     * @param state: the state of vote(YES/NO)
     * @param version: the version of value
     * @return void
     */
    protected void sendVote(SocketAddress address,
                            Vote.State state, int version) throws IOException {
        // TODO: Implement me!
        //put vote into output stream
        Vote vote = new Vote(state, version);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oout = new ObjectOutputStream(bout);
        oout.writeObject(vote);
        oout.flush();
        //send vote to client
        byte[] sendBuff = bout.toByteArray();
        DatagramPacket datagram = new DatagramPacket(sendBuff, sendBuff.length, address);
        this.socket.send(datagram);
        //close output stream
        bout.close();
        oout.close();
    }


    /**
     * @method sendVote
     * @discription send vote to client
     * @param address: socket address
     * @param valueResMess: value message sent to clinet(ValueResponseMessage<T>)
     * @return void
     */
    protected void sendRes(SocketAddress address, ValueResponseMessage<T> valueResMess) throws IOException {
        //put ValueResponseMessage into output stream
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oout = new ObjectOutputStream(bout);
        oout.writeObject(valueResMess);
        oout.flush();
        //send ValueResponseMessage to client
        byte[] sendBuff = bout.toByteArray();
        DatagramPacket datagram = new DatagramPacket(sendBuff, sendBuff.length, address);
        this.socket.send(datagram);
        //close output stream
        bout.close();
        oout.close();
    }

    /**
     * This is a helper method. You can implement it if you want to use it or just ignore it.
     * Its purpose is to extract the object stored in a DatagramPacket.
     */
    /**
     * @method getObjectFromMessage
     * @discription get object from datagram packet
     * @param packet: datagram packet
     * @return obj: object from datagram packet
     */
    protected Object getObjectFromMessage(DatagramPacket packet)
            throws IOException, ClassNotFoundException {
        // TODO: Implement me!
        if (packet.getData() == null) {
            System.out.println("packet.getData() null");
        }
        //get object from datagram packet
        ByteArrayInputStream bint = new ByteArrayInputStream(packet.getData());
        ObjectInputStream oint = new ObjectInputStream(bint);
        Object obj = oint.readObject();
        //close input stream
        bint.close();
        oint.close();

        return obj;  // Pacify the compiler
    }

    public int getID() {
        return id;
    }

    public SocketAddress getSocketAddress() {
        return socket.getLocalSocketAddress();
    }

}
