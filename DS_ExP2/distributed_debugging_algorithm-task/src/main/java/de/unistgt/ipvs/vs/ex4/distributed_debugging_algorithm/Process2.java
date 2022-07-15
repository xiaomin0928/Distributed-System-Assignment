package de.unistgt.ipvs.vs.ex4.distributed_debugging_algorithm;

//you are not allowed to change this class
public class Process2 extends AbstractProcess {

	private VectorClock vectorClock;

	public Process2(Monitor monitor, AbstractProcess[] processes, int Id) {
		super(monitor, processes, Id);

		vectorClock = new VectorClock(this.Id, this.numberOfProcesses);
	}

	@Override
	public void run() {
		// send the initial state to Monitor
		Message message = new Message(new VectorClock(vectorClock), this.localVariable);
		monitor.receiveMessage(this.Id, message);
		
		//line 1
		// receive
		Message receivedMessage = receive(0); // receive from process 0
		this.vectorClock.update(receivedMessage.getVectorClock());
		this.localVariable = receivedMessage.getLocalVariable();
		this.vectorClock.increment();

		// notify the monitor
		message = new Message(new VectorClock(vectorClock), this.localVariable);
		monitor.receiveMessage(this.Id, message);

		//line 2
		this.localVariable = this.localVariable + 5;
		this.vectorClock.increment();
		// notify the monitor
		message = new Message(new VectorClock(vectorClock), this.localVariable);
		monitor.receiveMessage(this.Id, message);

		//line 3
		// receive
		receivedMessage = receive(0); // receive from process 0
		this.vectorClock.update(receivedMessage.getVectorClock());
		this.localVariable = this.localVariable + receivedMessage.getLocalVariable();
		this.vectorClock.increment();

		// notify the monitor
		message = new Message(new VectorClock(vectorClock), this.localVariable);
		monitor.receiveMessage(this.Id, message);

		//line 4
		send(0, message); // send to process 0

		// send terminate signal
		monitor.processTerminated(this.Id);

		System.out.printf("process:%d , the local variable= %d\n", this.Id, this.localVariable);
	}

}
