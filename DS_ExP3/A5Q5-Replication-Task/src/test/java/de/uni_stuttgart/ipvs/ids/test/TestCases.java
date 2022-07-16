package de.uni_stuttgart.ipvs.ids.test;


import de.uni_stuttgart.ipvs.ids.replication.MajorityConsensus;
import de.uni_stuttgart.ipvs.ids.replication.QuorumNotReachedException;
import de.uni_stuttgart.ipvs.ids.replication.Replica;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Rule;

import org.junit.Test;
import org.junit.rules.Timeout;


public class TestCases {
    
    @Rule
    public Timeout globalTimeout = Timeout.seconds(30);
    
    
    @Test
    public void test1() throws SocketException {
        
        int replicaPort = 11000;
        int noReplicas = 10;
        double prob = 1; // Success Probability
        double value = 2.0;

        List<Replica<Double>> replicas = new ArrayList<Replica<Double>>(noReplicas);
        List<SocketAddress> replicaAddrs = new ArrayList<SocketAddress>(replicas.size());

        for (int i = 0; i < noReplicas; i++) {
            Replica<Double> r = new Replica<Double>(i, replicaPort + i, prob, value);
            System.out.println("listenPort"+ (replicaPort + i) + "");
            r.start();
            replicas.add(r);
            replicaAddrs.add(r.getSocketAddress());
        }

        MajorityConsensus<Double> mc = new MajorityConsensus<Double>(replicaAddrs, 3404);

        try {
            double y = mc.get().getValue();

            assertEquals(2.0, y, 0.0);
            mc.set(3.0);
            y = mc.get().getValue();
            assertEquals(3.0, y, 0.0);
      
        } catch (QuorumNotReachedException | IOException | ClassNotFoundException e) {
            assertTrue(false);
        }
    }

    @Test
    public void test2() throws SocketException {
        
        int replicaPort = 9000;
        int noReplicas = 10;
        double prob = 0.001; // Success Probability
        double value = 2.0;

        List<Replica<Double>> replicas = new ArrayList<Replica<Double>>(noReplicas);
        List<SocketAddress> replicaAddrs = new ArrayList<SocketAddress>(replicas.size());

        for (int i = 0; i < noReplicas; i++) {
            Replica<Double> r = new Replica<Double>(i, replicaPort + i, prob, value);
            r.start();
            replicas.add(r);
            replicaAddrs.add(r.getSocketAddress());
        }

        MajorityConsensus<Double> mc = new MajorityConsensus<Double>(replicaAddrs,5404);

        try {
            double y = mc.get().getValue();
            assertTrue(false);
        } catch (QuorumNotReachedException | IOException | ClassNotFoundException e) {
            assertTrue(true);
        }
    }
    

    @Test
    public void test3() throws SocketException {
        
        int replicaPort = 7000;
        int noReplicas = 10;
        double prob = 0.8; // Success Probability
        double value = 2.0;

        List<Replica<Double>> replicas = new ArrayList<Replica<Double>>(noReplicas);
        List<SocketAddress> replicaAddrs = new ArrayList<SocketAddress>(replicas.size());

        for (int i = 0; i < noReplicas; i++) {
            Replica<Double> r = new Replica<Double>(i, replicaPort + i, prob, value);
            r.start();
            replicas.add(r);
            replicaAddrs.add(r.getSocketAddress());
        }

        MajorityConsensus<Double> mc = new MajorityConsensus<Double>(replicaAddrs, 6404);

        double a =  availabilityCheck(mc, 10);
        assertTrue(a > 0);
    }
    
    public static double availabilityCheck(MajorityConsensus<Double> mc, int tries) {
        int success = 0;
        for (int i = 0; i < tries; i++) {
            try {
                mc.get().getValue();
                // Success
                success++;
            } catch (QuorumNotReachedException | IOException | ClassNotFoundException e) {
                // Failed
            }
        }

        return (((double)success)/((double)tries));
    }
    @AfterClass
    public static void doYourOneTimeTeardown() throws InterruptedException {
        Thread.sleep(1000);
        System.exit(0);
    }   
    
}
