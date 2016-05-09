package org.example.integration.server;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.ip.tcp.TcpInboundGateway;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArrayLengthHeaderSerializer;
import org.springframework.messaging.MessageChannel;

@Configuration
public class ServerConfig {

    @Bean
    public TcpNetServerConnectionFactory serverConnectionFactory() {
        final TcpNetServerConnectionFactory factory = new TcpNetServerConnectionFactory(12345);
        final ByteArrayLengthHeaderSerializer serializer = new ByteArrayLengthHeaderSerializer();
        factory.setDeserializer(serializer);
        factory.setSerializer(serializer);
        return factory;
    }

    @Bean
    public TcpInboundGateway fromClientTcpGate(final TcpNetServerConnectionFactory connectionFactory,
                                               @Qualifier("requestChannel") final MessageChannel requestChannel) {
        final TcpInboundGateway gate = new TcpInboundGateway();
        gate.setConnectionFactory(connectionFactory);
        gate.setRequestChannel(requestChannel);
        return gate;
    }
}
