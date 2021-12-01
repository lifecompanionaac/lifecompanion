module org.lifecompanion.framework.client {
    requires org.lifecompanion.framework.commons;
    requires okhttp3;
    requires gson;
    requires org.slf4j;
    requires java.sql;

    exports org.lifecompanion.framework.client.props;
    exports org.lifecompanion.framework.client.http;
    exports org.lifecompanion.framework.client.service;
}