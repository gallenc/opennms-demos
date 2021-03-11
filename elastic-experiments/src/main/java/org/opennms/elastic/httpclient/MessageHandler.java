package org.opennms.elastic.httpclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

public class MessageHandler {
    static final Logger log = LoggerFactory.getLogger(MessageHandler.class);
    
    public void handleIncomingMessage(JsonNode message) {
        log.debug("MessageHandler handling reply message: " + message);
    }

}
