package com.fishsoup.fishchat.registry;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Map;

@Component
public class ChatNacosRegistration implements Registration {

    @Value("${websocket.port}")
    private int port;

    private final NacosDiscoveryProperties nacosDiscoveryProperties;

    ChatNacosRegistration(NacosDiscoveryProperties nacosDiscoveryProperties) {
        this.nacosDiscoveryProperties = nacosDiscoveryProperties;
    }

    @Override
    public String getServiceId() {
        return "fish-chat-service";
    }

    @Override
    public String getHost() {
        return nacosDiscoveryProperties.getIp();
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public boolean isSecure() {
        return nacosDiscoveryProperties.isSecure();
    }

    @Override
    public URI getUri() {
        return DefaultServiceInstance.getUri(this);
    }

    @Override
    public Map<String, String> getMetadata() {
        return nacosDiscoveryProperties.getMetadata();
    }
}
