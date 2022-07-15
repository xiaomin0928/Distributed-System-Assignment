package de.unistgt.ipvs.vs.ex4.distributed_debugging_algorithm;

//you are not allowed to change this class structure
public class VectorClock {

	protected int[] vectorClock;
	private int processId;
	private int numberOfProcesses;

	public VectorClock(int processId, int numberOfProcesses) {
		vectorClock = new int[numberOfProcesses];   //The default initialization is {0,0}
		this.numberOfProcesses = numberOfProcesses;
		this.processId = processId;
	}

	VectorClock(VectorClock other) {
		vectorClock = other.vectorClock.clone();
		processId = other.processId;
		numberOfProcesses = other.numberOfProcesses;

	}
	/**
	 * @method increment
	 * @discription increase the vectorclock according to the process id
	 * @return void
	 */
	public void increment() {
		// TODO
		/*
		 * Complete a code to increment the local clock component
		 */

		this.vectorClock[this.processId]++;

	}
	/**
	 * @method get
	 * @discription get vectorclock
	 * @return void
	 */
	public int[] get() {
		// TODO
		// Complete a code to return the vectorClock value
		return this.vectorClock;
	}

	/**
	 * @method update
	 * @discription update vectorclock with Supermum operation
	 * @return void
	 */
	public void update(VectorClock other) {
		// TODO
		/*
		 * Implement Supermum operation
		 */
		for(int i = 0; i < this.vectorClock.length; i++)
		{
			if( other.vectorClock[i] > this.vectorClock[i] )
			{
				this.vectorClock[i] = other.vectorClock[i];
			}
		}

	}

	/**
	 * @method checkConsistency
	 * @discription check if two vectorclocks are consistent
	 * @param otherProcessId: other process id
	 * @param other: other vectorclock
	 * @return void
	 */
	public boolean checkConsistency(int otherProcessId, VectorClock other) {
		//TODO
		
		/*
		 * Implement a code to check if a state is consist regarding two vector clocks (i.e. this and other).
		 * See slide 41 from global state lecture.
		 */

		if( this.vectorClock[this.processId] >= other.vectorClock[this.processId] &&
		    other.vectorClock[otherProcessId] >= this.vectorClock[otherProcessId])
		{
			return true;
		}

		return false;
	}
}
