package de.unistgt.ipvs.vs.ex4.distributed_debugging_algorithm;

//you are not allowed to change this class
public class Message {
	private VectorClock vectorClock;
	private int localVariable; // e.g. x

	public Message(VectorClock vectorClock, int localVariabel) {
		this.vectorClock = vectorClock;
		this.localVariable = localVariabel;
	}

	public int getLocalVariable() {
		return localVariable;
	}

	public VectorClock getVectorClock() {
		return vectorClock;
	}
}
