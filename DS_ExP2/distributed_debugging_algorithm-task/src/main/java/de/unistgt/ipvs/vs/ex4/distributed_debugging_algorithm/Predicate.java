package de.unistgt.ipvs.vs.ex4.distributed_debugging_algorithm;

//you are not allowed to change this class structure
public class Predicate {

	static public boolean predicate0(Message process_i_Message, Message process_j_Message) {
		if (Math.abs(3 * process_i_Message.getLocalVariable() - 4 * process_j_Message.getLocalVariable()) == 25)
			return true;
		else
			return false;
	}

	static public boolean predicate1(Message process_i_Message, Message process_j_Message) {
		// TODO
		/*
		 * Add you code here to implement predicate2 x1 - x2= 15
		 */
		if ( (process_i_Message.getLocalVariable() - process_j_Message.getLocalVariable()) == 15 )
			return true;
		else
			return false;
	}

	static public boolean predicate2(Message process_i_Message, Message process_j_Message) {
		// TODO
		/*
		 * Add you code here to implement predicate2 x1 + x2= 30
		 */
		if ( (process_i_Message.getLocalVariable() + process_j_Message.getLocalVariable()) == 30 )
			return true;
		else
			return false;
	}

	static public boolean predicate3(Message process_i_Message, Message process_j_Message) {
		// TODO
		/*
		 * Add you code here to implement predicate1 x1- x3=8
		 */
		if ( (process_i_Message.getLocalVariable() - process_j_Message.getLocalVariable()) == 8 )
			return true;
		else
			return false;
	}

}
