module moviedb.core {
    requires transitive com.fasterxml.jackson.databind;
    requires java.net.http;

    exports moviedb.core;
    exports moviedb.json;
}