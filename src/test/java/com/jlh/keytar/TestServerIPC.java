package com.jlh.keytar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.jlh.keytar.gui.ServerIPC;

class TestServerIPC {
    private final static int PORT = 2003; //use a different port from other cases to ensure they do not fail
    private static ServerIPC ipc;
    private static Socket socket;
    private static BufferedReader in;

    @BeforeAll static void init() throws UnknownHostException, IOException, InterruptedException {
        
        new Thread(() -> {
            try {
                ipc = new ServerIPC(PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }}
        ).start();
        Thread.sleep(1000);

        socket = new Socket("localhost", PORT);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
    }

    @Test void testSendMessage() throws IOException {
        ipc.sendMessage("Hello");
        assertEquals("Hello", in.readLine());

        ipc.sendMessage("1234567890!@#$%^&*()-_=+");
        assertEquals("1234567890!@#$%^&*()-_=+", in.readLine());
    }

    @Test void testRecieveMessage() throws IOException {
        socket.getOutputStream().write('x');
        socket.getOutputStream().write('y');
        socket.getOutputStream().write('z');
        socket.getOutputStream().write('\n');
        socket.getOutputStream().flush();

        assertEquals("xyz", ipc.getMessage());
    }

    @AfterAll static void cleanup() throws IOException {
        ipc.close();
        in.close();
        socket.close();
    }
    
}
