package de.unistgt.ipvs.vs.ex2.server;

import de.unistgt.ipvs.vs.ex2.common.ICalculation;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


/**
 * Notice that you are not allowed to change this class.
 */
public class CalculationImpl extends UnicastRemoteObject implements ICalculation {
	private static final long serialVersionUID = -2946208347668376521L;
	
	// The current result
	private int result;
	
	// Create a new session with an initial result of 0.
	public CalculationImpl() throws RemoteException {
		result = 0;
	}

	// Add a value
	public void add(int value) throws RemoteException {
		result += value;
	}
	
	// Subtract a value
	public void subtract(int value) throws RemoteException {
		result -= value;
	}

	// Multiply by a value
	public void multiply(int value) throws RemoteException {
		result *= value;
	}

	// Return the result of the current calculation
	public int getResult() throws RemoteException {
		return result;
	}
}
