package com.jlh.keytar.api;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

public class KeyboardController {
    
    private final Robot robot;
    private final KeyboardMap map;

    private final int mouseSpeed = 10;
    
    public KeyboardController(File config) throws AWTException, IOException {
        robot = new Robot();
        map = new KeyboardMap(config);
    }

    public void pressKey(Coordinate coord, boolean caps) {
        pressKey(map.getCharacter(coord.getX(), coord.getY(), caps));
    }

    public void pressKey(char key) {
        if(Character.isUpperCase(key)) {
            robot.keyPress(KeyEvent.VK_SHIFT);
        }
            robot.keyPress(KeyEvent.getExtendedKeyCodeForChar(key));
            robot.keyRelease(KeyEvent.getExtendedKeyCodeForChar(key));
            robot.keyRelease(KeyEvent.VK_SHIFT);
        
        
    }

    public void pressArrowKey(ArrowKey key) {
        robot.keyPress(key.value);
        robot.keyRelease(key.value);
    }

    public void moveMouse(double xPower, double yPower) {
        Point cursor = MouseInfo.getPointerInfo().getLocation();
        robot.mouseMove(getMouseCoord(cursor.x, xPower), getMouseCoord(cursor.y, yPower));
    }

    private int getMouseCoord(int start, double power) {
        return (int)(start + (mouseSpeed * power));
    }

    public void click() {
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    public int nextKeymap() {
        if(!map.setKeymap(map.getCurrentKeyset() + 1)) {
            map.setKeymap(0);
        }
        return map.getCurrentKeyset();
    }

    public char[][][] getKeyset() {
        return map.getKeyset();
    }

    public enum ArrowKey {
        LEFT(37),
        UP(38),
        RIGHT(39),
        DOWN(40);
        
        public int value;
        private ArrowKey(int value) {
            this.value = value;
        }
    }
    

}
