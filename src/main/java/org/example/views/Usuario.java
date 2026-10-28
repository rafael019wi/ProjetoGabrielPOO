package org.example.views;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Usuario extends Application {

    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        // MENU LATERAL
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
        root.setLeft(menuLateral);

        // ÁREA CENTRAL
        VBox conteudoCentral = new VBox(20);
        conteudoCentral.setPadding(new Insets(40));
        conteudoCentral.setAlignment(Pos.TOP_LEFT);

        Label lbTitulo = new Label("Gestão de usuários");
        lbTitulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        //CONFIGURAÇÃO DA TABELA
        TableView<UsuarioData> tabela = new TableView<>();

        TableColumn<UsuarioData, String> colId = new TableColumn<>("Id");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<UsuarioData, String> colNome = new TableColumn<>("Nome completo");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<UsuarioData, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<UsuarioData, String> colCriado = new TableColumn<>("Criado em");
        colCriado.setCellValueFactory(new PropertyValueFactory<>("criadoEm"));

        TableColumn<UsuarioData, String> colEmail = new TableColumn<>("E-mail");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<UsuarioData, String> Telefone = new TableColumn<>("Telefone");
        Telefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));

        TableColumn<UsuarioData, String> CriadoPor = new TableColumn<>("Criado por");
        CriadoPor.setCellValueFactory(new PropertyValueFactory<>("criadoPor"));

        // NOVA COLUNA AÇÕES
        TableColumn<UsuarioData, Void> colAcoes = new TableColumn<>("Ações");

        Callback<TableColumn<UsuarioData, Void>, TableCell<UsuarioData, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<UsuarioData, Void> call(final TableColumn<UsuarioData, Void> param) {
                return new TableCell<>() {
                    // Criamos os botões que ficarão dentro da célula
                    private final Button btnEditar = new Button("✏️");
                    private final Button btnApagar = new Button("🗑️");
                    private final HBox containerBotoes = new HBox(10, btnEditar, btnApagar);

                    {
                        // Estilização rápida para remover o fundo padrão dos botões e deixá-los limpos
                        btnEditar.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                        btnApagar.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                        containerBotoes.setAlignment(Pos.CENTER);

                        // Ação do botão Editar
                        btnEditar.setOnAction(event -> {
                            UsuarioData usuario = getTableView().getItems().get(getIndex());
                            System.out.println("Editar o usuário: " + usuario.getNome());
                            // Aqui você chama a sua lógica ou tela de edição passando o objeto 'usuario'
                        });

                        // Ação do botão Apagar
                        btnApagar.setOnAction(event -> {
                            UsuarioData usuario = getTableView().getItems().get(getIndex());
                            System.out.println("Apagar o usuário: " + usuario.getNome());
                            // Exemplo de remoção direta da lista da tabela
                            getTableView().getItems().remove(usuario);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        // Se a linha estiver vazia, não mostra nada. Se tiver dados, renderiza o HBox com os botões
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(containerBotoes);
                        }
                    }
                };
            }
        };

        // Aplica a fábrica de células criada acima na coluna de ações
        colAcoes.setCellFactory(cellFactory);

        // Adiciona todas as colunas incluindo a nova coluna de ações
        tabela.getColumns().addAll(colId, colNome, colStatus, colCriado, CriadoPor, colEmail, Telefone, colAcoes );
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Dados de teste
        tabela.getItems().add(new UsuarioData("8906578", "Jailson Leve", "Ativo", "01/02/2025", "Dev_Link", "Zedamanga@gmail.com","1999879"));
        tabela.getItems().add(new UsuarioData("8906578", "Pesadao Leve", "Ativo", "01/02/2025", "Dev_Link", "Zedamanga@gmail.com","1999876"));

        conteudoCentral.getChildren().addAll(lbTitulo, tabela);
        root.setCenter(conteudoCentral);

        Scene scene = new Scene(root, 1000, 600);
        stage.setScene(scene);
        stage.setTitle("Painel Administrativo");
        stage.show();
    }

    // Classe de modelo interna
    public static class UsuarioData {
        private final String id;
        private final String nome;
        private final String status;
        private final String criadoEm;
        private final String email;
        private final String telefone;
        private final String criadoPor;

        public UsuarioData(String id, String nome, String status, String criadoEm,String criadoPor, String email, String telefone) {
            this.id = id;
            this.nome = nome;
            this.status = status;
            this.criadoEm = criadoEm;
            this.criadoPor = criadoPor;
            this.email = email;
            this.telefone = telefone;
        }

        public String getId() { return id; }
        public String getNome() { return nome; }
        public String getStatus() { return status; }
        public String getCriadoEm() { return criadoEm; }
        public String getEmail() { return email; }
        public String getTelefone() { return telefone; }
        public String getCriadoPor() { return criadoPor; }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
