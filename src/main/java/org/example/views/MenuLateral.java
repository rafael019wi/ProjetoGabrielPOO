
package org.example.views;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class MenuLateral {

    public VBox criarMenuLateral(Stage stage, Consumer<String> navegar) {
        VBox menuLateral = new VBox(20);
        menuLateral.setPadding(new Insets(30, 20, 30, 20));
        menuLateral.setStyle("-fx-background-color: #E0E0E0;");
        menuLateral.setPrefWidth(230);

        Label lbBoasVindas = new Label("Bem-vindo à página administrativa");
        lbBoasVindas.setStyle(
                "-fx-font-weight: bold;" +
                        " -fx-wrap-text: true;" +
                        "-fx-font-size: 15px;" +
                        "-fx-text-alignment: center; "
        );

        Hyperlink hlVisaoGeral = new Hyperlink("Visão Geral");
        Hyperlink hlCategorias = new Hyperlink("Gestão de categorias");
        Hyperlink hlProdutos = new Hyperlink("Gestão de produtos");
        Hyperlink hlUsuarios = new Hyperlink("Gestão de usuários");

        hlVisaoGeral.setOnAction(e -> navegar.accept("visao"));
        hlCategorias.setOnAction(e -> navegar.accept("categorias"));
        hlProdutos.setOnAction(e -> navegar.accept("produtos"));
        hlUsuarios.setOnAction(e -> navegar.accept("usuarios"));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnSair = new Button("Sair da Conta");
        btnSair.setOnAction(event -> stage.close());

        menuLateral.getChildren().addAll(
                lbBoasVindas, hlVisaoGeral, hlCategorias, hlProdutos,
                hlUsuarios, spacer, btnSair
        );
        return menuLateral;
    }
}
