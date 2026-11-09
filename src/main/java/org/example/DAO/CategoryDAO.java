package org.example.DAO;

import org.example.db.Connect;
import org.example.dto.Result;
import org.example.entity.Category;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    public Result<Category> createCategory(Integer createdBy, String name) {
        if (createdBy == null || createdBy <= 0 || name == null || name.isBlank()) {
            return new Result<>(false, 400, "Todos os campos são obrigatórios.", null);
        }

        String insertQuery = "INSERT INTO `categories` (fk_created_by, category_name, created_at) VALUES (?, ?, ?)";

        Connection conn = Connect.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            LocalDateTime createdAt = LocalDateTime.now();

            ps.setInt(1, createdBy);
            ps.setString(2, name.strip());
            ps.setTimestamp(3, Timestamp.valueOf(createdAt));

            ps.executeUpdate();

            // pega id gerado
            int idCategory = 0;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    idCategory = keys.getInt(1);
                }
            }

            Category category = new Category(idCategory, createdBy, name.strip(), createdAt);
            return new Result<>(true, 201, "Categoria cadastrada com sucesso.", category);

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                return new Result<>(false, 409, "Nome de categoria já cadastrada.", null);
            }
            if (e.getErrorCode() == 1452) {
                return new Result<>(false, 404, "Usuário não encontrado.", null);
            }
            e.printStackTrace();
            return new Result<>(false, 500, "Erro ao cadastrar categoria.", null);
        }
    }

    public Result<Category> updateCategory(Integer idCategory, String name) {
        if (idCategory == null || idCategory <= 0 || name == null || name.isBlank()) {
            return new Result<>(false, 400, "Todos os campos são obrigatórios.", null);
        }

        String updateQuery = "UPDATE `categories` SET category_name = ? WHERE id_category = ?";

        Connection conn = Connect.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(updateQuery)) {
            ps.setString(1, name.strip());
            ps.setInt(2, idCategory);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                return new Result<>(false, 404, "Categoria não encontrada.", null);
            }

            Category category = new Category(idCategory, null, name.strip());
            return new Result<>(true, 200, "Categoria atualizada com sucesso.", category);

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                return new Result<>(false, 409, "Nome de categoria já cadastrada.", null);
            }
            e.printStackTrace();
            return new Result<>(false, 500, "Erro ao atualizar categoria.", null);
        }
    }

    public Result<Void> deleteCategory(Integer idCategory) {
        if (idCategory == null || idCategory <= 0) {
            return new Result<>(false, 400, "Todos os campos são obrigatórios.", null);
        }

        String deleteQuery = "DELETE FROM `categories` WHERE id_category = ?";

        Connection conn = Connect.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(deleteQuery)) {
            ps.setInt(1, idCategory);

            int rowAffected = ps.executeUpdate();

            if (rowAffected == 0) {
                return new Result<>(false, 404, "Categoria não encontrada.", null);
            }

            return new Result<>(true, 200, "Categoria deletada com sucesso.", null);

        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                return new Result<>(false, 409, "Categoria possui registros vinculados e não pode ser deletada.", null);
            }
            e.printStackTrace();
            return new Result<>(false, 500, "Erro ao deletar categoria.", null);
        }
    }

    public Result<List<Category>> getAllCategories() {
        String selectQuery = """
        SELECT
            c.id_category,
            c.category_name,
            c.fk_created_by,
            u.user_name AS creator_name,
            c.created_at
        FROM `categories` AS c
        LEFT JOIN `user` AS u
            ON c.fk_created_by = u.id_user
        ORDER BY c.id_category DESC
        """;

        Connection conn = Connect.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(selectQuery)) {
            try (ResultSet result = ps.executeQuery()) {
                List<Category> categories = new ArrayList<>();
                while (result.next()) {
                    categories.add(new Category(
                            result.getInt("id_category"),
                            result.getInt("fk_created_by"),
                            result.getString("creator_name"),
                            result.getString("category_name"),
                            result.getTimestamp("created_at").toLocalDateTime()
                    ));
                }
                return new Result<>(true, 200, "Categorias listadas com sucesso.", categories);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result<>(false, 500, "Erro ao listar categorias.", null);
        }
    }
}
