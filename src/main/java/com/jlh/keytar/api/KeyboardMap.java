package com.jlh.keytar.api;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class  KeyboardMap {
    
    private int keyset = 0;
    private final char[][][] keys;
    private final Logger logger;

    public KeyboardMap(File configFile) throws IOException {
        ObjectMapper m = new ObjectMapper();
        JsonNode config = m.readTree(configFile);

        keys = m.readValue(config.get("keys").traverse(), char[][][].class);

        logger = LogManager.getLogger(KeyboardMap.class);
    }

    public Character getCharacter(int row, int col, boolean caps) {
        if(row > keys[keyset].length -1 || col > keys[keyset][row].length -1) {
            return null;
        }
        if(caps) {
            return Character.toUpperCase(keys[keyset][row][col]);
        }else {
            return Character.toLowerCase(keys[keyset][row][col]);
        }
    }

    public int getCurrentKeyset() {
        return keyset;
    }

    public int getKeysetCount() {
        return keys.length;
    }

    public char[][][] getKeyset() {
        return keys;
    }

    /**
     * Set the keymap to the desired keyset. If the keyset does not exist, the keyset is not changed.
     * @param desired desired keymap
     * @return true if the keymap exists, false otherwise
     */
    public boolean setKeymap(int desired) {
        if(desired >= 0 && desired < keys.length) {
            keyset = desired;
            logger.debug("Keymap switched to " + keyset);
            return true;
        }else {
            return false;

        }
    }

}
