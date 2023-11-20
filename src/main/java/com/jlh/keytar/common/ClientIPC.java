package com.jlh.keytar.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientIPC {
    
    protected Socket socket;
    protected final BufferedWriter out;
    protected final BufferedReader in;
    

    public ClientIPC(int port) throws UnknownHostException, IOException {
        socket = new Socket("localhost", port);
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    protected ClientIPC(Socket socket) throws IOException {
        this.socket = socket;
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

    }

    public void sendMessage(String message) throws IOException {
        out.append(message);
        out.newLine();
        out.flush();
    }

    public String getMessage() throws IOException {
        return in.readLine();
    }

    public void close() throws IOException {
        out.close();
        in.close();
        socket.close();

    }
    
}
