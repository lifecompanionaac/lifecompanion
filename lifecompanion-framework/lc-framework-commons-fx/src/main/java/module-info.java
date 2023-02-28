module org.lifecompanion.framework.commons.fx  {
	requires org.lifecompanion.framework.commons;
    requires javafx.controls;
	requires javafx.graphics;
	requires jdom;
    requires org.slf4j;
	requires java.rmi;

	exports org.lifecompanion.framework.commons.fx.control;
	exports org.lifecompanion.framework.commons.fx.translation;
	exports org.lifecompanion.framework.commons.fx.io;
	exports org.lifecompanion.framework.commons.fx.doublelaunch;
}