package org.example.integration.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.ip.tcp.TcpOutboundGateway;
import org.springframework.integration.ip.tcp.connection.AbstractClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.CachingClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.FailoverClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNetClientConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArrayLengthHeaderSerializer;
import org.springframework.messaging.MessageHandler;

import java.util.Collections;

@Configuration
public class ClientConfig {

    @Bean
    @ServiceActivator(inputChannel = "toServerTcp")
    public MessageHandler toServerTcpGate(final AbstractClientConnectionFactory clientConnectionFactory) {
        final TcpOutboundGateway gate = new TcpOutboundGateway();
        gate.setConnectionFactory(clientConnectionFactory);
        gate.setRemoteTimeout(500);
        return gate;
    }

    @Bean
    @Scope(value = "prototype")
    public AbstractClientConnectionFactory clientConnectionFactory() {
//        return getCachingFactory(true); // (1)
//        return getFailoverFactory(false); // (2)
//        return getFailoverFactory(true); // (3)
        return getFailoverCacheFactory(true); // (4)
    }

    @SuppressWarnings("unused")
    private AbstractClientConnectionFactory getCachingFactory(final boolean singleUse) {
        final TcpNetClientConnectionFactory tcpFactory = getTcpFactory(singleUse);
        return getCachingFactory(tcpFactory);
    }

    @SuppressWarnings("unused")
    private AbstractClientConnectionFactory getFailoverFactory(final boolean singleUse) {
        final TcpNetClientConnectionFactory tcpFactory = getTcpFactory(singleUse);
        return getFailoverFactory(tcpFactory, singleUse);
    }

    @SuppressWarnings("unused")
    private AbstractClientConnectionFactory getFailoverCacheFactory(final boolean singleUse) {
        final TcpNetClientConnectionFactory tcpFactory = getTcpFactory(singleUse);
        final CachingClientConnectionFactory cachingFactory = getCachingFactory(tcpFactory);
        return getFailoverFactory(cachingFactory, singleUse);
    }

    private TcpNetClientConnectionFactory getTcpFactory(final boolean singleUse) {
        final TcpNetClientConnectionFactory tcpFactory = new TcpNetClientConnectionFactory("localhost", 12345);

        final ByteArrayLengthHeaderSerializer serializer = new ByteArrayLengthHeaderSerializer();
        tcpFactory.setDeserializer(serializer);
        tcpFactory.setSerializer(serializer);

        tcpFactory.setSoKeepAlive(true);
        tcpFactory.setSingleUse(singleUse);

        return tcpFactory;
    }

    private CachingClientConnectionFactory getCachingFactory(final AbstractClientConnectionFactory factory) {
        final CachingClientConnectionFactory cachingFactory = new CachingClientConnectionFactory(factory, 5);

        factory.setSoKeepAlive(true);

        return cachingFactory;
    }

    private FailoverClientConnectionFactory getFailoverFactory(final AbstractClientConnectionFactory factory,
                                                               final boolean singleUse) {
        final FailoverClientConnectionFactory failoverFactory =
                new FailoverClientConnectionFactory(Collections.singletonList(factory));

        failoverFactory.setSoKeepAlive(true);
        failoverFactory.setSingleUse(singleUse);

        return failoverFactory;
    }
}
