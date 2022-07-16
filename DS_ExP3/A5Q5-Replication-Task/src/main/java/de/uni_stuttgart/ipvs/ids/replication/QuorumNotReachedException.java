package de.uni_stuttgart.ipvs.ids.replication;

import java.net.SocketAddress;
import java.util.Collection;

public class QuorumNotReachedException extends Exception {

	private static final long serialVersionUID = 3957064339052100451L;
	
	final protected int required;
	final protected Collection<SocketAddress> achieved;
//	protected Collection<SocketAddress> notAchieved;
	
	
	public Collection<SocketAddress> getAchieved() {
		return achieved;
	}

	public QuorumNotReachedException(int required, Collection<SocketAddress> achieved) {
		super("Got " + achieved.size() + " votes, but needed " + required + " votes.");
		this.required = required;
		this.achieved = achieved;
	}

}
