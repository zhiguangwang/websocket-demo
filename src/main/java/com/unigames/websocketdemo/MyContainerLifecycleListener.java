package com.unigames.websocketdemo;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;

/**
 * Use this singleton class to glue Jersey and Tyrus world by hk2 ServiceLocator.
 */
public final class MyContainerLifecycleListener implements ContainerLifecycleListener {

    private static final MyContainerLifecycleListener instance = new MyContainerLifecycleListener();

    private ServiceLocator serviceLocator;

    public static MyContainerLifecycleListener getInstance() {
        return instance;
    }

    private MyContainerLifecycleListener() {

    }

    public ServiceLocator getServiceLocator() {
        return this.serviceLocator;
    }

    private void setServiceLocator(ServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
    }

    private void setServiceLocatorFromContainer(Container container) {
        ServiceLocator serviceLocator = container.getApplicationHandler().getServiceLocator();
        setServiceLocator(serviceLocator);
    }

    @Override
    public void onStartup(Container container) {
        setServiceLocatorFromContainer(container);
    }

    @Override
    public void onReload(Container container) {
        setServiceLocatorFromContainer(container);
    }

    @Override
    public void onShutdown(Container container) {
        setServiceLocator(null);
    }
}
