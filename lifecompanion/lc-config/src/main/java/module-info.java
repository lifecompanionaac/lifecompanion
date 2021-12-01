open module org.lifecompanion.config {
    exports org.lifecompanion.config.view.common;
    exports org.lifecompanion.config.view.pane.profilconfig;
    exports org.lifecompanion.config.view.scene;
    exports org.lifecompanion.config.data.mode;
    exports org.lifecompanion.config.data.config;
    exports org.lifecompanion.config.data.component.profile;
    exports org.lifecompanion.config.data.control;
    exports org.lifecompanion.config.view.pane.dev;
    exports org.lifecompanion.config.data.config.tips;
    exports org.lifecompanion.config.data.action.impl;
    exports org.lifecompanion.config.view.pane.main.notification2;

    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.swing;
    requires javafx.media;

    requires java.naming;
    requires org.slf4j;
    requires java.desktop;

    exports org.lifecompanion.config.view.component.simple;
    exports org.lifecompanion.config.view.pane.general;
    exports org.lifecompanion.config.data.component.general;
    exports org.lifecompanion.config.view.pane.compselector;

    requires org.lifecompanion.framework.commons;
    requires org.lifecompanion.framework.commons.fx;
    requires org.lifecompanion.api;
    requires org.lifecompanion.framework.client;

    requires jdom;
    requires easybind;
    requires okhttp3;
    requires gson;
    requires org.controlsfx.controls;
    requires io.github.classgraph;
    requires org.predict4all.core;

    requires webcam.capture;
    requires mslinks;
    requires org.apache.pdfbox;
}