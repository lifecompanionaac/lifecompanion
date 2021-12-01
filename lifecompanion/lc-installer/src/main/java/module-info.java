open module org.lifecompanion.installer {
    requires javafx.controls;
    requires javafx.graphics;
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires java.desktop;
    requires oshi.core;
    requires mslinks;
	requires java.naming;

	requires org.lifecompanion.framework.client;
    requires org.lifecompanion.framework.commons;
	requires org.lifecompanion.framework.commons.fx;

    exports org.lifecompanion.installer;
}