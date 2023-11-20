package com.jlh.keytar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlh.keytar.common.IPCMessage;

class TestIPCMessage {
    
    private static String[] rawMessages = new String[4];
    private static IPCMessage[] correct = new IPCMessage[4];
    private ObjectMapper map = new ObjectMapper();


    @BeforeAll static void init() {
        rawMessages[0] = "{\"type\" : \"event\", \"name\" : \"startup\"}";
        rawMessages[1] = "{\"type\" : \"event\", \"name\" : \"char_sent\"}";
        rawMessages[2] = "{\"type\" : \"data\", \"name\" : \"caps\", \"data\" : \"true\"}";
        rawMessages[3] = "{\"type\" : \"data\", \"name\" : \"row_sel\", \"data\" : \"1\"}";

        correct[0] = new IPCMessage("event", "startup");
        correct[1] = new IPCMessage("event", "char_sent");
        correct[2] = new IPCMessage("data", "caps", "true");
        correct[3] = new IPCMessage("data", "row_sel", "1");
    }

    /**
     * Test that the raw JSON strings are correctly mapped to the IPCMessage class using the object mapper;
     * @throws JsonProcessingException
     * @throws JsonMappingException
     */
    @Test void testObjectMapper() throws JsonMappingException, JsonProcessingException {
        for (int i = 0; i < rawMessages.length; i++) {
            assertEquals(correct[i], map.readValue(rawMessages[i], IPCMessage.class));
        }
    }
    
}
