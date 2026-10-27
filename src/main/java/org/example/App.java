package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        var javaVersion = SystemInfo.javaVersion();
        var javafxVersion = SystemInfo.javafxVersion();

        var label = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        var scene = new Scene(new StackPane(label), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}


/*
//Exemplo de conexão com o banco de dados

package org.example;

import org.example.entity.User;
import org.example.DAO.UserDAO;

public class App {
    public static void main(String[] args) {
        User user = new User();
        user.setName("Guilherme Negrijo");
        user.setEmail("gui@gmail.com");
        user.setPhone("16999999999");
        user.setPassword("teste123");
        user.setStatus("active");

        new UserDAO().createUser(user);
        System.out.println(user.getName());
    }
}
*/