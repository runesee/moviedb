module moviedb.rest {
    requires spring.context;
    requires spring.web;
    requires spring.boot.autoconfigure;
    requires spring.boot;
    requires spring.core;
    requires spring.beans;
    requires transitive moviedb.core;

    exports moviedb.rest;

    uses org.springframework.cglib.core.ReflectUtils;
    uses org.springframework.beans.BeansException;

    opens moviedb.rest;

}