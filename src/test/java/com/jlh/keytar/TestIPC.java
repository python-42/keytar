package com.jlh.keytar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.jlh.keytar.common.ClientIPC;
import com.jlh.keytar.gui.ServerIPC;

class TestIPC {
    private final static int PORT = 2001; //use a different port from other cases to ensure they do not fail
    private static ClientIPC client;
    private static ServerIPC server;

    @BeforeAll static void init() throws UnknownHostException, IOException, InterruptedException {
        new Thread(
            () -> {
                try {
                    server = new ServerIPC(PORT);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        ).start();
        Thread.sleep(1000);
        
        client = new ClientIPC(PORT);
    }

    @Test void testClientSend() throws IOException {
        client.sendMessage("hello there");
        client.sendMessage("!@#$%^&*()");
        assertEquals("hello there", server.getMessage());
        assertEquals("!@#$%^&*()", server.getMessage());
        
    }

    @Test void testServerSend() throws IOException {
        server.sendMessage("hello there");
        server.sendMessage("!@#$%^&*()");
        assertEquals("hello there", client.getMessage());
        assertEquals("!@#$%^&*()", client.getMessage());
    }

    @AfterAll static void cleanup() throws IOException {
        client.close();
        server.close();
    }

    
}
