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
import org.example.DAO.UserDAO;
import org.example.entity.User;
import java.util.List;
import org.example.dto.Result;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Usuario extends Application {

    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        // ÁREA CENTRAL
        VBox conteudoCentral = new VBox(20);
        conteudoCentral.setPadding(new Insets(40));
        conteudoCentral.setAlignment(Pos.TOP_LEFT);

        MenuLateral construtorMenu = new MenuLateral();
        VBox menuInstanciado = construtorMenu.criarMenuLateral(root, stage);

        root.setLeft(menuInstanciado);

        //Botao de usuario
        HBox cabecalho = new HBox(20);
        cabecalho.setAlignment(Pos.CENTER_LEFT);

        Label lbTitulo = new Label("Gestão de usuários");
        lbTitulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Region espacador = new Region();
        HBox.setHgrow(espacador, Priority.ALWAYS); // Empurra o botão para o canto direito

        Button btnNovoUsuario = new Button("+ Novo Usuário");
        btnNovoUsuario.setStyle(
                "-fx-background-color: #2F1CD8;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 8 16 8 16;" +
                        "-fx-cursor: hand;"
        );


        // CONFIGURAÇÃO TABELA
        TableView<User> tabela = new TableView<>();

        btnNovoUsuario.setOnAction(e -> abrirJanelaCadastro(stage, tabela)); // ⬅️ PONTO 1 (Continuacao)
        cabecalho.getChildren().addAll(lbTitulo, espacador, btnNovoUsuario);

        TableColumn<User, Integer> colId = new TableColumn<>("Id");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<User, String> colNome = new TableColumn<>("Nome completo");
        colNome.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<User, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<User, String> colCriado = new TableColumn<>("Criado em");
        colCriado.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        TableColumn<User, String> colEmail = new TableColumn<>("E-mail");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<User, String> Telefone = new TableColumn<>("Telefone");
        Telefone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        tabela.getColumns().addAll(colId, colNome, colEmail, colStatus);

        // Dispara a carga do banco de dados na inicialização
        atualizarTabelaDeUsuarios(tabela);

        // NOVA COLUNA AÇÕES
        TableColumn<User, Void> colAcoes = new TableColumn<>("Ações");

        Callback<TableColumn<User, Void>, TableCell<User, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<User, Void> call(final TableColumn<User, Void> param) {
                return new TableCell<>() {
                    private final Button btnEditar = new Button("✏️");
                    private final Button btnApagar = new Button("🗑️");
                    private final HBox containerBotoes = new HBox(10, btnEditar, btnApagar);

                    {
                        btnEditar.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                        btnApagar.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                        containerBotoes.setAlignment(Pos.CENTER);

                        // Ação do botão Editar corrigida para trabalhar com User
                        btnEditar.setOnAction(event -> {
                            User usuarioSelecionado = getTableView().getItems().get(getIndex());
                            abrirJanelaEdicao(stage, usuarioSelecionado, getTableView());
                        });

                        // Ação do botão Apagar com conexão ao Back-End do UserDAO
                        btnApagar.setOnAction(event -> {
                            User usuario = getTableView().getItems().get(getIndex());

                            UserDAO userDAO = new UserDAO();
                            Result resultado = userDAO.deleteUser(usuario.getId());

                            if (resultado.isSuccess()) {
                                getTableView().getItems().remove(usuario);
                                System.out.println("Usuário removido: " + resultado.getMessage());
                            } else {
                                Alert alert = new Alert(Alert.AlertType.ERROR, resultado.getMessage(), ButtonType.OK);
                                alert.showAndWait();
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(containerBotoes);
                        }
                    }
                };
            }
        };

        colAcoes.setCellFactory(cellFactory);

        // Monta a grade com todas as colunas sincronizadas em User
        tabela.getColumns().clear();
        tabela.getColumns().addAll(colId, colNome, colStatus, colCriado, colEmail, Telefone, colAcoes);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        conteudoCentral.getChildren().addAll(cabecalho, tabela);
        root.setCenter(conteudoCentral);

        Scene scene = new Scene(root, 1000, 600);
        stage.setScene(scene);
        stage.setTitle("Painel Administrativo");
        stage.show();
    }

    public void atualizarTabelaDeUsuarios(TableView<User> tabela) {
        UserDAO userDAO = new UserDAO();
        Result resultado = userDAO.getAllUsers();

        if (resultado.isSuccess()) {
            List<User> listaUsuarios = (List<User>) resultado.getData();
            ObservableList<User> dadosGraficos = FXCollections.observableArrayList(listaUsuarios);
            tabela.setItems(dadosGraficos);
        } else {
            System.err.println("Erro ao listar usuários: " + resultado.getMessage());
        }
    }

    // Janela de Edição sincronizada com o objeto User do banco de dados
    private void abrirJanelaEdicao(Stage dono, User usuario, TableView<User> tabela) {
        Stage janelaEditar = new Stage();
        janelaEditar.setTitle("Editar Usuário");
        janelaEditar.initModality(Modality.WINDOW_MODAL);
        janelaEditar.initOwner(dono);

        VBox layoutForm = new VBox(10);
        layoutForm.setPadding(new Insets(20));
        layoutForm.setStyle("-fx-background-color: #F4F4F4;");

        // 🔒 CAMPOS BLOQUEADOS
        TextField txtId = new TextField(String.valueOf(usuario.getId()));
        txtId.setEditable(false);
        txtId.setStyle("-fx-background-color: #E0E0E0;");

        TextField txtCriadoEm = new TextField(usuario.getCreatedAt() != null ? usuario.getCreatedAt().toString() : "");
        txtCriadoEm.setEditable(false);
        txtCriadoEm.setStyle("-fx-background-color: #E0E0E0;");

        // 🔓 CAMPOS LIBERADOS PARA EDIÇÃO
        TextField txtNome = new TextField(usuario.getName());
        TextField txtEmail = new TextField(usuario.getEmail());
        TextField txtTelefone = new TextField(usuario.getPhone());

        ComboBox<String> cbStatus = new ComboBox<>();
        cbStatus.getItems().addAll("active", "blocked");
        cbStatus.setMaxWidth(Double.MAX_VALUE);
        cbStatus.setValue(usuario.getStatus());

        Button btnSalvar = new Button("Salvar Alterações");
        Button btnCancelar = new Button("Cancelar");

        HBox containerBotoes = new HBox(10, btnSalvar, btnCancelar);
        containerBotoes.setAlignment(Pos.CENTER_RIGHT);

        // Integração do clique do Salvar com o método updateProfile do banco de dados
        btnSalvar.setOnAction(e -> {
            String nomeInput = txtNome.getText().trim();
            String emailInput = txtEmail.getText().trim();
            String telefoneInput = txtTelefone.getText().trim();
            String statusInput = cbStatus.getValue();

            UserDAO userDAO = new UserDAO();
            Result resultadoUpdate = userDAO.updateProfile(nomeInput, emailInput, telefoneInput, statusInput, usuario.getId());

            if (resultadoUpdate.isSuccess()) {
                // Sincroniza o objeto local com as modificações confirmadas pelo banco
                usuario.setName(nomeInput);
                usuario.setEmail(emailInput);
                usuario.setPhone(telefoneInput);
                usuario.setStatus(statusInput);

                tabela.refresh();
                janelaEditar.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, resultadoUpdate.getMessage(), ButtonType.OK);
                alert.showAndWait();
            }
        });

        btnCancelar.setOnAction(e -> janelaEditar.close());

        layoutForm.getChildren().addAll(
                new Label("ID (Bloqueado):"), txtId,
                new Label("Criado Em (Bloqueado):"), txtCriadoEm,
                new Label("Nome Completo:"), txtNome,
                new Label("Status:"), cbStatus,
                new Label("E-mail:"), txtEmail,
                new Label("Telefone:"), txtTelefone,
                containerBotoes
        );

        Scene cenaEdicao = new Scene(layoutForm, 400, 500);
        janelaEditar.setScene(cenaEdicao);
        janelaEditar.setResizable(false);
        janelaEditar.showAndWait();
    }
    private void abrirJanelaCadastro(Stage dono, TableView<User> tabela) {
        Stage janelaCadastro = new Stage();
        janelaCadastro.setTitle("Cadastrar Novo Usuário");
        janelaCadastro.initModality(Modality.WINDOW_MODAL);
        janelaCadastro.initOwner(dono);

        VBox layoutForm = new VBox(10);
        layoutForm.setPadding(new Insets(20));
        layoutForm.setStyle("-fx-background-color: #F4F4F4;");

        TextField txtNome = new TextField();
        txtNome.setPromptText("Digite o nome completo");

        TextField txtEmail = new TextField();
        txtEmail.setPromptText("Digite o e-mail");

        TextField txtTelefone = new TextField();
        txtTelefone.setPromptText("Digite o telefone");

        PasswordField txtSenha = new PasswordField();
        txtSenha.setPromptText("Crie uma senha");

        PasswordField txtConfirmarSenha = new PasswordField();
        txtConfirmarSenha.setPromptText("Confirme a senha");

        ComboBox<String> cbStatus = new ComboBox<>();
        cbStatus.getItems().addAll("active", "blocked");
        cbStatus.setValue("active");
        cbStatus.setMaxWidth(Double.MAX_VALUE);

        Button btnSalvar = new Button("Cadastrar");
        btnSalvar.setStyle("-fx-background-color: #2F1CD8; -fx-text-fill: white;");

        Button btnCancelar = new Button("Cancelar");

        HBox containerBotoes = new HBox(10, btnSalvar, btnCancelar);
        containerBotoes.setAlignment(Pos.CENTER_RIGHT);

        btnSalvar.setOnAction(e -> {
            String nome = txtNome.getText().trim();
            String email = txtEmail.getText().trim();
            String telefone = txtTelefone.getText().trim();
            String senha = txtSenha.getText();
            String confirmarSenha = txtConfirmarSenha.getText();
            String status = cbStatus.getValue();

            UserDAO userDAO = new UserDAO();
            Result<User> resultado = userDAO.createUser(nome, email, telefone, senha, confirmarSenha, status);

            if (resultado.isSuccess()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, resultado.getMessage(), ButtonType.OK);
                alert.showAndWait();

                atualizarTabelaDeUsuarios(tabela);
                janelaCadastro.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, resultado.getMessage(), ButtonType.OK);
                alert.showAndWait();
            }
        });

        btnCancelar.setOnAction(e -> janelaCadastro.close());

        layoutForm.getChildren().addAll(
                new Label("Nome Completo:"), txtNome,
                new Label("E-mail:"), txtEmail,
                new Label("Telefone:"), txtTelefone,
                new Label("Senha:"), txtSenha,
                new Label("Confirmar Senha:"), txtConfirmarSenha,
                new Label("Status:"), cbStatus,
                containerBotoes
        );

        Scene cenaCadastro = new Scene(layoutForm, 400, 520);
        janelaCadastro.setScene(cenaCadastro);
        janelaCadastro.setResizable(false);
        janelaCadastro.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
