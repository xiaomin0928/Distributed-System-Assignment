package de.unistgt.ipvs.vs.ex2.server;


import de.unistgt.ipvs.vs.ex2.common.ICalculation;
import de.unistgt.ipvs.vs.ex2.common.ICalculationFactory;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Change this class (implementation/signature/...) as necessary to complete the assignment.
 * You may also add some fields or methods.
 */

 public class CalculationImplFactory  extends UnicastRemoteObject implements ICalculationFactory {
    private static final long serialVersionUID = 8409100566761383094L;

    private ICalculation iCalculation; //remote interface

    // Create a remote object
    public CalculationImplFactory() throws RemoteException {
        this.iCalculation = new CalculationImpl();
    }

    /**
     * @method getSession
     * @discription create a new session for client
     * @return iCalculation: a new session for client
     * @throws RemoteException
     */
    @Override
    public ICalculation getSession() throws RemoteException {
        if( iCalculation == null )
        {
            iCalculation = new CalculationImpl();
        }

        return iCalculation;
    }
}
