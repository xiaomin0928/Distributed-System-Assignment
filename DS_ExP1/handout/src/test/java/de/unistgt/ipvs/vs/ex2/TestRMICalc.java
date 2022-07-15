package de.unistgt.ipvs.vs.ex2;


import de.unistgt.ipvs.vs.ex2.client.CalcRmiClient;
import de.unistgt.ipvs.vs.ex2.client.CalculationMode;
import de.unistgt.ipvs.vs.ex2.server.CalcRmiServer;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.rules.Timeout;

import javax.naming.NamingException;

/**
 *
 * @author bibartoo
 */
public class TestRMICalc {
    
    public Timeout globalTimeout = Timeout.seconds(30);
    
    @Test
    public void test1() throws RemoteException, MalformedURLException, NotBoundException {
        String srvIP   = "localhost";	// "127.0.0.1"
        int    srvPort = 22345;
        
    
        CalcRmiServer cSrv = new CalcRmiServer(srvIP, "sessionFactoryTest1");
        cSrv.start();
        
        try {
                Thread.sleep(3000);
        } catch (InterruptedException e) {
                e.printStackTrace();
        }


        String url = "//" + srvIP + "/sessionFactoryTest1";
        CalcRmiClient csCli = new CalcRmiClient();
        csCli.init(url);

        
        Collection<Integer> numbers1_1 = new ArrayList<Integer>(3);
	numbers1_1.add(1); numbers1_1.add(2); numbers1_1.add(3);
        csCli.calculate(CalculationMode.ADD, numbers1_1);

        
        Collection<Integer> numbers1_2 = new ArrayList<Integer>(3);
	numbers1_2.add(3); numbers1_2.add(2); numbers1_2.add(1);
        csCli.calculate(CalculationMode.SUB, numbers1_2);
                
        assertEquals(0,csCli.getCalcRes());
        
        cSrv.stopServer();
    }
    
    @Test
    public void test2() throws RemoteException, MalformedURLException, NotBoundException {
        String srvIP   = "localhost";	// "127.0.0.1"
        int    srvPort = 22345;
        
    
        CalcRmiServer cSrv = new CalcRmiServer(srvIP, "sessionFactoryTest2");
        cSrv.start();
        
        try {
                Thread.sleep(3000);
        } catch (InterruptedException e) {
                e.printStackTrace();
        }


        String url = "//" + srvIP + "/sessionFactoryTest2";
        CalcRmiClient csCli = new CalcRmiClient();
        csCli.init(url);

        
        Collection<Integer> numbers1_1 = new ArrayList<Integer>(3);
	numbers1_1.add(1); numbers1_1.add(2); numbers1_1.add(3);
        csCli.calculate(CalculationMode.ADD, numbers1_1);

        
        Collection<Integer> numbers1_2 = new ArrayList<Integer>(3);
	numbers1_2.add(3); numbers1_2.add(2); numbers1_2.add(1);
        csCli.calculate(CalculationMode.SUB, numbers1_2);
                
        assertEquals(0,csCli.getCalcRes());
        
        cSrv.stopServer();
    }
}
