package com.unigames.websocketdemo;

import com.unigames.commons.inject.hk2.AutoInjectBinder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

import javax.websocket.server.ServerEndpointConfig;

/**
 * Use this configurator in websocket standalone mode.
 * In this case the configurator is responsible for creating ServericeLocator and bind all injections.
 */
public class StandaloneServerEndpointConfigurator extends ServerEndpointConfig.Configurator {

    private static final Logger log = LogManager.getLogger();

    private final ServiceLocator serviceLocator;

    public StandaloneServerEndpointConfigurator() {
        log.debug("StandaloneServerEndpointConfigurator() {}", this.hashCode());
        this.serviceLocator = createServiceLocator();
    }

    private static ServiceLocator createServiceLocator() {
        ServiceLocator serviceLocator = ServiceLocatorUtilities.createAndPopulateServiceLocator();
        ServiceLocatorUtilities.bind(serviceLocator, new AutoInjectBinder(App.class.getPackage().getName()));
        return serviceLocator;
    }

    @Override
    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        T instance = serviceLocator.createAndInitialize(endpointClass);
        log.debug("getEndpointInstance: {}@{}", instance.getClass().getName(), instance.hashCode());
        return instance;
    }
}
