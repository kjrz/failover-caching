package org.example.integration.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

@MessageEndpoint
public final class ServerEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(ServerEndpoint.class);

    @ServiceActivator(inputChannel = "requestChannel")
    public byte[] processRequest(final byte[] in) {
        LOG.info("Incoming request: " + new String(in));
        return in;
    }
}
