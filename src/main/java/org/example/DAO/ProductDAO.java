package org.example.DAO;

import org.example.db.Connect;
import org.example.dto.Result;
import org.example.entity.Product;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    public Result<Product> createProduct(Integer idCategory, Integer createdBy, String bookAuthor, String nameProduct, String description, Double price, Integer stock, String status) {
        if (idCategory == null || idCategory <= 0 || createdBy == null || createdBy <= 0 || bookAuthor == null || nameProduct == null || description == null || price == null || price <= 0.0 || stock == null || stock <= 0 || status == null) {
            return new Result<>(false, 400, "Todos os campos são obrigatórios.", null);
        }

        String insertQuery = """
        INSER INTO `products` (
            fk_id_category,
            fk_created_by,
            book_author,
            name_product,
            description,
            price,
            stock,
            product_status,
            created_at
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        Connection conn = Connect.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
            LocalDateTime createdAt = LocalDateTime.now();

            ps.setInt(1, idCategory);
            ps.setInt(2, createdBy);
            ps.setString(3, bookAuthor);
            ps.setString(4, nameProduct);
            ps.setString(5, description);
            ps.setDouble(6, price);
            ps.setInt(7, stock);
            ps.setString(8, status);
            ps.setTimestamp(9, Timestamp.valueOf(createdAt));

            ps.executeUpdate();

            int idProduct = 0;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    idProduct = keys.getInt(1);
                }
            }

            Product product = new Product(idProduct, idCategory, createdBy, bookAuthor, nameProduct, description, price, stock, status, createdAt);
            return new Result<>(true, 201, "Produto cadastrado com sucesso.", product);

        } catch (SQLIntegrityConstraintViolationException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("fk_id_category")) {
                return new Result<>(false, 404, "Categoria não encontrada.", null);
            }
            if (msg != null && msg.contains("fk_created_by")) {
                return new Result<>(false, 404, "Usuário não encontrado.", null);
            }
            return new Result<>(false, 400, "Violação de integridade dos dados.", null);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result<>(false, 500, "Erro ao cadastrar produto.", null);
        }
    }

    public Result<Product> updateProduct(Integer idProduct, Integer idCategory, Integer createdBy, String bookAuthor, String nameProduct, String description, Double price, Integer stock, String status) {
        if (idProduct == null || idProduct <=0 ||idCategory == null || idCategory <= 0 || createdBy == null || createdBy <= 0 || bookAuthor == null || nameProduct == null || description == null || price == null || price <= 0.0 || stock == null || stock <= 0 || status == null) {
            return new Result<>(false, 400, "Todos os campos são obrigatórios.", null);
        }

        String updateQuery = """
        UPDATE `products` SET
            fk_id_category = ?,
            fk_created_by = ?,
            book_author = ?,
            name_product = ?,
            description = ?,
            price = ?,
            stock = ?,
            product_status = ?
        WHERE id_product = ?
        """;

        Connection conn = Connect.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(updateQuery)) {

            ps.setInt(1, idCategory);
            ps.setInt(2, createdBy);
            ps.setString(3, bookAuthor);
            ps.setString(4, nameProduct);
            ps.setString(5, description);
            ps.setDouble(6, price);
            ps.setInt(7, stock);
            ps.setString(8, status);
            ps.setInt(9, idProduct);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                return new Result<>(false, 404, "Produto não encontrado.", null);
            }

            Product product = new Product(idProduct, idCategory);
            return new Result<>(true, 200, "Produto atualizado com sucesso.", product);

        } catch (SQLIntegrityConstraintViolationException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("fk_id_category")) {
                return new Result<>(false, 404, "Categoria não encontrada.", null);
            }
            if (msg != null && msg.contains("fk_created_by")) {
                return new Result<>(false, 404, "Usuário não encontrado.", null);
            }
            return new Result<>(false, 400, "Violação de integridade dos dados.", null);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result<>(false, 500, "Erro ao atualizar produto.", null);
        }
    }

    public Result<Void> deleteProduct(Integer idProduct) {
        if (idProduct == null || idProduct <= 0) {
            return new Result<>(false, 400, "Todos os campos são obrigatórios.", null);
        }

        String deleteQuery = "DELETE FROM `products` WHERE id_product = ?";

        Connection conn = Connect.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(deleteQuery)) {
            ps.setInt(1, idProduct);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                return new Result<>(false, 404, "Produto não encontrado.", null);
            }

            return new Result<>(true, 200, "Produto deletar com sucesso.", null);

        } catch (SQLException e) {
            e.printStackTrace();
            return new Result<>(false, 500, "Erro ao deletar produto.", null);
        }
    }

    public Result<List<Product>> getAllProducts() {
        String selectQuery = """
        SELECT
            p.id_products,
            p.fk_id_category,
            p.fk_created_by,
            u.user_name AS creator_name,
            p.book_author,
            p.name_product,
            p.description,
            p.price,
            p.stock,
            p.product_status,
            p.created_at
        FROM `products` AS p
        LEFT JOIN `user` u
            ON p.created_by = u.id_user
        ORDER BY id_products DESC
        """;

        Connection conn = Connect.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(selectQuery)) {
            try (ResultSet result = ps.executeQuery()) {
                List<Product> products = new ArrayList<>();
                while (result.next()) {
                    products.add(new Product(
                            result.getInt("id_products"),
                            result.getInt("fk_id_category"),
                            result.getInt("fk_created_by"),
                            result.getString("creator_name"),
                            result.getString("book_author"),
                            result.getString("name_product"),
                            result.getString("description"),
                            result.getDouble("price"),
                            result.getInt("stock"),
                            result.getString("product_status"),
                            result.getTimestamp("created_at").toLocalDateTime()
                    ));
                }
                return new Result<>(true, 200, "Produtos listados com sucesso.", products);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result<>(false, 500, "Erro ao listar produtos.", null);
        }
    }
}
