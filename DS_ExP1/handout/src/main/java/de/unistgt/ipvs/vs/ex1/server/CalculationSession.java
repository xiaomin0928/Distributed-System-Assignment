package de.unistgt.ipvs.vs.ex1.server;

import de.unistgt.ipvs.vs.ex1.common.ICalculation;

import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;

/**
 * Add fields and methods to this class as necessary to fulfill the assignment.
 */
public class CalculationSession implements Runnable {
    private Socket cliSocket = null;    //client socket
    private int currOperation = 0;      //current operation,0-no operation
    private final int ADD = 1;          //add operation flag
    private final int SUB = 2;          //subtract operation flag
    private final int MUL = 3;          //multiply operation flag
    private int currResult = 0;         //current calculation result

    public CalculationSession(Socket clientSocket) {
        this.cliSocket = clientSocket;
    }

    @Override
    public void run() {

        try {
            System.out.println("a new session for client");
            //send message to client
            OutputStream outStr = cliSocket.getOutputStream();
            PrintWriter priWriter = new PrintWriter(outStr);
            if( cliSocket != null ) //client has connected
            {
                System.out.println("Client has connected");
                //After a client has connected to the server,the server sends the content "RDY" to the client
                sendMessageToClient("RDY",priWriter);
            }

            //calculation object
            CalculationImpl calculation = new CalculationImpl();

            //receive message from client
            InputStream inStr = cliSocket.getInputStream();
            InputStreamReader inStreamReader = new InputStreamReader(inStr);
            BufferedReader buffReader = new BufferedReader(inStreamReader);
            String strBuffer = null;
            while ( !cliSocket.isClosed() )
            {
                //read message from client until disconnected
                if( (strBuffer = buffReader.readLine())== null)
                {
                    break;
                }
                System.out.println("Server get message from Client:" + strBuffer);

                //For each received message, the server sends immediately a response with the content "OK"
                sendMessageToClient("OK",priWriter);

                //analyze message received from client
                if( analyzeClientMessage(strBuffer,priWriter,calculation) == false )
                {
                    System.out.println("Server get invalid message from Client:" + strBuffer);
                }
            }
            //close IO stream
            if( cliSocket.isClosed() )
            {
                //close input stream
                buffReader.close();
                inStreamReader.close();
                inStr.close();
                //close output stream
                priWriter.close();
                outStr.close();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @method analyzeClientMessage
     * @discription analyze message received from client
     * @param strBuffer: message received from server
     * @param priWriter: socket output stream
     * @param calculation: calculation object
     * @return true: get valid message from client and analyze successfully
     *         false: get invalid message from client
     * @throws RemoteException
     */
    public boolean analyzeClientMessage(String strBuffer,PrintWriter priWriter,CalculationImpl calculation) throws RemoteException {

        //get string inside "<>"
        String message = strBuffer.substring(strBuffer.indexOf("<"),strBuffer.indexOf(">")+1);

        //check if the two digit integer equals the total message length
        int strLen = Integer.parseInt(message.substring(1,3));
        if( strLen != message.length() )
        {
            sendMessageToClient("ERR" + " " + message,priWriter);
            System.out.println("Server get invalid message from Client:" + strBuffer);
            return false;
        }

        //check if a colon ':' followed
        if( message.charAt(3) != ':' )
        {
            sendMessageToClient("ERR" + " " + message,priWriter);
            System.out.println("Server get invalid message from Client:" + strBuffer);
            return false;
        }

        //get message content(cf.<08:ADD 123> content is ADD 123)
        String fullMessageContent = message.substring(message.indexOf(":")+1,message.length()-1);
        //Remove whitespace at the beginning and the end
        fullMessageContent = fullMessageContent.trim();
        //split message with one or more whitespace
        String[] singleMessage = fullMessageContent.split("\\s+");
        for( int i = 0; i<singleMessage.length; i++)
        {
            if (isNumeric(singleMessage[i]) || isLetter(singleMessage[i]))
            {
                //add operation
                if( singleMessage[i].equalsIgnoreCase("ADD") )
                {
                    currOperation = ADD;
                    //validity acknowledgement
                    sendMessageToClient("OK" + " " + singleMessage[i],priWriter);

                }
                //subtract operation
                else if(singleMessage[i].equalsIgnoreCase("SUB"))
                {
                    currOperation = SUB;
                    //validity acknowledgement
                    sendMessageToClient("OK" + " " + singleMessage[i],priWriter);
                }
                //multiply operation
                else if(singleMessage[i].equalsIgnoreCase("MUL"))
                {
                    currOperation = MUL;
                    //validity acknowledgement
                    sendMessageToClient("OK" + " " + singleMessage[i],priWriter);
                }
                //"RES" response,get calculation result
                else if(singleMessage[i].equalsIgnoreCase("RES"))
                {
                    currResult = calculation.getResult();
                    //send result to client
                    sendMessageToClient("OK" + " " + "RES" + " " + currResult,priWriter);

                }
                //a single integer value
                else if(isNumeric(singleMessage[i]))
                {
                    int number = Integer.parseInt(singleMessage[i]);
                    //calculate according to current calculation operator
                    switch (currOperation)
                    {
                        case ADD:
                            calculation.add(number);
                            break;
                        case SUB:
                            calculation.subtract(number);
                            break;
                        case MUL:
                            calculation.multiply(number);
                            break;
                        default:
                            break;
                    }
                    //validity acknowledgement
                    sendMessageToClient("OK" + " " + singleMessage[i],priWriter);
                }
                //invalid messages
                else
                {
                    sendMessageToClient("ERR" + " " + singleMessage[i],priWriter);
                    System.out.println("Server get invalid message from Client:" + singleMessage[i]);
                }
            }
            //invalid messages
            else
            {
                sendMessageToClient("ERR" + " " + singleMessage[i], priWriter);
                System.out.println("Server get invalid message from Client:" + singleMessage[i]);
            }
        }
        //After processing every message, server sends the content "FIN" to client
        sendMessageToClient("FIN",priWriter);
        return true;
    }

    /**
     * @method send message to client
     * @discription lookup a remote object
     * @param strRes:response message
     * @param priWriter:socket output stream
     * @return true: message is valid,analyze the response ("OK","ERR","FIN","RES") from client
     *         false: message is invalid
     */
    public boolean sendMessageToClient(String strRes,PrintWriter priWriter)
    {
        if( strRes == null || priWriter == null )
        {
            return false;
        }
        int iLen = strRes.length()+5;
        String strLen = null;
        //if number smaller than 10,add '0' before it (cf.'9'->'09')
        if( iLen < 10)
        {
            strLen = "0" + iLen;
        }
        else
        {
            strLen = iLen + "";
        }
        priWriter.println("<" + strLen + ":" + strRes + ">");
        priWriter.flush();
        return true;
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