module org.lifecompanion.framework.server {
    requires org.lifecompanion.framework.commons;

    requires org.slf4j;
    requires java.sql;
    requires javax.servlet.api;
    requires org.postgresql.jdbc;
    requires commons.fileupload;
    requires spark.core;
    requires jbcrypt;
    requires java.jwt;
    requires sql2o;
    requires gson;
    requires persistence.api;
    requires org.mybatis;
    requires okhttp3;

    requires software.amazon.awssdk.services.s3;
    requires software.amazon.awssdk.auth;
    requires software.amazon.awssdk.core;
    requires software.amazon.awssdk.regions;

    exports org.lifecompanion.framework.server;
}