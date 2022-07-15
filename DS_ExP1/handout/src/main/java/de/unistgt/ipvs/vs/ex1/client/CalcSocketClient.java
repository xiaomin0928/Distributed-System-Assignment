package de.unistgt.ipvs.vs.ex1.client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Implement the connectTo-, disconnect-, and calculate-method of this class
 * as necessary to complete the assignment. You may also add some fields or methods.
 */
public class CalcSocketClient {
	private Socket cliSocket;
	private int    rcvdOKs;		// --> Number of valid message contents
	private int    rcvdErs;		// --> Number of invalid message contents
	private int    calcRes;		// --> Calculation result (cf.  'RES')

	//output stream
	private OutputStream outStr;
	private PrintWriter priWriter;

	//input stream
	private InputStream inStr;
	private InputStreamReader inStrReader;
	private BufferedReader buffReader;

	//process flag
	final int NOFIN = 0; 	//server has not process all messages
	final int FIN = 1; 		//server has processed all messages
	final int ERR = 2;      //the message from server has errors


	public CalcSocketClient() {
		this.cliSocket = null;
		this.rcvdOKs   = 0;
		this.rcvdErs   = 0;
		this.calcRes   = 0;

		//IO stream init
		this.outStr = null;
		this.priWriter = null;
		this.inStr = null;
		this.inStrReader = null;
		this.buffReader = null;
	}
	
	public int getRcvdOKs() {
		return rcvdOKs;
	}

	public int getRcvdErs() {
		return rcvdErs;
	}

	public int getCalcRes() {
		return calcRes;
	}

	/**
	 * @method connectTo
	 * @discription client connect to server
	 * @param srvIP:server IP
	 * @param srvPort:socket port
	 * @return true: client connect to server successfully
	 *         false: client connect to server failed
	 */
	public boolean connectTo(String srvIP, int srvPort) {
               
		//Solution here
		try {
			//create a socket
			cliSocket = new Socket(srvIP, srvPort);
			//get output stream
			outStr = cliSocket.getOutputStream();
			priWriter = new PrintWriter(outStr);
			//get input stream
			inStr = cliSocket.getInputStream();
			inStrReader = new InputStreamReader(inStr);
			buffReader = new BufferedReader(inStrReader);

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * @method disconnect
	 * @discription client disconnect with server
	 * @return true: disconnect successfully
	 *         failed: disconnect failed
	 */
	public boolean disconnect() {
               
	    //Solution here
		//close the connection
		try {
			//close input stream
			buffReader.close();
			inStrReader.close();
			inStr.close();
			//close output stream
			priWriter.close();
			outStr.close();
			//close socket
			cliSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * @method calculate
	 * @discription client send request to server to calculate
	 * @param request: the request sent to server
	 * @return
	 */
	public boolean calculate(String request) {
               
		if (cliSocket == null) {
			System.err.println("Client not connected!");
			return false;
		}

		//Solution here
		try {
			//send message to server
			priWriter.println(request);
			priWriter.flush();

			//receive message from server
			String strBuffer = null;
			while ((strBuffer = buffReader.readLine()) != null)
			{

				System.out.println("Client get message from Server:" + strBuffer);
				//analyze message received from server
				if( analyzeServerMessage(strBuffer) == FIN )
				{
					break; // read message from server until server processed all messages
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}



		return true;
	}

	/**
	 * @method analyzeServerMessage
	 * @discription analyze message received from server
	 * @param strBuffer:message received from server
	 * @return NOFIN: server has not processed all messages
	 *         FIN: server has processed all messages
	 *         ERR: server sent invalid messages
	 */
	public int analyzeServerMessage(String strBuffer)
	{
		//get string inside "<>"
		String message = strBuffer.substring(strBuffer.indexOf("<"),strBuffer.indexOf(">")+1);

		//check if the two digit integer equals the total message length
		int strLen = Integer.parseInt(message.substring(1,3));
		if( strLen != message.length() )
		{
			System.out.println("Client get invalid message from Server:" + message);
			return ERR;
		}

		//check if a colon ':' followed
		if( message.charAt(3) != ':' )
		{
			System.out.println("Client get invalid message from Server:" + strBuffer);
			return NOFIN;
		}

		//get message content(cf.<07:OK> content is OK)
		String fullMessageContent = message.substring(message.indexOf(":")+1,message.length()-1);
		//split message with one or more whitespace
		String[] singleMessage = fullMessageContent.split("\\s+");
		//analyse every message content
		for( int i = 0; i<singleMessage.length; i++)
		{
			if (isNumeric(singleMessage[i]) || isLetter(singleMessage[i]))
			{
				//"OK" response, valid acknowledgement
				if( singleMessage[i].equalsIgnoreCase("OK") )
				{
					rcvdOKs++;
				}
				//"ERR" response,invalid message
				else if(singleMessage[i].equalsIgnoreCase("ERR"))
				{
					rcvdErs++;
				}
				//"RES" response,get calculation result
				else if(singleMessage[i].equalsIgnoreCase("RES"))
				{
					if(isNumeric(singleMessage[i+1]))
					{
						calcRes = Integer.parseInt(singleMessage[i+1]);
						i++;
					}
				}
				//"FIN" response, server has processed every message content
				else if(singleMessage[i].equalsIgnoreCase("FIN"))
				{
					return FIN;
				}
				//other operator
				else if(singleMessage[i].equalsIgnoreCase("RDY") || //"RDY" response, client has connected to server
						singleMessage[i].equalsIgnoreCase("ADD") || //"ADD" operator
						singleMessage[i].equalsIgnoreCase("SUB") || //"SUB" operator
						singleMessage[i].equalsIgnoreCase("MUL") || //"MUL" operator
						isNumeric(singleMessage[i])) {
					//do nothing
				}
				//invalid messages
				else
				{
					System.out.println("Client get invalid message from Server:" + singleMessage[i]);
					return ERR;
				}
			}
		}

		return NOFIN;
	}

	/**
	 * @method isNumeric
	 * @discription check if the string only contains numbers
	 * @param str: a string
	 * @return true: the string only contains numbers
	 *         false: the string does not only contains numbers
	 */
	public boolean isNumeric(String str)
	{
		if(!str.matches("-?\\d+"))
		{
			return false;
		}
		return true;
	}

	/**
	 * @method isLetter
	 * @discription check if the string only contains letters
	 * @param str: a string
	 * @return true: the string only contains letters
	 *         false: the string does not only contains letters
	 */
	public boolean isLetter(String str)
	{
		for(int i = 0;i<str.length();i++)
		{
			if(!Character.isLetter(str.charAt(i)))
			{
				return false;
			}
		}
		return true;
	}
}
