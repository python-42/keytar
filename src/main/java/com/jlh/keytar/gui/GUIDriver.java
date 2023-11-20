package com.jlh.keytar.gui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlh.keytar.common.IPCMessage;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUIDriver extends Application{
    private static ServerIPC ipc;
    private static ByteArrayOutputStream log;
    private static Logger logger;

    private FXMLController controller;
    private ObjectMapper map;
    private IPCMessage msg;
    private char[][][] keyset;
    

    public GUIDriver(int port, String[] args, ByteArrayOutputStream log) throws IOException {
        logger = LogManager.getLogger(GUIDriver.class);
        ipc = new ServerIPC(port);
        GUIDriver.log = log;
        Application.launch(GUIDriver.class, args);
    }

    public GUIDriver() {}

    @Override
    public void start(Stage stage) throws Exception {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        stage.setScene(new Scene(root));
        stage.show();

        new Thread(
            () -> {try {
                beginListening();
            } catch (IOException e) {
                e.printStackTrace();
            }}
        ).start();
        
    }

 

    private void beginListening() throws IOException{
        logger.info("Began listening for events from IPC.");
        boolean awaitingStartup = true;
        String rawMsg;
        

        while(true) {
            controller.setLogView(log.toString().replaceAll("\u001B\\[[;\\d]*m", ""));//remove ascii color characters

            rawMsg = ipc.getMessage();

            if(rawMsg == null) {
                continue;
            }
            
            logger.trace("Message recieved from IPC: " + rawMsg);
            
            msg = parseJSON(rawMsg);

            logger.trace("JSON parsed to " + msg);

            if(awaitingStartup) {
                if(msg.getName().equals("startup")) {
                    logger.debug("Startup message recieved, begin to interpret messages from IPC");

                    keyset = readKeyset(msg.getData());

                    String[] keysetName = getKeysetName(keyset);


                    Platform.runLater(() -> { 
                        controller.setKeyset(keyset[0]); 
                        controller.setKeysetList(keysetName);
                    });
                    

                    awaitingStartup = false;
                }
                continue;
            }

            switch (msg.getName()) {
                case "end": 
                    System.exit(0);
                    break;
                
                case "char_discard": 
                    Platform.runLater(
                        () -> {controller.setCurrentChar("??");}
                    );
                    break;

                case "char_sent": 
                    Platform.runLater(
                        () -> {controller.blinkCurrentChar();}
                    );
                    break;
                case "caps":
                    Platform.runLater(
                        () -> {controller.setCapsDisplay(msg.getData().toLowerCase().equals("true"));}
                    );
                    break; 

                case "row_sel":
                    int i = Integer.parseInt(msg.getData());
                    Platform.runLater(
                        () -> 
                        {
                            controller.setHighlightedRow(i);
                        }
                    );
                    break;

                case "col_sel":
                    Platform.runLater(
                        () -> {controller.setCurrentChar(Integer.parseInt(msg.getData()));}
                    );
                    break;

                case "keymap":
                    Platform.runLater(
                        () -> {controller.setKeyset(keyset[Integer.parseInt(msg.getData())]);}
                    );
                    break;


            }
            

        }
    }

    private String[] getKeysetName(char[][][] keyset) {
        String[] rtn = new String[keyset.length];

        for (int i = 0; i < rtn.length; i++) {
            rtn[i] = (char)keyset[i][0][0] + "";
            rtn[i] += (char)keyset[i][0][1] + "";
            rtn[i] += (char)keyset[i][0][2] + "";
        }
        return rtn;
    }

    private IPCMessage parseJSON(String in) {
        if(map == null) {
            map = new ObjectMapper();
        }

        try {
            return map.readValue(in, IPCMessage.class);
        } catch (JsonProcessingException e) {
            logger.error("An error occured while parsing JSON string " + in);
            e.printStackTrace();
        }

        return null;

    }

    private char[][][] readKeyset(String input) throws IOException {    
        if(map == null) {
            map = new ObjectMapper();
        }    
        return map.readValue(input, char[][][].class);

    }


}