package de.unistgt.ipvs.vs.ex4.distributed_debugging_algorithm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

//you are not allowed to change this class structure. However, you can add local functions!
public class Monitor implements Runnable {

	/**
	 * The state consists on vector timestamp and local variables of each
	 * process. In this class, a state is represented by messages (events)
	 * indices of each process. The message contains a local variable and vector
	 * timestamp, see Message class. E.g. if state.processesMessagesCurrentIndex
	 * contains {1, 2}, it means that the state contains the second message
	 * (event) from process1 and the third message (event) from process2
	 */
	private class State {
		// Message indices of each process
		private int[] processesMessagesCurrentIndex;

		public State(int numberOfProcesses) {
			processesMessagesCurrentIndex = new int[numberOfProcesses];
		}

		public State(int[] processesMessagesCurrentIndex) {
			this.processesMessagesCurrentIndex = processesMessagesCurrentIndex;
		}

		{
			processesMessagesCurrentIndex = new int[numberOfProcesses];
		}

		public int[] getProcessesMessagesCurrentIndex() {
			return processesMessagesCurrentIndex;
		}

		public int getProcessMessageCurrentIndex(int processId) {
			return this.processesMessagesCurrentIndex[processId];
		}

		@Override
		public boolean equals(Object other) {
			State otherState = (State) other;

			// Iterate over processesMessagesCurrentIndex array
			for (int i = 0; i < numberOfProcesses; i++)
				if (this.processesMessagesCurrentIndex[i] != otherState.processesMessagesCurrentIndex[i])
					return false;

			return true;
		}
	}

	private int numberOfProcesses;
	private final int numberOfPredicates = 4;

	// Count of still running processes. The monitor starts to check predicates
	// (build lattice) whenever runningProcesses equals zero.
	private AtomicInteger runningProcesses;
	/*
	 * Q1, Q2, ..., Qn It represents the processes' queue. See distributed
	 * debugging algorithm from global state lecture!
	 */
	private List<List<Message>> processesMessages;

	// list of states
	private LinkedList<State> states;

	//all reachable states
	private LinkedList<State> allReachableStates = new LinkedList<>();

	// The predicates checking results
	private boolean[] possiblyTruePredicatesIndex;
	private boolean[] definitelyTruePredicatesIndex;

	public Monitor(int numberOfProcesses) {
		this.numberOfProcesses = numberOfProcesses;

		runningProcesses = new AtomicInteger();
		runningProcesses.set(numberOfProcesses);

		processesMessages = new ArrayList<>(numberOfProcesses);
		for (int i = 0; i < numberOfProcesses; i++) {
			List<Message> tempList = new ArrayList<>();
			processesMessages.add(i, tempList);
		}

		states = new LinkedList<>();

		possiblyTruePredicatesIndex = new boolean[numberOfPredicates];// there
																		// are
																		// three
		// predicates
		for (int i = 0; i < numberOfPredicates; i++)
			possiblyTruePredicatesIndex[i] = false;

		definitelyTruePredicatesIndex = new boolean[numberOfPredicates];
		for (int i = 0; i < numberOfPredicates; i++)
			definitelyTruePredicatesIndex[i] = false;
	}

	/**
	 * receive messages (events) from processes
	 *
	 * @param processId
	 * @param message
	 */
	public void receiveMessage(int processId, Message message) {
		synchronized (processesMessages) {
			processesMessages.get(processId).add(message);
		}
	}

	/**
	 * Whenever a process terminates, it notifies the Monitor. Monitor only
	 * starts to build lattice and check predicates when all processes terminate
	 *
	 * @param processId
	 */
	public void processTerminated(int processId) {
		runningProcesses.decrementAndGet();
	}

	public boolean[] getPossiblyTruePredicatesIndex() {
		return possiblyTruePredicatesIndex;
	}

	public boolean[] getDefinitelyTruePredicatesIndex() {
		return definitelyTruePredicatesIndex;
	}

	@Override
	public void run() {
		// wait till all processes terminate
		while (runningProcesses.get() != 0)
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		// create initial state (S00)
		State initialState = new State(numberOfProcesses);

		// check predicates for part (b)
		for (int predicateNo = 0; predicateNo < 3; predicateNo++) {
			System.out.printf("Predicate%d-----------------------------------\n",predicateNo);
			states.add(initialState); // add the initial state to states list
			buildLattice(predicateNo, 0, 1);
			states.clear();

		}

		if (numberOfProcesses > 2) {
			int predicateNo = 3;
			System.out.printf("Predicate%d-----------------------------------\n",predicateNo);
			states.add(initialState); // add the initial state to states list
			buildLattice(predicateNo, 0, 2);
			states.clear();
		}

	}

	/**
	 * @method buildLattice
	 * @discription build the lattice of consistent states
	 * @param predicateNo: which predicate to validate
	 * @param process_i_id: id of the process i
	 * @param process_j_id: id of the process j
	 * @return void
	 */
	public void buildLattice(int predicateNo, int process_i_id, int process_j_id) {
		// TODO
		/*
		 * - implement this function to build the lattice of consistent states.
		 * - The goal of building the lattice is to check a predicate if it is
		 * possibly or/and definitely True. Thus your function should stop
		 * whenever the predicate evaluates to both possibly and definitely
		 * True. NOTE1: this function should call findReachableStates and
		 * checkPredicate functions. NOTE2: predicateNo, process_i_id and
		 * process_j_id are described in checkPredicate function.
		 */

		//get all reachable states of S00
		allReachableStates.clear();
		LinkedList<State> reachableStates = findReachableStates(states.getFirst(),process_i_id,process_j_id);
		//add all reachable states int states list
		for(int i = 0; i < reachableStates.size(); i++)
		{
			states.add(reachableStates.get(i));
		}

		//check a predicate if it is  possibly/definitely True
		checkPredicate(predicateNo,process_i_id,process_j_id);

	}

	/**
	 * @method findReachableStates
	 * @discription find all reachable states starting from a given state
	 * @param state
	 * @param process_i_id: id of the process i
	 * @param process_j_id: id of the process j
	 * @return list of all reachable states
	 */
	private LinkedList<State> findReachableStates(State state, int process_i_id, int process_j_id) {
		// TODO
		/*
		 * Given a state, implement a code that find all reachable states. The
		 * function should return a list of all reachable states
		 *
		 */

		//find reachable state in the next state with process i state change
		int[] processesMessagesCurrentIndexNext_i = new int[numberOfProcesses];
		if( state.processesMessagesCurrentIndex[process_i_id] + 1 < processesMessages.get(process_i_id).size() ) {
			//create a new state by process i move to next event and process j doesn't change
			processesMessagesCurrentIndexNext_i[process_i_id] = state.processesMessagesCurrentIndex[process_i_id] + 1;
			processesMessagesCurrentIndexNext_i[process_j_id] = state.processesMessagesCurrentIndex[process_j_id];
			State nextState_i = new State(processesMessagesCurrentIndexNext_i);
			//get message of each event
			Message stateMessageNext_ii = processesMessages.get(process_i_id).get(nextState_i.getProcessMessageCurrentIndex(process_i_id));
			Message stateMessageNext_ij = processesMessages.get(process_j_id).get(nextState_i.getProcessMessageCurrentIndex(process_j_id));
			//check if the state is reachable
			if (stateMessageNext_ii.getVectorClock().checkConsistency(process_j_id, stateMessageNext_ij.getVectorClock())) {
				//check if the state is already in the list
				if( !allReachableStates.contains(nextState_i) ) {
					allReachableStates.add(nextState_i);
					//find the next reachable state from this state
					findReachableStates(nextState_i, process_i_id, process_j_id);
				}
			}
		}
		//find reachable state in the next state with process j state change
		int[] processesMessagesCurrentIndexNext_j = new int[numberOfProcesses];
		if( state.processesMessagesCurrentIndex[process_j_id] + 1 < processesMessages.get(process_j_id).size() ) {
			//create a new state by process j move to next event and process i doesn't change
			processesMessagesCurrentIndexNext_j[process_i_id] = state.processesMessagesCurrentIndex[process_i_id];
			processesMessagesCurrentIndexNext_j[process_j_id] = state.processesMessagesCurrentIndex[process_j_id] + 1;
			State nextState_j = new State(processesMessagesCurrentIndexNext_j);
			//get message of each event
			Message stateMessageNext_jj = processesMessages.get(process_j_id).get(nextState_j.getProcessMessageCurrentIndex(process_j_id));
			Message stateMessageNext_ji = processesMessages.get(process_i_id).get(nextState_j.getProcessMessageCurrentIndex(process_i_id));
			//check if the state is reachable
			if (stateMessageNext_ji.getVectorClock().checkConsistency(process_j_id, stateMessageNext_jj.getVectorClock())) {
				//check if the state is already in the list
				if( !allReachableStates.contains(nextState_j) ) {
					allReachableStates.add(nextState_j);
					//find the next reachable state from this state
					findReachableStates(nextState_j, process_i_id, process_j_id);
				}
			}
		}

		return allReachableStates;
	}

	/**
	 * - check a predicate and return true if the predicate is **definitely**
	 * True. - To simplify the code, we check the predicates only on local
	 * variables of two processes. Therefore, process_i_Id and process_j_id
	 * refer to the processes that have the local variables in the predicate.
	 * The predicate0, predicate1 and predicate2 contain the local variables
	 * from process1 and process2. whilst the predicate3 contains the local
	 * variables from process1 and process3.
	 *
	 * @param predicateNo: which predicate to validate
	 * @param process_i_id: id of the process i
	 * @param process_j_id: id of the process j
	 * @return true if predicate is definitely true else return false
	 */
	private boolean checkPredicate(int predicateNo, int process_i_id, int process_j_id) {
		// TODO
		/*
		 * - check if a predicate is possibly and/or definitely true. - iterate
		 * over all reachable states to check the predicates. NOTE: you can use
		 * the following code switch (predicateNo) { case 0: predicate =
		 * Predicate.predicate0(process_i_Message, process_j_Message); break;
		 * case 1: ... }
		 */

		int level = 0;
		int levelMax = processesMessages.get(process_i_id).size() + processesMessages.get(process_j_id).size() - 2;
		//list of states in each level
		LinkedList<State> levelStateList = new LinkedList<State>();
		//list of states that need to be checked predicate
		LinkedList<State> statesList = new LinkedList<State>();
		//init the list with S00
		statesList.add(states.getFirst());

		//check a predicate if it is possibly True
		while( level <= levelMax )
		{
			//if all states in this level is predicate false, if all states are false then move to the next level and check
			if( checkPredicateAllFalse(statesList, predicateNo, process_i_id, process_j_id) )
			{
				//find reachable states in the next level
				level++;
				for(int k=0; k < states.size(); k++)
				{
					if( level == getStateLevel(states.get(k),process_i_id,process_j_id) )
					{
						levelStateList.add(states.get(k));
					}
				}
				statesList = new LinkedList<State>(levelStateList);
			}
			//if one of states in this level is predicate ture, predicate is possibly True
			else {
				possiblyTruePredicatesIndex[predicateNo] = true;
				break;
			}

		}

		//clear the data
		level = 0;
		statesList.clear();
		statesList.add(states.getFirst());

		//check a predicate if it is  definitely True
		//if the predicate is possibly false, it is also definitely false
		if( possiblyTruePredicatesIndex[predicateNo] == false )
		{
			definitelyTruePredicatesIndex[predicateNo] = false;
		}
		else
		{
			while( level <= levelMax )
			{
				//if statesList is not empty, one of states in this level is predicate false, then check the next level
				if( !statesList.isEmpty() )
				{
					level++;
					statesList.clear();
					for(int k=0; k < states.size(); k++)
					{
						//find reachable states in the next level
						if( level == getStateLevel(states.get(k),process_i_id,process_j_id) )
						{
							levelStateList.add(states.get(k));
							//add states to statesList if it is predicate false
							if( checkStatePredicate(states.get(k),predicateNo,process_i_id,process_j_id) == false )
							{
								statesList.add(states.get(k));
							}
						}
					}
				}
				//if statesList is empty, all states in this level are predicate true, the predicate is definitely true
				else {
					definitelyTruePredicatesIndex[predicateNo] = true;
					break;
				}

			}
		}

		return definitelyTruePredicatesIndex[predicateNo];
	}

	/**
	 * @method checkPredicateAllFalse
	 * @discription check if the predicates of all states are false
	 * @param stateList: states need to be checked
	 * @param predicateNo: which predicate to validate
	 * @param process_i_id: id of the process i
	 * @param process_j_id: id of the process j
	 * @return true: all states are predicate false
	 *         false: one of states is predicate true
	 */
	private boolean checkPredicateAllFalse(LinkedList<State> stateList, int predicateNo, int process_i_id, int process_j_id) {

		for(int i = 0; i < stateList.size(); i++)
		{
			State s = stateList.get(i);
			if( checkStatePredicate(s,predicateNo,process_i_id,process_j_id) == true )
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * @method checkStatePredicate
	 * @discription check the predicate of state
	 * @param state: state need to be checked
	 * @param predicateNo: which predicate to validate
	 * @param process_i_id: id of the process i
	 * @param process_j_id: id of the process j
	 * @return true:  predicate of state are predicate true
	 *         false: predicate of state are predicate flase
	 */
	private boolean checkStatePredicate(State state, int predicateNo, int process_i_id, int process_j_id) {

		boolean predicate = false;
		Message process_i_Message = processesMessages.get(process_i_id).get(state.getProcessMessageCurrentIndex(process_i_id));
		Message process_j_Message = processesMessages.get(process_j_id).get(state.getProcessMessageCurrentIndex(process_j_id));
		switch (predicateNo) {
			case 0:
				predicate = Predicate.predicate0(process_i_Message, process_j_Message);
				break;
			case 1:
				predicate = Predicate.predicate1(process_i_Message, process_j_Message);
				break;
			case 2:
				predicate = Predicate.predicate2(process_i_Message, process_j_Message);
				break;
			case 3:
				predicate = Predicate.predicate3(process_i_Message, process_j_Message);
				break;
			default:
				break;
		}

		return predicate;
	}

	/**
	 * @method getStateLevel
	 * @discription get the level of state
	 * @param state: state need to be get the level
	 * @param process_i_id: id of the process i
	 * @param process_j_id: id of the process j
	 * @return level:  the level of state
	 */
	private int getStateLevel(State state,int process_i_id, int process_j_id)
	{
		int level = 0;
		level = state.getProcessMessageCurrentIndex(process_i_id) + state.getProcessMessageCurrentIndex(process_j_id);

		return level;
	}

}
