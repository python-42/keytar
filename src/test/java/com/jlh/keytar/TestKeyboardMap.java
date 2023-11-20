package com.jlh.keytar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jlh.keytar.api.KeyboardMap;

class TestKeyboardMap {
    private static KeyboardMap map;

    @BeforeAll static void setup() throws IOException {

        File f = new File("src/test/resources/testKeyboardMapConfig.json");
        map = new KeyboardMap(f);
    }

    @BeforeEach void reset() {
        map.setKeymap(0);
    }

    /**
     * Switch keymap and check success and attempt to switch keymap to non-existant value
     */
    @Test void testKeymapSwitcher() {
        assertEquals(2, map.getKeysetCount());
        assertEquals(0, map.getCurrentKeyset());
        
        assertEquals(true, map.setKeymap(1));
        assertEquals(1, map.getCurrentKeyset());

        assertEquals(false, map.setKeymap(3));
        assertEquals(1, map.getCurrentKeyset());
    }

    /**
     * Test getCharacter method (core method of this class)
     */
    @Test void testGetCharacter() {
        assertEquals('A', map.getCharacter(0, 0, true));
        assertEquals('a', map.getCharacter(0, 0, false));

        assertEquals('Y', map.getCharacter(4, 4, true));
        assertEquals(null, map.getCharacter(5, 0, false));
    }

    /**
     * Switch keymap and test that getCharacter values are different
     */
    @Test void testKeymapValue() {
        map.setKeymap(1);
        assertEquals('1', map.getCharacter(0, 0, true));
        assertEquals('1', map.getCharacter(0, 0, false));

        assertEquals('6', map.getCharacter(1, 0, true));
        assertEquals('6', map.getCharacter(1, 0, false));

        assertEquals(';', map.getCharacter(4, 1, true));
        assertEquals(';', map.getCharacter(4, 1, false));

        map.setKeymap(0);
        assertEquals('A', map.getCharacter(0, 0, true));
    }
    
}
