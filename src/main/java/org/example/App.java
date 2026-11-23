package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import org.example.views.Login;
import org.example.views.MenuLateral;
import org.example.views.VisaoGeral;
import org.example.views.Categoria;
import org.example.views.Produto;
import org.example.views.Usuario;

public class App extends Application {

    private BorderPane root;
    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        Login login = new Login();
        Scene cenaLogin = new Scene(login.criarTela(stage), 400, 400);

        stage.setTitle("StockMaster Fx - Login");
        stage.setScene(cenaLogin);
        stage.centerOnScreen();
        stage.show();
    }

    public void iniciarPainel(Stage stage) {
        this.stage = stage;
        root = new BorderPane();
        MenuLateral menu = new MenuLateral();
        root.setLeft(menu.criarMenuLateral(stage, this::navegar));

        navegar("visao");
        Scene scene = new Scene(root, 1200, 700);
        stage.setTitle("StockMaster Fx - Painel Administrativo");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    private void navegar(String pagina) {
        switch (pagina) {
            case "visao":
                root.setCenter(new VisaoGeral().getView());
                break;
            case "categorias":
                root.setCenter(new Categoria().getView(stage));
                break;
            case "produtos":
                root.setCenter(new Produto(stage).getView());
                break;
            case "usuarios":
                root.setCenter(new Usuario().getView(stage));
                break;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
