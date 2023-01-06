package com.trustbank.tcpconnection;

import android.util.Log;

import com.trustbank.util.TrustMethods;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class TcpClientOld {

//    public static final String SERVER_IP = "192.168.0.6"; //your computer IP address
    public static final String SERVER_IP = "117.247.82.141"; //your computer IP address
    public static final int SERVER_PORT = 2150;
    // message to send to the server
    private String mServerMessage;
    private OnMessageReceived mMessageListener = null;
    private boolean mRun = false;
    private PrintWriter mBufferOut;
    private BufferedReader mBufferIn;
    private Socket s;

    /**
     * Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TcpClientOld(OnMessageReceived listener) {
        mMessageListener = listener;
    }

    /**
     * Sends the message entered by client to the server
     *
     * @param message text entered by client
     */
    public void sendMessage(String message) {
        if (mBufferOut != null && !mBufferOut.checkError()) {
            mBufferOut.println(message);
            mBufferOut.flush();
        }
    }

    /**
     * Close the connection and release the members
     */
    public void stopClient() {

        // send message that we are closing the connection
        sendMessage(Constants.CLOSED_CONNECTION+"Mono");

        mRun = false;

        if (mBufferOut != null) {
            mBufferOut.flush();
            mBufferOut.close();
        }

        mMessageListener = null;
        mBufferIn = null;
        mBufferOut = null;
        mServerMessage = null;
    }

    public void run() {

        mRun = true;

            try {
                s = new Socket(InetAddress.getByName(TcpClientOld.SERVER_IP), 2150);
                mBufferIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
                mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);
                String outMsg = "TCP connecting to ";
                mBufferOut.write(outMsg);
                mBufferOut.flush();
                Log.i("TcpClientOld", "sent: " + outMsg);

                StringBuffer stringBuffer = new StringBuffer();
                String line = null;

                while (true) {
                    final String line1 = mBufferIn.readLine();
                    if (line1 == null) break;
                    stringBuffer.append(line1).append("\n");
//                    doSomething(stringBuffer);
                    mServerMessage = stringBuffer.toString();
                    mMessageListener.messageReceived(mServerMessage);
                }

                TrustMethods.LogMessage("data1",stringBuffer.toString());

                mBufferIn.close();
//                s.close();

          /*      //sends the message to the server
                mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                //receives the message which the server sends back
                mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                // send login name
                sendMessage(Constants.LOGIN_NAME+"Mono");

                //in this while the client listens for the messages sent by the server
                while (mRun) {

                    mServerMessage = mBufferIn.readLine();

                    if (mServerMessage != null && mMessageListener != null) {
                        //call the method messageReceived from MyActivity class
                        mMessageListener.messageReceived(mServerMessage);
                    }

                }*/

                TrustMethods.LogMessage("RESPONSE FROM SERVER", "S: Received Message: '" + mServerMessage + "'");

            } catch (Exception e) {

                TrustMethods.LogMessage("TCP", "S: Error "+e.getMessage());

            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket

                // after it is closed, which means a new socket instance has to be created.
                if (s != null){
                try {
                        s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                }
            }


    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
}