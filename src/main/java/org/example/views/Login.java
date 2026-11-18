package org.example.views;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class Login extends Application {

    @Override
    public void start(Stage stage) {
        VBox vbox = new VBox(15.0);
        vbox.setPadding(new Insets(40, 30, 40, 30));
        vbox.setAlignment(Pos.TOP_LEFT);
        vbox.setStyle("-fx-background-color: #D3D3D3;");

        Label labelNome = new Label("StockMaster Fx");
        labelNome.setMaxWidth(Double.MAX_VALUE); // Faz o fundo azul ocupar toda a largura disponível
        labelNome.setPadding(new Insets(8, 12, 8, 12));
        labelNome.setStyle(

                "-fx-background-color: #0B24FB;" + "-fx-text-fill: white;" + "-fx-font-size: 16px;" + "-fx-font-weight: bold;"
        );

        TextField tfUsuario = new TextField();
        tfUsuario.setPromptText("E-mail"); // Texto de dica que some ao digitar
        tfUsuario.setPrefHeight(40);  // Deixa o campo mais alto como na foto
        tfUsuario.setStyle(
                "-fx-background-color: #D3D3D3;" +
                        "-fx-border-color: #4A4A4A;" +
                        "-fx-border-width: 1px;" +
                        "-fx-prompt-text-fill: #202020;"
        );
        PasswordField tfSenha = new PasswordField();
        tfSenha.setPromptText("Senha"); // Texto de dica
        tfSenha.setPrefHeight(40);
        tfSenha.setStyle(
                "-fx-background-color: #D3D3D3;" +
                        "-fx-border-color: #4A4A4A;" +
                        "-fx-border-width: 1px;" +
                        "-fx-prompt-text-fill: #202020;"
        );

        Button botaoOk = new Button("Entrar");
        botaoOk.setPadding(new Insets(8, 25, 8, 25)); // Botão retangular largo
        botaoOk.setStyle(
                "-fx-background-color: #2F1CD8;" + // Roxo azulado do botão
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-background-radius: 0;"
        );
        Label labelMensagem = new Label();
        labelMensagem.setStyle("-fx-text-fill: red;");

        botaoOk.setOnAction(e -> {
            String email = tfUsuario.getText().trim();
            String senha = tfSenha.getText();

            // 1. Validação visual básica
            if (email.isEmpty() || senha.isEmpty()) {
                labelMensagem.setText("Por favor, preencha todos os campos!");
                return;
            }

            try {
                // 2. Instancia o seu DAO (Back-End)
                org.example.DAO.UserDAO userDAO = new org.example.DAO.UserDAO();

                // 3. Chama o seu método de login (Retorna o container Result<User>)
                org.example.dto.Result resultadoLogin = userDAO.login(email, senha);


                // 4. Verifica se a validação do banco deu certo (campo 'success' do seu Result)
                if (resultadoLogin.isSuccess()) { // Se o seu método do Result for .isSuccess() ou .getSuccess()

                    // Extrai o objeto User de dentro do resultado
                    org.example.entity.User usuarioAutenticado = (org.example.entity.User) resultadoLogin.getData();

                    // Salva o usuário extraído na sessão global
                    org.example.util.UserSession.setLoggedUser(usuarioAutenticado);

                    labelMensagem.setStyle("-fx-text-fill: green;");
                    labelMensagem.setText("Login realizado com sucesso! Redirecionando...");

                    // Abre a tela de Administração de Usuários (Usuario.java)
                    Usuario telaUsuario = new Usuario();
                    try {
                        telaUsuario.start(stage);
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

        vbox.getChildren().addAll(labelNome, tfUsuario, tfSenha, botaoOk, labelMensagem);

        Scene cena = new Scene(vbox, 500, 300);

        stage.setTitle("StockMaster Fx");
        stage.setScene(cena);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}