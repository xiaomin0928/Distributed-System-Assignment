package de.unistgt.ipvs.vs.ex4.distributed_debugging_algorithm;

import java.util.concurrent.atomic.AtomicBoolean;

//you are not allowed to change this class
public abstract class AbstractProcess implements Runnable {
	protected Monitor monitor;
	protected AbstractProcess[] processes;
	protected int localVariable = 0;
	protected int numberOfProcesses;

	protected AtomicBoolean[] newVariableFlags;
	protected Message[] newVariables;

	protected AtomicBoolean[] receivedVariableFlags;

	protected int Id;

	public AbstractProcess(Monitor monitor, AbstractProcess[] processes, int Id) {
		this.monitor = monitor;
		this.processes = processes;
		this.Id = Id;
		numberOfProcesses = processes.length;

		newVariableFlags = new AtomicBoolean[numberOfProcesses];
		for (int i = 0; i < numberOfProcesses; i++) {
			newVariableFlags[i] = new AtomicBoolean();
			newVariableFlags[i].set(false);
		}
		receivedVariableFlags = new AtomicBoolean[numberOfProcesses];
		for (int i = 0; i < numberOfProcesses; i++) {
			receivedVariableFlags[i] = new AtomicBoolean();
			receivedVariableFlags[i].set(false);
		}
		newVariables = new Message[numberOfProcesses];

	}

	/**
	 * send a message to process (processId) and wait for ack
	 * @param processId
	 * @param message
	 */
	public void send(int processId, Message message) {

		processes[processId].setNewVariable(this.Id, message);

		processes[processId].setNewVariableFlag(this.Id, true);

		// wait till process [processId] receive the event
		while (!processes[processId].getReceivedVariableFlag(this.Id))
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		processes[processId].setReceivedVariableFlag(this.Id, false);
	}

	/**
	 * receive a message from process (processId) and send ack so the sending process can continue.
	 * @param processId
	 * @return
	 */
	public Message receive(int processId) {
		// wait till process [processId] send the event
		while (!this.getNewVariableFlag(processId))
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		Message temp;
//		synchronized (newVariables) {
			temp = this.newVariables[processId];
//		}

		// reset newVariableFlag
		this.setNewVariableFlag(processId, false);
		// notify the sender
		this.setReceivedVariableFlag(processId, true);

		return temp;
	}

	void setNewVariableFlag(int processId, boolean value) {
		newVariableFlags[processId].set(value);
	}
	
	boolean getNewVariableFlag(int processId) {
		return this.newVariableFlags[processId].get();
	}

	void setReceivedVariableFlag(int processId, boolean value) {
		this.receivedVariableFlags[processId].set(value);
	}

	void setNewVariable(int processId, Message message) {
//		synchronized (newVariables) {
			this.newVariables[processId] = message;
//		}

	}

	boolean getReceivedVariableFlag(int processId) {
		return this.receivedVariableFlags[processId].get();
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}
	
	public int getLocalVariable() {
		return localVariable;
	}


}
