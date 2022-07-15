package de.unistgt.ipvs.vs.ex4.distributed_debugging_test;

//you are not allowed to change this class structure

import de.unistgt.ipvs.vs.ex4.distributed_debugging_algorithm.AbstractProcess;
import de.unistgt.ipvs.vs.ex4.distributed_debugging_algorithm.Monitor;
import de.unistgt.ipvs.vs.ex4.distributed_debugging_algorithm.Process1;
import de.unistgt.ipvs.vs.ex4.distributed_debugging_algorithm.Process2;
import de.unistgt.ipvs.vs.ex4.distributed_debugging_algorithm.Process3;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public class DebugTest {

	@Rule
        public Timeout globalTimeout = Timeout.seconds(30);

        @Test
	public void testPartb() throws InterruptedException {

		System.out.println("Check only part (b) from the question (4)");

		// instantiation and run threads
		int numberOfProcesses = 2;
		Monitor monitor = new Monitor(numberOfProcesses);
		AbstractProcess[] processes = new AbstractProcess[numberOfProcesses];

		Process1 process1 = new Process1(monitor, processes, 0);
		processes[0] = process1;

		Process2 process2 = new Process2(monitor, processes, 1);
		processes[1] = process2;

		Thread process1Thread = new Thread(process1);
		Thread process2Thread = new Thread(process2);
		Thread monitorThread = new Thread(monitor);

		process1Thread.start();
		process2Thread.start();

		monitorThread.start();

		process1Thread.join();
		process2Thread.join();

		monitorThread.join();

		// checking ---------------

		// check local variables
		assertEquals(10,processes[0].getLocalVariable());
		assertEquals(25,processes[1].getLocalVariable());

		// check predicates
		boolean[] possiblyTruePredicatesIndex = monitor.getPossiblyTruePredicatesIndex();
		boolean[] definitelyTruePredicatesIndex = monitor.getDefinitelyTruePredicatesIndex();

		// predicate0
			assertEquals(true, possiblyTruePredicatesIndex[0]);
		assertEquals(true, definitelyTruePredicatesIndex[0]);

		// predicate1
		assertEquals(true, possiblyTruePredicatesIndex[1]);	
		assertEquals(false, definitelyTruePredicatesIndex[1]);

		// predicate2
		assertEquals(false, possiblyTruePredicatesIndex[2]);	
		assertEquals(false, definitelyTruePredicatesIndex[2]);
	}


        @Test
	public void testPartbc() throws InterruptedException {

		System.out.println("Check part (b)  and part (c) from the question (4)");

		// instantiation and run threads
		int numberOfProcesses = 3;
		Monitor monitor = new Monitor(numberOfProcesses);
		AbstractProcess[] processes = new AbstractProcess[numberOfProcesses];

		Process1 process1 = new Process1(monitor, processes, 0);
		processes[0] = process1;

		Process2 process2 = new Process2(monitor, processes, 1);
		processes[1] = process2;

		Process3 process3 = new Process3(monitor, processes, 2);
		processes[2] = process3;

		Thread process1Thread = new Thread(process1);
		Thread process2Thread = new Thread(process2);
		Thread process3Thread = new Thread(process3);
		Thread monitorThread = new Thread(monitor);

		process1Thread.start();
		process2Thread.start();
		process3Thread.start();

		monitorThread.start();

		process1Thread.join();
		process2Thread.join();
		process3Thread.join();

		monitorThread.join();

		// checking ---------------

		// check local variables
                assertEquals(1,processes[0].getLocalVariable());
                assertEquals(25,processes[1].getLocalVariable());
                assertEquals(11,processes[2].getLocalVariable());


		// check predicates
		boolean[] possiblyTruePredicatesIndex = monitor.getPossiblyTruePredicatesIndex();
		boolean[] definitelyTruePredicatesIndex = monitor.getDefinitelyTruePredicatesIndex();

		// predicate0
                assertEquals(true, possiblyTruePredicatesIndex[0]);
                assertEquals(true, definitelyTruePredicatesIndex[0]);
                
		// predicate1
                assertEquals(true, possiblyTruePredicatesIndex[1]);
                assertEquals(false, definitelyTruePredicatesIndex[1]);

		// predicate2;
                assertEquals(false, possiblyTruePredicatesIndex[2]);
                assertEquals(false, definitelyTruePredicatesIndex[2]);

		// predicate3
                assertEquals(true, possiblyTruePredicatesIndex[3]);
                assertEquals(true, definitelyTruePredicatesIndex[3]);
	}

}
