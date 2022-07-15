package de.unistgt.ipvs.vs.ex1.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import de.unistgt.ipvs.vs.ex1.common.ICalculation;

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
        @Override
	public void add(int value) throws RemoteException {
		result += value;
	}
	
	// Subtract a value
        @Override
	public void subtract(int value) throws RemoteException {
		result -= value;
	}

	// Multiply by a value
        @Override
	public void multiply(int value) throws RemoteException {
		result *= value;
	}

	// Return the result of the current calculation
        @Override
	public int getResult() throws RemoteException {
		return result;
	}
}
