package com.thirdeye3.usermanager.configs;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;

public class HttpConnectorConfig {

    @Value("${server.http.port:8080}")
    private static int httpPort;

    public static Connector createHttpConnector() {
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setScheme("http");
        connector.setPort(httpPort);
        return connector;
    }
}
