package org.example.integration.client;

import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(defaultRequestChannel = "toServerTcp")
public interface ClientGateway {

    byte[] viaTcp(byte[] in);
}
