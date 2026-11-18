package org.example.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.example.DAO.UserDAO;
import org.example.dto.Result;
import org.example.entity.User;
import org.example.util.UserSession;

public class Login {

    public VBox criarTela(Stage stage) {

        VBox vbox = new VBox(15.0);
        vbox.setPadding(new Insets(40, 30, 40, 30));
        vbox.setAlignment(Pos.TOP_LEFT);
        vbox.setStyle("-fx-background-color: #D3D3D3;");

        Label labelNome = new Label("StockMaster Fx");
        labelNome.setMaxWidth(Double.MAX_VALUE);
        labelNome.setPadding(new Insets(8, 12, 8, 12));
        labelNome.setStyle(
                "-fx-background-color: #0B24FB;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;"
        );

        TextField tfUsuario = new TextField();
        tfUsuario.setPromptText("E-mail");
        tfUsuario.setPrefHeight(40);
        tfUsuario.setStyle(
                "-fx-background-color: #D3D3D3;" +
                        "-fx-border-color: #4A4A4A;" +
                        "-fx-border-width: 1px;" +
                        "-fx-prompt-text-fill: #202020;"
        );

        PasswordField tfSenha = new PasswordField();
        tfSenha.setPromptText("Senha");
        tfSenha.setPrefHeight(40);
        tfSenha.setStyle(
                "-fx-background-color: #D3D3D3;" +
                        "-fx-border-color: #4A4A4A;" +
                        "-fx-border-width: 1px;" +
                        "-fx-prompt-text-fill: #202020;"
        );

        Button botaoOk = new Button("Entrar");
        botaoOk.setPadding(new Insets(8, 25, 8, 25));
        botaoOk.setStyle(
                "-fx-background-color: #2F1CD8;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-background-radius: 0;"
        );

        Label labelMensagem = new Label();
        labelMensagem.setStyle("-fx-text-fill: red;");

        botaoOk.setOnAction(e -> {

            String email = tfUsuario.getText().trim();
            String senha = tfSenha.getText();

            if (email.isEmpty() || senha.isEmpty()) {
                labelMensagem.setStyle("-fx-text-fill: red;");
                labelMensagem.setText("Por favor, preencha todos os campos!");
                return;
            }

            try {
                UserDAO userDAO = new UserDAO();
                Result resultadoLogin = userDAO.login(email, senha);

                if (resultadoLogin.isSuccess()) {

                    User usuarioAutenticado = (User) resultadoLogin.getData();
                    UserSession.setLoggedUser(usuarioAutenticado);

                    labelMensagem.setStyle("-fx-text-fill: green;");
                    labelMensagem.setText("Login realizado com sucesso!");

                    try {
                        // 1. Cria a estrutura principal do painel administrativo
                        BorderPane root = new BorderPane();

                        // 2. Instancia e encaixa o Menu Lateral na esquerda
                        MenuLateral construtorMenu = new MenuLateral();
                        root.setLeft(construtorMenu.criarMenuLateral(root, stage));

                        // 3. Instancia a página de Usuários e encaixa o VBox dela no centro
                        Usuario telaUsuario = new Usuario();
                        root.setCenter(telaUsuario.getView(stage));

                        // 4. Troca a cena da janela atual para o painel administrativo
                        Scene cenaPainel = new Scene(root, 1000, 600);
                        stage.setTitle("StockMaster Fx - Painel Administrativo");
                        stage.setScene(cenaPainel);
                        stage.centerOnScreen();

                    } catch (Exception ex) {
                        labelMensagem.setStyle("-fx-text-fill: red;");
                        labelMensagem.setText("Erro ao carregar a página de administração.");
                        ex.printStackTrace();
                    }

                } else {
                    labelMensagem.setStyle("-fx-text-fill: red;");
                    labelMensagem.setText(resultadoLogin.getMessage());
                }

            } catch (Exception ex) {
                labelMensagem.setStyle("-fx-text-fill: red;");
                labelMensagem.setText("Erro crítico de conexão com o banco de dados.");
                ex.printStackTrace();
            }
        });

        vbox.getChildren().addAll(
                labelNome,
                tfUsuario,
                tfSenha,
                botaoOk,
                labelMensagem
        );

        return vbox;
    }
}
