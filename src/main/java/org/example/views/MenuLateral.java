package org.example.views;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.util.Callback;

public class MenuLateral {

    public VBox criarMenuLateral(BorderPane root, Stage stage) {
        VBox menuLateral = new VBox(20);
        menuLateral.setPadding(new Insets(30, 20, 30, 20));
        menuLateral.setStyle("-fx-background-color: #E0E0E0;");

        Label lbBoasVindas = new Label("Bem-vindo à página administrativa");
        lbBoasVindas.setStyle("-fx-font-weight: bold; -fx-wrap-text: true;");

        Hyperlink hlVisaoGeral = new Hyperlink("Visão Geral");
        Hyperlink hlCategorias = new Hyperlink("Gestão de categorias");
        Hyperlink hlProdutos = new Hyperlink("Gestão de produtos");

        Hyperlink hlUsuarios = new Hyperlink("Gestão de usuários");
        hlUsuarios.setStyle("-fx-font-weight: bold; -fx-underline: true;");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        Button btnSair = new Button("Sair da Conta");
        btnSair.setOnAction(event -> stage.close());

        menuLateral.getChildren().addAll(lbBoasVindas, hlVisaoGeral, hlCategorias, hlProdutos, hlUsuarios, spacer, btnSair);

        if (root != null) {
            root.setLeft(menuLateral);
        }
        return menuLateral;
    }
}
