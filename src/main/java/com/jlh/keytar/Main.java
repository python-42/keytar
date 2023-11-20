package com.jlh.keytar;

import java.awt.AWTException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ConnectException;
import java.util.Map.Entry;

import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jlh.keytar.api.ControllerInput;
import com.jlh.keytar.common.ClientIPC;
import com.jlh.keytar.gui.GUIDriver;

public class Main {
    public static void main(String[] args) throws AWTException, IOException {
        
        
        setProperties();
        ByteArrayOutputStream log;
        if(System.getProperty("keytar.ipc.redirect-stdout").equals("ON")) {
            log = redirectSysout();
        }else {
            log = new ByteArrayOutputStream();
        }
        


        Logger logger = LogManager.getLogger(Main.class);
        logger.info(System.getProperties().size() + " properties set");

        logger.debug(System.getProperty("java.library.path"));

        final int port = Integer.parseInt(System.getProperty("keytar.ipc.port"));

        if (System.getProperty("keytar.ipc.builtin-gui").equals("ON")) {
            logger.info("Using built in GUI");
            new Thread(
                () -> {
                    try {
                        new GUIDriver(port, args, log);
                    } catch (IOException e) {
                        logger.error("IOException occured while loading built in GUI");
                        e.printStackTrace();
                    }
                }
            ).start();
            
        }

        while(true) {
            try {
                new ControllerInput(
                    new File(System.getProperty("keytar.keyset-config-file")),
                    new ClientIPC(port)
                );
                break;
            }catch (ConnectException e) {
                logger.warn("Client IPC failed to connect, trying again...");
                
            }
        }

    }

    private static void setProperties() throws IOException {
        Properties props = new Properties();
        FileInputStream in = new FileInputStream("keytar.properties");
        props.load(in);
        in.close();
        for (Entry<Object, Object> x : props.entrySet()) {
            System.getProperties().putIfAbsent(x.getKey(), x.getValue());
        }
    }

    private static ByteArrayOutputStream redirectSysout() {
        ByteArrayOutputStream rtn = new ByteArrayOutputStream();
        System.setOut(new PrintStream(rtn));

        return rtn;
    }

}