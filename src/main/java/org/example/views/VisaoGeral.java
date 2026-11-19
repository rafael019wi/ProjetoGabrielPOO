package org.example.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VisaoGeral {

    public BorderPane criarPagina(Stage stage) {

        // Layout principal
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");

        // Menu lateral já existente
        MenuLateral menu = new MenuLateral();
        menu.criarMenuLateral(root, stage);

        // Conteúdo central
        VBox conteudo = new VBox(12);
        conteudo.setPadding(new Insets(0));

        // Título
        Label lbTitulo = new Label("Visão Geral");
        lbTitulo.setStyle(
                "-fx-font-size: 24px;" +
                        "-fx-font-weight: normal;"
        );

        // Painel de informações
        StackPane painel = new StackPane();
        painel.setAlignment(Pos.TOP_LEFT);
        painel.setPadding(new Insets(18, 15, 15, 15));
        painel.setPrefHeight(207);
        painel.setMinHeight(207);
        painel.setMaxWidth(Double.MAX_VALUE);

        painel.setStyle(
                "-fx-background-color: #D9D9D9;"
        );

        // Quantidade de produtos
        Label lbProdutos = new Label("Produtos cadastrados:  200");
        lbProdutos.setStyle(
                "-fx-font-size: 12px;"
        );

        painel.getChildren().add(lbProdutos);

        // Adicionando elementos
        conteudo.getChildren().addAll(
                lbTitulo,
                painel
        );

        // Posicionamento do conteúdo
        BorderPane.setMargin(
                conteudo,
                new Insets(44, 35, 0, 40)
        );

        root.setCenter(conteudo);

        return root;
    }
}