package com.jlh.keytar.api;

import java.awt.AWTException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlh.keytar.api.KeyboardController.ArrowKey;
import com.jlh.keytar.common.ClientIPC;
import com.jlh.keytar.common.IPCMessage;

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

public class ControllerInput {
    private final Logger logger;
    private Controller controller;
    private final KeyboardController keyboard;
    private final HashMap<String, Consumer<Float>> eventMap = new HashMap<String, Consumer<Float>>();

    private Coordinate coordinate = new Coordinate();
    private String id;

    private boolean caps = false;
    private int whammyVal;
    private ClientIPC ipc = null;

    private ObjectMapper map = new ObjectMapper();
    
    public ControllerInput(File config, ClientIPC ipc) throws AWTException, IOException{
        this.ipc = ipc;
        logger = LogManager.getLogger(ControllerInput.class);
        keyboard = new KeyboardController(config);
        controllerSetup();
        eventMapSetup();
        eventHandler();
    }

    private void controllerSetup() {
        int index = -1;
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
        logger.info( controllers.length + " controllers loaded");
        if (controllers.length > 0) {
            for (int i = 0; i < controllers.length; i++) {
                if (controllers[i].getName().contains("Guitar Hero")) {
                    index = i;
                }
            }

            if (index == -1) {
                logger.error("Guitar Hero controller not found, exiting... (Default controller ID: " + controllers[0].getName() + ")");
                System.exit(1);
            }
            controller = controllers[index];
            logger.info("Using controller " + controller.getName());
        }else {
            logger.error("No controller connected, exiting...");
            System.exit(2);
        }
        logger.debug("Controller setup complete");
    }

    private void eventMapSetup() {
        eventMap.put("0", this::handleCoordinateSending);
        eventMap.put("1", this::handleCoordinateSending);
        eventMap.put("2", this::handleCoordinateSending);
        eventMap.put("3", this::handleCoordinateSending);
        eventMap.put("4", this::handleCoordinateSending);
        eventMap.put("5", this::switchKeymap);

        eventMap.put("8", this::toggleCapsLock);
        eventMap.put("9", this::quit);
        eventMap.put("10", this::pressBackspace);
        eventMap.put("12", this::leftClickMouse);

        
        eventMap.put("y", this::confirmInput);
        eventMap.put("rz", this::handleWhammy);
        eventMap.put("ry", this::doNothing);
        eventMap.put("pov", this::mouseMovement);
        logger.debug("Event map setup complete, " + eventMap.size() + " keys / axes bound.");

    }

    private void eventHandler() throws IOException {
        sendDataJson("startup", keyboard.getKeyset());
        logger.debug("Event handler started.");
        EventQueue queue;
        Event e = new Event();

        while(true) {
            controller.poll();
            queue = controller.getEventQueue();
            while (queue.getNextEvent(e)) {
                id = correctComponentID(e.getComponent().getIdentifier().getName());
                eventMap.getOrDefault(
                    id, (Float f) -> {logger.debug("Component with identifier " + e.getComponent().getName() + " not bound to an action.");}
                ).accept(e.getValue());
                
                if(id != "ry") {//Accelerometer
                    logger.trace("Component " + id + " reads value " + e.getValue());
                }
            }
        }
    }

    /**
     * Component names appear to be inconsistent between operating systems, so this converts them to a universal ID
     */
    private String correctComponentID(String s) {
        switch (s) {
            case "Select":
                return "10";
            case "Mode":
                return "12";
            case "A":
                return "0";
            case "B":
                return "1";
            case "C":
                return "2";
            case "X":
                return "3";
            case "Y":
                return "4";
            case "Z":
                return "5";
            case "Left Thumb 2":
                return "8";
            case "Right Thumb 2":
                return "9";
            default:
                return s;
        }
    }

    private void handleCoordinateSending(float value) {
        if((int)value % 2 == 1) {
            coordinate.fill(fretKeyIdToCoordinate());

            if(coordinate.getCurrentFillPosition()){
                sendDataJson("col_sel", Integer.toString(fretKeyIdToCoordinate()));
            }else {
                sendDataJson("row_sel", Integer.toString(fretKeyIdToCoordinate()));
            }
        }
    }

    /**
     * Default ID's of the fret keys are super bizarre, and should be corrected.
     * Default:
     *      1 | 0
     *      2 | 4
     *      3 | K
     * 
     * Desired: 
     *      0 | 3
     *      1 | 4
     *      2 | K
     * 
     * K refers to the keyset switcher key
     * @return logical ID
     */
    private int fretKeyIdToCoordinate() {
        int rtn = Integer.parseInt(id);
        if(rtn == 0) {
            rtn = 3;
        }else if(rtn < 4) {
            rtn--;
        }

        return rtn;
    }

    private void switchKeymap(float value) {
        if((int) value % 2 == 1) {
            sendDataJson("keymap", Integer.toString(keyboard.nextKeymap()));
        }
    }

    private void toggleCapsLock(float value) {
        if((int)value % 2 == 1) {
            caps = !caps;
        }
        sendDataJson("caps", Boolean.toString(caps));
        logger.trace("Caps toggled to " + caps);
    }

    private void quit(float value) {
        logger.info("Pause button pressed, exiting...");
        sendEventJson("end");
        System.exit(0);
    }

    private void pressBackspace(float value) {
        if((int)value % 2 == 1) {
            keyboard.pressKey('\u0008'); //backspace character
        }
    }

    private void leftClickMouse(float value) {
        if((int)value % 2 == 1) {
            keyboard.click();
        }
    }

    private void confirmInput(float value) {
        if(Math.round(value) == 1) {
            if(whammyVal != 0) {
                if(whammyVal == 1) {
                    if(caps) {
                        logger.trace("Up arrow sent");
                        keyboard.pressArrowKey(ArrowKey.UP);
                        sendDataJson("arrow_sent", "U");
                        
                    }else {
                        logger.trace("Right arrow sent");
                        keyboard.pressArrowKey(ArrowKey.RIGHT);
                        sendDataJson("arrow_sent", "R");
                    }
                    
                }else {
                    if(caps) {
                        logger.trace("Down arrow sent");
                        keyboard.pressArrowKey(ArrowKey.DOWN);
                        sendDataJson("arrow_sent", "D");
                    }else {
                        logger.trace("Left arrow sent");
                        keyboard.pressArrowKey(ArrowKey.LEFT);
                        sendDataJson("arrow_sent", "L");
                    }
                }
                
            }else if(coordinate.readyToSend()) {
                logger.trace("Coordinates " + coordinate + "sent");
                keyboard.pressKey(coordinate, caps);
                sendEventJson("char_sent");
            }
        }else if(Math.round(value) == -1) {
            sendEventJson("char_discard");
            coordinate = new Coordinate();
        }
    }

    private void mouseMovement(float value) {
        keyboard.moveMouse(getXPower(value), getYPower(value));
    }

    private double getXPower(float x) {
        if(x % 1 == 0 || x + 0.5 == 0) {
            return 0;
        }

        if( x % 0.125 == 0) {
            if(x > 0.5) {
                return 0.5;
            }
            return -0.5;
        }

        if(x > 0.5) {
            return 1;
        }

        return -1;
    }

    private double getYPower(float y) {
        if(y % 1 == 0 || y + 0.5 == 0) {
            if( y > 0.5) {
                return -1;
            }
            return 1;
        }

        if( y % 0.125 == 0) {
            if(y > 0.5) {
                return -0.5;
            }
            return 0.5;
        }

        return 0;
    }

    private void handleWhammy(float value) {
        whammyVal = Math.round(value * 2);
    }

    private void doNothing(float value) {
        //To hide the accelerometer in logging
    }

    private void sendEventJson(String name) {
        try {
            if(ipc != null) {
                
                ipc.sendMessage(map.writeValueAsString(new IPCMessage("event", name)));
            }
        } catch (IOException e) {
            logger.error("Failed to send message via IPC");
            e.printStackTrace();
        }
    }

    private void sendDataJson(String name, String data) {
        try {
            if(ipc != null) {
                ipc.sendMessage(map.writeValueAsString(new IPCMessage("data", name, data)));
            }
        } catch (IOException e) {
            logger.error("Failed to send message via IPC");
            e.printStackTrace();
        }
    }

    private void sendDataJson(String name, char[][][] data) {
        try {
            sendDataJson(name, map.writeValueAsString(data));
        } catch (JsonProcessingException e) {
            logger.error("Failed to convert character arary to JSON string.");
            e.printStackTrace();
        }
    }

}
