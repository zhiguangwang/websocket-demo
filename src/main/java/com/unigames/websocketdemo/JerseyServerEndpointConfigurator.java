package com.unigames.websocketdemo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.hk2.api.ServiceLocator;

import javax.websocket.server.ServerEndpointConfig;

/**
 * Use this configurator when combined with Jersey
 * In this case the configurator gets hk2 ServiceLocator created by Jersey
 */
public class JerseyServerEndpointConfigurator extends ServerEndpointConfig.Configurator {

    private static final Logger log = LogManager.getLogger();

    public JerseyServerEndpointConfigurator() {
        log.debug("JerseyServerEndpointConfigurator() {}", this.hashCode());
    }

    @Override
    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        ServiceLocator serviceLocator = MyContainerLifecycleListener.getInstance().getServiceLocator();
        return serviceLocator.createAndInitialize(endpointClass);
    }
}
