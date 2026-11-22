package org.example.views;

import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.util.Callback;
import org.example.DAO.CategoryDAO;
import org.example.dto.Result;
import org.example.entity.Product;
import org.example.entity.Category;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class AddProduto {

    private static final CategoryDAO categoryDAO = new CategoryDAO();
    public static Optional<Product> abrir(Stage stage) {
        return abrir(stage, null);
    }
    public static Optional<Product> abrir(Stage stage, Product produto) {

        boolean editando = produto != null;
        Result<List<Category>> resultadoCategorias = categoryDAO.getAllCategories();
        if (resultadoCategorias == null || !resultadoCategorias.isSuccess() || resultadoCategorias.getData() == null) {
            mostrarErro("Não foi possível carregar as categorias.");
            return Optional.empty();
        }

        List<Category> categorias = resultadoCategorias.getData();
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle(editando ? "Editar produto" : "Adicionar produto");
        dialog.initOwner(stage);
        dialog.initModality(Modality.APPLICATION_MODAL);

        ButtonType cancelar = new ButtonType("CANCELAR", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType confirmar = new ButtonType(editando ? "SALVAR ALTERAÇÕES" : "ADICIONAR", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(cancelar, confirmar);

        TextField txtNome = new TextField();
        txtNome.setPromptText("Nome do produto");


        TextField txtAutor = new TextField();
        txtAutor.setPromptText("Autor");


        TextArea txtDescricao = new TextArea();
        txtDescricao.setPromptText("Descrição do produto");

        txtDescricao.setPrefRowCount(3);
        txtDescricao.setWrapText(true);

        TextField txtQuantidade = new TextField();
        txtQuantidade.setPromptText("Quantidade");


        TextField txtPreco = new TextField();
        txtPreco.setPromptText("Preço");


        ComboBox<String> cbStatus = new ComboBox<>();
        cbStatus.getItems().addAll("active", "blocked");
        cbStatus.setPromptText("Selecione o status");

        cbStatus.setMaxWidth(Double.MAX_VALUE);

        TextField txtIdCategoria = new TextField();
        txtIdCategoria.setPromptText("ID Categoria");


        TextField txtCategoria = new TextField();
        txtCategoria.setPromptText("Categoria");
        txtCategoria.setEditable(false);

        txtIdCategoria.setTextFormatter(new TextFormatter<>(change -> change.getControlNewText().matches("\\d*") ? change : null));

        txtIdCategoria.textProperty().addListener(
                (observable, antigo, novo) -> {
                    txtCategoria.clear();
                    if (novo.isEmpty()) {
                        return;
                    }
                    try {
                        int id = Integer.parseInt(novo);
                        categorias.stream()
                                .filter(c -> c.getId() != null && c.getId() == id)
                                .findFirst()
                                .ifPresent(c -> txtCategoria.setText(c.getName()));

                    }
                    catch (NumberFormatException e) {
                        txtCategoria.clear();
                    }
                }
        );

        if (editando) {
            txtNome.setText(produto.getNameProduct());
            txtAutor.setText(produto.getBookAuthor());
            txtDescricao.setText(produto.getDescription());
            txtQuantidade.setText(String.valueOf(produto.getStock()));
            txtPreco.setText(String.valueOf(produto.getPrice()));
            cbStatus.setValue(produto.getStatus());

            if (produto.getIdCategory() != null) {
                txtIdCategoria.setText(
                        String.valueOf(produto.getIdCategory())
                );
            }
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));
        grid.add(txtNome, 1, 0);
        grid.add(txtAutor, 1, 1);
        grid.add(txtDescricao, 1, 2);
        grid.add(txtQuantidade, 1, 3);
        grid.add(txtPreco, 1, 4);
        grid.add(cbStatus, 1, 5);

        HBox linhaCategoria = new HBox(10, txtIdCategoria, txtCategoria);
        txtIdCategoria.setPrefWidth(100);
        txtCategoria.setPrefWidth(200);
        grid.add(linhaCategoria, 1, 6);

        dialog.setOnShown(event -> {

            Button btnCancelar = (Button) dialog.getDialogPane().lookupButton(cancelar);
            btnCancelar.setStyle(
                    "-fx-font-weight: bold;" +
                            "-fx-font-size: 15px;" +
                            "-fx-background-color: #2F1CD8;" +
                            "-fx-text-fill: white "
            );

            Button btnConfirmar = (Button) dialog.getDialogPane().lookupButton(confirmar);
            btnConfirmar.setStyle(
                    "-fx-font-weight: bold;" +
                            "-fx-font-size: 15px;" +
                            "-fx-background-color: #2F1CD8;" +
                            "-fx-text-fill: white "
            );

            ButtonBar buttonBar = (ButtonBar) dialog.getDialogPane().lookup(".button-bar");
            if (buttonBar != null) {
                buttonBar.getButtons().clear();
            }

            Region espaco = new Region();
            HBox.setHgrow(espaco, Priority.ALWAYS);
            HBox barraPersonalizada = new HBox(10);
            barraPersonalizada.setAlignment(Pos.CENTER);
            barraPersonalizada.setMaxWidth(Double.MAX_VALUE);

            barraPersonalizada.getChildren().addAll(
                    btnCancelar,
                    espaco,
                    btnConfirmar
            );

            VBox conteudo = new VBox(15);
            conteudo.setPadding(new Insets(10));
            conteudo.getChildren().addAll(grid, barraPersonalizada);
            dialog.getDialogPane().setContent(conteudo);
        });

        dialog.getDialogPane().setContent(grid);

        Callback<ButtonType, Product> buttonTypeProductCallback = botao -> {
            if (botao != confirmar) {
                return null;
            }

            String nome = txtNome.getText().trim();
            String autor = txtAutor.getText().trim();
            String descricao = txtDescricao.getText().trim();
            String idTexto = txtIdCategoria.getText().trim();

            if (nome.isEmpty() || autor.isEmpty() || descricao.isEmpty() || txtQuantidade.getText().trim().isEmpty() || txtPreco.getText().trim().isEmpty()
                    || cbStatus.getValue() == null || idTexto.isEmpty()) {
                mostrarErro("Preencha todos os campos.");
                return null;
            }

            Optional<Category> categoriaEncontrada = categorias.stream()
                    .filter(c -> c.getId() != null && c.getId().equals(Integer.valueOf(idTexto)))
                    .findFirst();

            if (categoriaEncontrada.isEmpty()) {
                mostrarErro("Categoria não encontrada. Verifique o ID.");
                return null;
            }

            try {
                int quantidade = Integer.parseInt(txtQuantidade.getText().trim());
                double preco = Double.parseDouble(txtPreco.getText().trim().replace(",", "."));
                if (quantidade <= 0) {
                    mostrarErro("A quantidade deve ser maior que zero.");
                    return null;
                }
                if (preco <= 0) {
                    mostrarErro("O preço deve ser maior que zero.");
                    return null;
                }
                Integer idCategoria = categoriaEncontrada.get().getId();
                if (editando) {
                    return new Product(
                            produto.getId(), idCategoria,
                            produto.getCreatedBy(), autor, nome, descricao,
                            preco, quantidade, cbStatus.getValue(), produto.getCreatedAt());
                }
                return new Product(
                        null, idCategoria, null, autor,
                        nome, descricao, preco,
                        quantidade, cbStatus.getValue(), null);
            }
            catch (NumberFormatException e) {
                mostrarErro("Informe uma quantidade e um preço válidos.");
                return null;
            }
        };
        dialog.setResultConverter(buttonTypeProductCallback);
        return dialog.showAndWait();
    }

    private static void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}