open module org.lifecompanion.app {
    requires javafx.controls;
    requires javafx.graphics;
    requires java.naming;
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;
    requires org.slf4j;
    requires org.lifecompanion.framework.client;
    requires org.lifecompanion.framework.commons;
    requires org.lifecompanion.framework.commons.fx;

    requires org.lifecompanion.api;
    requires org.lifecompanion.config;
    requires java.rmi;
    requires java.desktop;

    exports org.lifecompanion.app.launcher;
    exports org.lifecompanion.app.instance;
}