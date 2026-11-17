
package org.example.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import org.example.entity.Product;

public class Produto {

    private VBox prodView;
    private BorderPane root;

    public Produto(Stage stage) {

        // Painel principal
        root = new BorderPane();

        // Conteúdo da página
        prodView = new VBox(20);
        prodView.setPadding(new Insets(30));
        prodView.setStyle("-fx-background-color: #f4f4f4;");

        // Cabeçalho
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label lblTitulo = new Label("Gestão de produtos");
        lblTitulo.setStyle(
                "-fx-font-size: 35px;" +
                        " -fx-font-weight: bold;"
        );

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);

        Button btnAdd = new Button("Adicionar produto");
        btnAdd.setStyle(
                "-fx-background-color: white;" +
                        " -fx-border-color: #333;" +
                        " -fx-cursor: hand;" +
                        " -fx-font-family: 'Inter';" +
                        " -fx-font-size: 16px;"
        );

        // Tabela de produtos
        TableView<Product> tabela = new TableView<>();
        tabela.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );
        tabela.setStyle("-fx-background-color: #e0e0e0;");

        // Coluna ID
        TableColumn<Product, Integer> colId = new TableColumn<>("Id");
        colId.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        // Coluna Nome
        TableColumn<Product, String> colNome =
                new TableColumn<>("Nome do produto");
        colNome.setCellValueFactory(
                new PropertyValueFactory<>("nome")
        );

        // Coluna Estoque
        TableColumn<Product, Integer> colEstoque =
                new TableColumn<>("Estoque");
        colEstoque.setCellValueFactory(
                new PropertyValueFactory<>("estoque")
        );

        // Coluna Preço
        TableColumn<Product, Double> colPreco =
                new TableColumn<>("Preço");
        colPreco.setCellValueFactory(
                new PropertyValueFactory<>("preco")
        );

        // Coluna Status
        TableColumn<Product, String> colStatus =
                new TableColumn<>("Status produto");
        colStatus.setCellValueFactory(
                new PropertyValueFactory<>("status")
        );

        // Coluna Autor
        TableColumn<Product, String> colAutor =
                new TableColumn<>("Autor");
        colAutor.setCellValueFactory(
                new PropertyValueFactory<>("autor")
        );

        // Coluna Categoria
        TableColumn<Product, Integer> colCat =
                new TableColumn<>("Id categoria");
        colCat.setCellValueFactory(
                new PropertyValueFactory<>("categoria")
        );

        // Coluna Ações
        TableColumn<Product, Void> colAcoes =
                new TableColumn<>("Ações");

        colAcoes.setCellFactory(param -> new TableCell<Product, Void>() {

            private final Button btnEditar = new Button("✏️");
            private final Button btnExcluir = new Button("🗑️");

            {
                btnEditar.setStyle(
                        "-fx-background-color: transparent;" +
                                " -fx-cursor: hand;" +
                                " -fx-font-size: 14px;"
                );

                btnExcluir.setStyle(
                        "-fx-background-color: transparent;" +
                                " -fx-cursor: hand;" +
                                " -fx-font-size: 14px;"
                );
            }

            private final HBox containerBotoes =
                    new HBox(10, btnEditar, btnExcluir);

            {
                containerBotoes.setAlignment(Pos.CENTER);
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
        });

        // Adiciona as colunas à tabela
        tabela.getColumns().addAll(
                colId,
                colNome,
                colEstoque,
                colPreco,
                colStatus,
                colAutor,
                colCat,
                colAcoes
        );

        // Configura o crescimento da tabela
        VBox.setVgrow(tabela, Priority.ALWAYS);

        // Monta o cabeçalho
        header.getChildren().addAll(
                lblTitulo,
                espaco,
                btnAdd
        );

        // Adiciona os componentes ao conteúdo
        prodView.getChildren().addAll(header, tabela);

        // Adiciona o conteúdo ao centro do BorderPane
        root.setCenter(prodView);

        // Cria e adiciona o menu lateral
        MenuLateral menu = new MenuLateral();
        menu.criarMenuLateral(root, stage);
    }

    // Retorna a tela completa
    public BorderPane getView() {
        return root;
    }
}
