module org.example {
    requires javafx.controls;
    requires java.sql;
    requires io.github.cdimascio.dotenv.java;

    exports org.example;
    opens org.example to javafx.fxml, javafx.graphics;

    exports org.example.views;
    opens org.example.views to javafx.fxml, javafx.graphics;
}