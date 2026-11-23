package org.example.views;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.example.DAO.CategoryDAO;
import org.example.dto.Result;
import org.example.entity.Category;
import org.example.util.UserSession;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Categoria {

    // Ícones em SVG
    private static final String EDIT_ICON =
            "M2 14l1-4L11 2l3 3-8 8-4 1z M9 4l3 3";
    private static final String DELETE_ICON =
            "M2 4h12 M6 4V2h4v2 M4 4l1 10h6l1-10 M7 6v5 M9 6v5";

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Único ponto de acesso ao banco nesta tela
    private final CategoryDAO categoryDAO = new CategoryDAO();

    // ---------------------------------------------------------------
    // Montagem da tela
    // ---------------------------------------------------------------

    public VBox getView(Stage stage) {
        // Tabela com os dados do banco
        TableView<Category> table = createTable(stage);

        Button addButton = createButton("Adicionar categoria");
        addButton.setOnAction(event -> openAddForm(stage, table));

        Label title = new Label("Gestão de categorias");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(16, title, spacer, addButton);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(20));

        VBox content = new VBox(15, header, table);
        content.setPadding(new Insets(10));
        VBox.setVgrow(table, Priority.ALWAYS);

        return content;
    }

    // ---------------------------------------------------------------
    // Ações com o banco
    // ---------------------------------------------------------------

    // Busca as categorias e atualiza a tabela
    private void loadCategories(TableView<Category> table) {
        Result<List<Category>> result = categoryDAO.getAllCategories();

        if (result.isSuccess()) {
            table.getItems().setAll(result.getData());
        } else {
            showError(result.getMessage());
        }
    }

    // Cadastra a categoria com o ID do usuário autenticado
    private void openAddForm(Stage stage, TableView<Category> table) {
        Consumer<String> onSave = name -> {
            Result<Category> result =
                    categoryDAO.createCategory(UserSession.getLoggedUser().getId(), name);

            if (result.isSuccess()) {
                loadCategories(table);
            } else {
                showError(result.getMessage());
            }
        };

        openForm(stage, "", "Adicionar", onSave);
    }

    // Abre o formulário preenchido para edição
    private void openEditForm(Stage stage, TableView<Category> table,
                              Category category) {
        Consumer<String> onSave = name -> {
            Result<Category> result =
                    categoryDAO.updateCategory(category.getId(), name);

            if (result.isSuccess()) {
                loadCategories(table);
            } else {
                showError(result.getMessage());
            }
        };

        openForm(stage, category.getName(), "Salvar alterações", onSave);
    }

    // Pede confirmação antes de excluir
    private void openDeleteConfirmation(Stage stage,
                                        TableView<Category> table,
                                        Category category) {
        Label message = new Label(
                "Deseja excluir a categoria \"" + category.getName() + "\"?");
        message.setWrapText(true);
        message.setStyle("-fx-font-size: 18px;");

        Runnable onConfirm = () -> {
            Result<Void> result =
                    categoryDAO.deleteCategory(category.getId());

            if (result.isSuccess()) {
                loadCategories(table);
            } else {
                showError(result.getMessage());
            }
        };

        openModal(stage, "Excluir categoria",
                message, "Confirmar", onConfirm);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }

    // ---------------------------------------------------------------
    // Tabela
    // ---------------------------------------------------------------

    private TableView<Category> createTable(Stage stage) {
        TableView<Category> table = new TableView<>();
        table.setPlaceholder(new Label("Nenhuma categoria cadastrada."));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Category, String> idColumn =
                createColumn("ID da categoria", 103, this::getIdText);
        TableColumn<Category, String> nameColumn =
                createColumn("Nome da categoria", 129, Category::getName);
        TableColumn<Category, String> dateColumn =
                createColumn("Criado em", 89, this::getDateText);
        TableColumn<Category, String> creatorColumn =
                createColumn("Criado por", 89, this::getCreatorText);
        TableColumn<Category, Void> actionsColumn =
                createActionsColumn(stage, table);

        table.getColumns().setAll(
                List.of(idColumn, nameColumn, dateColumn,
                        creatorColumn, actionsColumn));

        loadCategories(table);

        return table;
    }

    // Cria uma coluna usando o texto informado por getValue
    private TableColumn<Category, String> createColumn(
            String title, double minWidth,
            Function<Category, String> getValue) {
        TableColumn<Category, String> column = new TableColumn<>(title);
        column.setMinWidth(minWidth);
        column.setReorderable(false);
        column.setStyle("-fx-alignment: CENTER;");
        column.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(getValue.apply(cell.getValue())));

        return column;
    }

    // Coluna com os botões de editar e excluir
    private TableColumn<Category, Void> createActionsColumn(
            Stage stage, TableView<Category> table) {
        TableColumn<Category, Void> column = new TableColumn<>("Ações");
        column.setMinWidth(80);
        column.setSortable(false);
        column.setReorderable(false);

        column.setCellFactory(col -> {
            TableCell<Category, Void> cell = new TableCell<>();

            Button editButton = createIconButton("Editar categoria", EDIT_ICON);
            Button deleteButton = createIconButton("Excluir categoria", DELETE_ICON);

            HBox actions = new HBox(4, editButton, deleteButton);
            actions.setAlignment(Pos.CENTER);

            // Exibe os botões apenas nas linhas preenchidas
            cell.graphicProperty().bind(
                    Bindings.when(cell.emptyProperty())
                            .then((Node) null)
                            .otherwise(actions)
            );

            editButton.setOnAction(event -> {
                Category category = cell.getTableRow().getItem();
                openEditForm(stage, table, category);
            });

            deleteButton.setOnAction(event -> {
                Category category = cell.getTableRow().getItem();
                openDeleteConfirmation(stage, table, category);
            });

            return cell;
        });

        return column;
    }

    private String getIdText(Category category) {
        return String.valueOf(category.getId());
    }

    private String getDateText(Category category) {
        if (category.getCreatedAt() == null) {
            return "";
        }

        return category.getCreatedAt().format(DATE_FORMAT);
    }

    private String getCreatorText(Category category) {
        if (category.getCreatorName() == null) {
            return "";
        }

        return category.getCreatorName();
    }

    // ---------------------------------------------------------------
    // Janelas modais
    // ---------------------------------------------------------------

    // onSave recebe o nome digitado no formulário
    private void openForm(Stage stage, String currentName, String action,
                          Consumer<String> onSave) {
        TextField nameField = new TextField(currentName);
        nameField.setPromptText("Nome da categoria");
        nameField.setAccessibleText("Nome da categoria");
        nameField.setPrefHeight(36);

        String title = action.equals("Adicionar")
                ? "Adicionar categoria" : "Editar categoria";

        Runnable onConfirm =
                () -> onSave.accept(nameField.getText().strip());

        openModal(stage, title, nameField, action, onConfirm);
    }

    private void openModal(Stage owner, String title, Node content,
                           String action, Runnable onConfirm) {
        Stage window = new Stage();
        window.initOwner(owner);
        window.initModality(Modality.WINDOW_MODAL);
        window.setTitle(title);
        window.setResizable(false);

        Button cancelButton = new Button("Cancelar");
        cancelButton.setCancelButton(true);
        cancelButton.setOnAction(event -> window.close());

        Button confirmButton = createButton(action);
        confirmButton.setDefaultButton(true);
        confirmButton.setOnAction(event -> {
            onConfirm.run();
            window.close();
        });

        Node formContent = content;

        // Bloqueia a confirmação enquanto o nome estiver vazio
        if (content instanceof TextField nameField) {
            confirmButton.disableProperty().bind(
                    Bindings.createBooleanBinding(
                            () -> nameField.getText().isBlank(),
                            nameField.textProperty())
            );

            nameField.setOnAction(event -> confirmButton.fire());
            formContent = new VBox(8, new Label("Nome da categoria:"), nameField);
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox buttons = new HBox(16, cancelButton, spacer, confirmButton);
        buttons.setAlignment(Pos.CENTER_LEFT);

        VBox panel = new VBox(20, formContent, buttons);
        panel.setPadding(new Insets(24));
        panel.setPrefWidth(480);
        panel.setStyle("-fx-background-color: #F4F4F4;");

        Scene scene = new Scene(panel);

        // ESC fecha a janela
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                window.close();
                event.consume();
            }
        });

        window.setScene(scene);

        // Centraliza o modal sobre a tela principal
        window.setOnShown(event -> {
            window.setX(
                    owner.getX() + (owner.getWidth() - window.getWidth()) / 2);
            window.setY(
                    owner.getY() + (owner.getHeight() - window.getHeight()) / 2);
        });

        window.showAndWait();
    }

    // ---------------------------------------------------------------
    // Componentes reutilizáveis
    // ---------------------------------------------------------------

    private Button createButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: #2F1CD8;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 8 16 8 16;" +
                        "-fx-cursor: hand;");

        return button;
    }

    private Button createIconButton(String description, String iconPath) {
        SVGPath icon = new SVGPath();
        icon.setContent(iconPath);
        icon.setFill(null);
        icon.setStroke(Color.web("#111111"));
        icon.setStrokeWidth(1.5);
        icon.setStrokeLineCap(StrokeLineCap.ROUND);
        icon.setStrokeLineJoin(StrokeLineJoin.ROUND);

        Button button = new Button("", icon);
        button.setAccessibleText(description);
        button.setTooltip(new Tooltip(description));
        button.setStyle(
                "-fx-background-color: transparent; -fx-padding: 5; -fx-cursor: hand;");

        return button;
    }

}
