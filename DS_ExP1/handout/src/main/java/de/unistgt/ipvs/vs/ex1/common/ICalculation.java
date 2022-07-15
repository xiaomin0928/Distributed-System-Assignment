package de.unistgt.ipvs.vs.ex1.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Notice that you are not allowed to change this class.
 */
public interface ICalculation extends Remote {
	// Add a value
	public void add(int value) throws RemoteException;

	// Subtract a value
	public void subtract(int value) throws RemoteException;

	// Multiply by a value
	public void multiply(int value) throws RemoteException;
	
	// Return the result of the current calculation
	public int getResult() throws RemoteException;
	
}
