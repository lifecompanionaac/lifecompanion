open module org.lifecompanion.framework.commons {
    requires persistence.api;
    requires org.slf4j;
    requires jdom;
    requires java.xml;
    requires org.apache.commons.io;
    requires org.apache.commons.codec;
    requires java.rmi;

    exports org.lifecompanion.framework.utils;
    exports org.lifecompanion.framework.commons;
    exports org.lifecompanion.framework.commons.doublelaunch;
    exports org.lifecompanion.framework.commons.translation;
    exports org.lifecompanion.framework.commons.ui;
    exports org.lifecompanion.framework.commons.utils.app;
    exports org.lifecompanion.framework.commons.utils.io;
    exports org.lifecompanion.framework.commons.utils.lang;
    exports org.lifecompanion.framework.commons.utils.system;
    exports org.lifecompanion.framework.commons.configuration;
    exports org.lifecompanion.framework.model.client;
    exports org.lifecompanion.framework.model.server;
    exports org.lifecompanion.framework.model.server.service;
    exports org.lifecompanion.framework.model.server.dto;
    exports org.lifecompanion.framework.model.server.error;
    exports org.lifecompanion.framework.model.server.update;
    exports org.lifecompanion.framework.model.server.tech;
    exports org.lifecompanion.framework.model.server.user;
    exports org.lifecompanion.framework.model.server.stats;
}