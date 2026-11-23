
package org.example.views;

import javafx.scene.layout.*;
import org.example.DAO.ProductDAO;
import org.example.DAO.CategoryDAO;
import org.example.dto.Result;
import org.example.entity.Product;
import org.example.entity.Category;
import org.example.entity.User;
import org.example.util.UserSession;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class Produto {

    private final Stage stage;
    private final ProductDAO productDAO = new ProductDAO();
    private final BorderPane root = new BorderPane();
    private final TableView<Product> tabela = new TableView<>();

    public Produto(Stage stage) {
        this.stage = stage;
        configurarTabela();
        configurarTela();
        carregarProdutos();
    }

    public BorderPane getView() {
        return root;
    }

    private void configurarTela() {

        Label titulo = new Label("Gestão de produtos");
        titulo.setStyle(
                "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;"
        );

        Button btnAdd = new Button("Adicionar produto");
        btnAdd.setStyle(
                "-fx-background-color: #2F1CD8;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 8 16 8 16;" +
                        "-fx-cursor: hand;"
        );
        btnAdd.setOnAction(event -> adicionarProduto());

        HBox header = new HBox(titulo, btnAdd);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(20));

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);
        header.getChildren().add(1, espaco);

        VBox conteudo = new VBox(15, header, tabela);
        conteudo.setPadding(new Insets(10));

        root.setCenter(conteudo);
    }

    private void configurarTabela() {

        TableColumn<Product, Integer> colunaId = new TableColumn<>("ID");
        colunaId.setStyle("-fx-alignment: center; ");
        colunaId.setCellValueFactory( new PropertyValueFactory<>("id"));

        TableColumn<Product, String> colunaNome = new TableColumn<>("Nome");
        colunaNome.setStyle("-fx-alignment: center; ");
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nameProduct"));

        TableColumn<Product, String> colunaAutor = new TableColumn<>("Autor");
        colunaAutor.setStyle("-fx-alignment: center; ");
        colunaAutor.setCellValueFactory(new PropertyValueFactory<>("bookAuthor"));

        TableColumn<Product, Integer> colunaEstoque = new TableColumn<>("Estoque");
        colunaEstoque.setStyle("-fx-alignment: center; ");
        colunaEstoque.setCellValueFactory(new PropertyValueFactory<>("stock"));

        TableColumn<Product, Double> colunaPreco = new TableColumn<>("Preço");
        colunaPreco.setStyle("-fx-alignment: center; ");
        colunaPreco.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<Product, String> colunaStatus = new TableColumn<>("Status");
        colunaStatus.setStyle("-fx-alignment: center; ");
        colunaStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Product, Integer> colunaCategoria = new TableColumn<>("ID Categoria");
        colunaCategoria.setStyle("-fx-alignment: center; ");
        colunaCategoria.setCellValueFactory(new PropertyValueFactory<>("idCategory"));


        TableColumn<Product, Void> colunaAcoes = new TableColumn<>("Ações");
        colunaAcoes.setStyle("-fx-alignment: center; ");
        colunaAcoes.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnExcluir = new Button("Excluir");

            private final HBox botoes = new HBox(8, btnEditar, btnExcluir);
            {
                btnEditar.setOnAction(event -> {
                    Product produto = getTableView().getItems().get(getIndex());
                    editarProduto(produto);
                });

                btnExcluir.setOnAction(event -> {
                    Product produto = getTableView().getItems().get(getIndex());
                    excluirProduto(produto);
                });

                botoes.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean vazio) {
                super.updateItem(item, vazio);
                if (vazio) {
                    setGraphic(null);
                } else {
                    setGraphic(botoes);
                }
            }
        });

        tabela.getColumns().addAll(
                colunaId, colunaNome,
                colunaAutor, colunaEstoque,
                colunaPreco, colunaStatus,
                colunaCategoria, colunaAcoes
        );



        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void carregarProdutos() {

        Result<List<Product>> resultado = productDAO.getAllProducts();

        if (resultado == null || !resultado.isSuccess()) {
            mostrarErro(resultado != null ? resultado.getMessage() : "Não foi possível carregar os produtos.");
            return;
        }

        tabela.setItems(
                FXCollections.observableArrayList(resultado.getData())
        );
    }

    private void adicionarProduto() {

        User usuario = UserSession.getLoggedUser();
        Optional<Product> opcional = AddProduto.abrir(stage);

        if (opcional.isEmpty()) {
            return;
        }

        Product produto = opcional.get();

        Result<Product> resultado = productDAO.createProduct(
                produto.getIdCategory(), usuario.getId(),
                produto.getBookAuthor(), produto.getNameProduct(),
                produto.getDescription(), produto.getPrice(),
                produto.getStock(), produto.getStatus()
        );

        if (resultado != null && resultado.isSuccess()) {
            mostrarSucesso("Produto cadastrado com sucesso!");
            carregarProdutos();
        }
        else {
            mostrarErro(resultado != null ? resultado.getMessage() : "Não foi possível cadastrar o produto.");
        }
    }

    private void editarProduto(Product produto){
        Optional<Product> opcional = AddProduto.abrir(stage, produto);

        if (opcional.isEmpty()) {
            return;
        }
        Product atualizado = opcional.get();
        Result<Product> resultado = productDAO.updateProduct(
                atualizado.getId(), atualizado.getIdCategory(),
                atualizado.getCreatedBy(), atualizado.getBookAuthor(),
                atualizado.getNameProduct(), atualizado.getDescription(),
                atualizado.getPrice(), atualizado.getStock(), atualizado.getStatus()
        );

        if (resultado != null && resultado.isSuccess()){
            mostrarSucesso("Produto atualizado com sucesso!");
            carregarProdutos();
        }
        else{
            mostrarErro(resultado != null ? resultado.getMessage() : "Não foi possível atualizar o produto.");
        }
    }

    private void excluirProduto(Product produto){

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Excluir produto");
        confirmacao.setHeaderText("Deseja realmente excluir este produto?");
        confirmacao.setContentText(produto.getNameProduct());

        Optional<ButtonType> resposta = confirmacao.showAndWait();
        if (resposta.isEmpty() || resposta.get() != ButtonType.OK){
            return;
        }

        Result<Void> resultado = productDAO.deleteProduct(produto.getId());

        if (resultado != null && resultado.isSuccess()){
            mostrarSucesso("Produto excluído com sucesso!");
            carregarProdutos();
        }
        else{
            mostrarErro(resultado != null ? resultado.getMessage() : "Não foi possível excluir o produto.");
        }
    }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarSucesso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
