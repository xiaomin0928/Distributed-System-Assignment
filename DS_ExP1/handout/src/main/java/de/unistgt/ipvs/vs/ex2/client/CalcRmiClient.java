package de.unistgt.ipvs.vs.ex2.client;

import de.unistgt.ipvs.vs.ex2.common.ICalculation;
import de.unistgt.ipvs.vs.ex2.common.ICalculationFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.Collection;


/**
 * Implement the getCalcRes-, init-, and calculate-method of this class as
 * necessary to complete the assignment. You may also add some fields or methods.
 */
public class CalcRmiClient {
	private ICalculation calc = null;

	public CalcRmiClient() {
		this.calc = null;
	}

	/**
	 * @method getCalcRes
	 * @discription get the result of current calculation
	 * @return result: result of current calculation
	 * @throws RemoteException
	 */
	public int getCalcRes() throws RemoteException {
		int result = calc.getResult();
		return result;
	}

	/**
	 * @method init
	 * @discription lookup a remote object
	 * @param url: URL used for lookup
	 * @return true: lookup remote object successfully
	 *         false: lookup remote object failed
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 */
	public boolean init(String url) throws RemoteException, MalformedURLException, NotBoundException {
		//set security manager
		if(System.getSecurityManager()==null){
			System.setSecurityManager(new SecurityManager());
		}
		//lookup remote object
		Object objRemote = java.rmi.Naming.lookup(url);
		if( objRemote == null )
		{
			return false;
		}
		calc =  (ICalculation)objRemote;

		return true;
	}

	/**
	 * @method calculate
	 * @discription
	 * @param calcMode: calculation operation
	 * @param numbers: numbers used for calculation
	 * @return true: calculate successfully
	 *         false: invalid calculation operation, calculate failed
	 * @throws RemoteException
	 */
	public boolean calculate(CalculationMode calcMode, Collection<Integer> numbers) throws RemoteException {

		if( calcMode == CalculationMode.ADD )
		{
			for(int i: numbers)
			{
				calc.add(i);
			}
		}
		else if( calcMode == CalculationMode.SUB )
		{
			for(int i: numbers)
			{
				calc.subtract(i);
			}
		}
		else if( calcMode == CalculationMode.MUL )
		{
			for(int i: numbers)
			{
				calc.multiply(i);
			}
		}
		else
		{
			return false;
		}

		return true;
	}
}
