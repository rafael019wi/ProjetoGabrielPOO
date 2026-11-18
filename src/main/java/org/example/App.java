package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.views.Login;

/**
 * JavaFX App
 */


public class App extends Application {

    @Override
    public void start(Stage stage) {

        Login login = new Login();

        Scene cena = new Scene(login.criarTela(stage), 500, 300);

        stage.setTitle("StockMaster Fx");
        stage.setScene(cena);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
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