package de.unistgt.ipvs.vs.ex1;

import de.unistgt.ipvs.vs.ex1.client.CalcSocketClient;
import de.unistgt.ipvs.vs.ex1.server.CalcSocketServer;
import java.io.IOException;
import static org.junit.Assert.*;
import org.junit.Rule;

import org.junit.Test;
import org.junit.rules.Timeout;


public class TestCalc {
    
    @Rule
    public Timeout globalTimeout = Timeout.seconds(30);

    @Test
    public void test1() {
         // Start Client
        String srvIP   = "localhost"; //"127.0.0.1";
        int      srvPort = 12345;

        // Start Server
        CalcSocketServer cSrv = new CalcSocketServer(srvPort);
        cSrv.start();
        cSrv.waitUnitlRunnig();

        CalcSocketClient cCli = new CalcSocketClient();
        cCli.connectTo(srvIP, srvPort);

        //Test 
        String req1 = "ADD 1 2 3 SUB 3 2 1";
        cCli.calculate("<" + (req1.length() + 5) + ":" + req1 + ">");
        cCli.calculate("<08:rEs>");

        assertEquals(0,cCli.getCalcRes());
        assertEquals(11,cCli.getRcvdOKs());

        cCli.disconnect();
    }

    @Test
    public void test2() {
         // Start Client
        String srvIP = "localhost"; //"127.0.0.1";
        int srvPort = 12346;

        // Start Server
        CalcSocketServer cSrv = new CalcSocketServer(srvPort);
        cSrv.start();
        cSrv.waitUnitlRunnig();

        CalcSocketClient cCli = new CalcSocketClient();
        cCli.connectTo(srvIP, srvPort);

        //Test 
        String req1 = "Add -3 ASM -2 ABC -1 SUB -1 ASM10 -2 ABC09 -3";
        cCli.calculate("<" + (req1.length() + 5) + ":" + req1 + ">");
        cCli.calculate("<08:rEs>");

        assertEquals(0, cCli.getCalcRes());

        cCli.disconnect();
    }


    @Test
    public void test3() {
         // Start Client
        String srvIP   = "localhost"; //"127.0.0.1";
        int      srvPort = 12347;

        // Start Server
        CalcSocketServer cSrv = new CalcSocketServer(srvPort);
        cSrv.start();
        cSrv.waitUnitlRunnig();

        CalcSocketClient cCli = new CalcSocketClient();
        cCli.connectTo(srvIP, srvPort);

        //Test 
        String req31 = "  MUL  1   ASM  ADD ABC 10    5  SUB 100 ADD10   ADD";
        cCli.calculate("24 foo 42 <" + (req31.length() + 5) + ":" + req31 + ">");

        String req32 = "60 4 MUL -2 RES  ";
        cCli.calculate("a faq 23 <" + (req32.length() + 5) + ":" + req32 + "> bla 42 ");
       
        
        assertEquals(42,cCli.getCalcRes());
        assertEquals(3, cCli.getRcvdErs());
        
        cCli.disconnect();
    }

}