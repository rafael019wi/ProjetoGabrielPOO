
package org.example.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.example.DAO.ProductDAO;
import org.example.DAO.UserDAO;
import org.example.DAO.CategoryDAO;

public class VisaoGeral {

    public VBox getView() {
        VBox conteudo = new VBox(20);
        conteudo.setPadding(new Insets(0));
        Label lbTitulo = new Label("Visão Geral");
        lbTitulo.setStyle(
                "-fx-font-size: 24px;" +
                        "-fx-font-weight: normal;"
        );
        lbTitulo.setMaxWidth(Double.MAX_VALUE);
        lbTitulo.setAlignment(Pos.CENTER);

        Label lbProdutos = new Label("...");
        Label lbUsuarios = new Label("...");
        Label lbCategorias = new Label("...");

        VBox painelProdutos = criarPainel("Produtos cadastrados", lbProdutos);
        VBox painelUsuarios = criarPainel("Usuários cadastrados", lbUsuarios);
        VBox painelCategorias = criarPainel("Categorias cadastradas", lbCategorias);

        HBox paineis = new HBox(18);
        paineis.setAlignment(Pos.CENTER);
        paineis.setFillHeight(true);

        paineis.getChildren().addAll(painelProdutos, painelUsuarios, painelCategorias);
        carregarQuantidades(lbProdutos, lbUsuarios, lbCategorias);
        conteudo.getChildren().addAll(lbTitulo, paineis);
        BorderPane.setMargin(conteudo, new Insets(44, 35, 0, 40));
        return conteudo;
    }

    private VBox criarPainel(String titulo, Label valor) {
        Label lbTitulo = new Label(titulo);
        lbTitulo.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-font-weight: normal;"
        );

        valor.setStyle(
                "-fx-font-size: 30px;" +
                        "-fx-font-weight: bold;"
        );

        VBox painel = new VBox(18);
        painel.setAlignment(Pos.TOP_LEFT);
        painel.setPadding(new Insets(20));
        painel.setPrefHeight(150);
        painel.setMaxWidth(Double.MAX_VALUE);

        painel.setStyle(
                "-fx-background-color: #D9D9D9;" +
                        "-fx-background-radius: 8px;"
        );

        painel.getChildren().addAll(lbTitulo, valor);
        HBox.setHgrow(painel, javafx.scene.layout.Priority.ALWAYS);
        return painel;
    }

    private void carregarQuantidades(Label lbProdutos, Label lbUsuarios, Label lbCategorias) {

        var resultadoProdutos = new ProductDAO().getAllProducts();
        if (resultadoProdutos.isSuccess() && resultadoProdutos.getData() != null) {
            lbProdutos.setText(String.valueOf(resultadoProdutos.getData().size()));
        }
        else {
            lbProdutos.setText("Erro");
        }

        var resultadoUsuarios = new UserDAO().getAllUsers();
        if (resultadoUsuarios.isSuccess() && resultadoUsuarios.getData() != null) {
            lbUsuarios.setText(String.valueOf(resultadoUsuarios.getData().size()));
        }
        else {
            lbUsuarios.setText("Erro");
        }

        var resultadoCategorias = new CategoryDAO().getAllCategories();
        if (resultadoCategorias.isSuccess() && resultadoCategorias.getData() != null) {
            lbCategorias.setText(String.valueOf(resultadoCategorias.getData().size()));
        }
        else {
            lbCategorias.setText("Erro");
        }
    }
}
