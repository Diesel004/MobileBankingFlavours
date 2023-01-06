package com.trustbank.tcpconnection.tcp;

import com.trustbank.tcpconnection.util.ResponseEntity;
import com.trustbank.util.TrustMethods;

import java.lang.*;
import java.io.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class TcpClient {
    public String ServerIP;
    public int Port;

    public TcpClient(String serverIp, int port) {
        this.ServerIP = serverIp;
        this.Port = port;
    }

  /*  public ResponseEntity SendData(String data) {
        String inMsg = "";
        try {
            String outMsg = data;
            // Create Socket for 192.168.1.2 at port 1234
            Socket socket = new Socket(this.ServerIP, this.Port);
            // write message to the server,
            BufferedWriter socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            socketWriter.write(outMsg);
            socketWriter.flush();
            // Read and display the message from the server
            BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            inMsg = TcpClient.ReadAll(socketReader);// socketReader.readLine();
            //inMsg = socketReader.readLine();
            TrustMethods.systemMessage("inMsg:\n " + inMsg);
            socket.close();
            return ResponseEntity.CreateSuccess(inMsg);
        } catch (Exception e1) {
            e1.printStackTrace();
            return ResponseEntity.CreateError("9999", e1.getMessage(), e1);
        }
    }*/

    /*public static String ReadAll(BufferedReader bufferedReader) throws IOException {
        char[] res = new char[1024 * 5];
        String contents = "";
        bufferedReader.read(res, 0, res.length);
        contents = new String(res);
        contents = contents.replace("\0", "");
        return contents;
    }*/
}