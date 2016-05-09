package org.example;

import org.example.integration.client.ClientGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.MessageTimeoutException;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;

@SpringBootApplication
@EnableIntegration
@IntegrationComponentScan
public class FailoverCachingApplication {

    private static final Logger LOG = LoggerFactory.getLogger(FailoverCachingApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(FailoverCachingApplication.class, args).close();
    }

    @Bean
    public CommandLineRunner test(final ClientGateway clientGateway) {
        return (args) -> {
            sendAndReceive(clientGateway, "1st");
            sendAndReceive(clientGateway, "2nd");
        };
    }

    private void sendAndReceive(final ClientGateway clientGateway, final String request) {
        LOG.info("Outgoing request: " + request);
        try {
            final byte[] response = clientGateway.viaTcp(request.getBytes());
            LOG.info("Incoming response: " + new String(response));
        } catch(MessageTimeoutException e) {
            LOG.error("Client timeout");
        }
    }
}
