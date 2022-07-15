package de.unistgt.ipvs.vs.ex2.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Notice that you are not allowed to change this class.
 */
public interface ICalculationFactory extends Remote {
	// create a new multiplication session
	public ICalculation getSession() throws RemoteException;
}