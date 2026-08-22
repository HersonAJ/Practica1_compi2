module com.example.piglatin {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.antlr.antlr4.runtime;
    requires java.xml;
    requires java.desktop;
    requires guru.nidi.graphviz;

    opens com.example.piglatin to javafx.graphics;
    exports com.example.piglatin.ui;
    exports com.example.piglatin.ui.components;
}