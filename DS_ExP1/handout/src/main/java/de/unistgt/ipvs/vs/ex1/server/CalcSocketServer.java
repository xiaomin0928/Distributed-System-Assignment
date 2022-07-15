package de.unistgt.ipvs.vs.ex1.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Extend the run-method of this class as necessary to complete the assignment.
 * You may also add some fields, methods, or further classes.
 */
public class CalcSocketServer extends Thread {
	private ServerSocket srvSocket;
	private int port;

	public CalcSocketServer(int port) {
		this.srvSocket = null;
		
		this.port = port;
	}
	
	@Override
	public void interrupt() {
		try {
			if (srvSocket != null) srvSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
           
		if (port <= 0) {
			System.err.println("Wrong number of arguments.\nUsage: SocketServer <listenPort>\n");
			System.exit(-1);
		}

		//bind socket
		try {
			srvSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Start listening server socket ..
		while ( true )
		{
			try {
				Socket clientSocket = srvSocket.accept();
				if( clientSocket != null && !clientSocket.isClosed())
				{
					//create a new session when connecting to client
					new Thread(new CalculationSession(clientSocket)).start();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
        
        public void waitUnitlRunnig(){
            while(this.srvSocket == null){
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                }
            }
        }
}