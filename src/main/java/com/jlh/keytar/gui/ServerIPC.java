package com.jlh.keytar.gui;

import java.io.IOException;
import java.net.ServerSocket;

import com.jlh.keytar.common.ClientIPC;

public class ServerIPC extends ClientIPC{

    @SuppressWarnings("resource")
    public ServerIPC(int port) throws IOException{
        
        super(new ServerSocket(port).accept());
    }
    
    
}
