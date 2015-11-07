package com.unigames.websocketdemo;

import com.unigames.commons.inject.hk2.AutoInjectBinder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

import javax.websocket.server.ServerEndpointConfig;

/**
 * Created by wangzhiguang on 11/3/15.
 */
public class HK2ServerEndpointConfigurator extends ServerEndpointConfig.Configurator {

    private static final Logger LOG = LogManager.getLogger();

    private final ServiceLocator serviceLocator;

    public HK2ServerEndpointConfigurator() {
        LOG.debug("HK2ServerEndpointConfigurator() {}", this.hashCode());

        ServiceLocator serviceLocator = ServiceLocatorUtilities.createAndPopulateServiceLocator("WebSocket");
        ServiceLocatorUtilities.bind(serviceLocator, new AutoInjectBinder(App.class.getPackage().getName()));

        this.serviceLocator = serviceLocator;
    }

    @Override
    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        T instance = serviceLocator.createAndInitialize(endpointClass);
        LOG.debug("getEndpointInstance: {}@{}", instance.getClass().getName(), instance.hashCode());
        return instance;
    }

}
