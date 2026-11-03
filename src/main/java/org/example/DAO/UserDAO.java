package org.example.DAO;

import org.example.db.Connect;
import org.example.dto.Result;
import org.example.entity.User;
import org.example.util.PasswordUtil;
import org.example.util.UserUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public Result<User> createUser(String name, String email, String phone, String password, String confirmPassword, String status) {
        if (name == null || email == null || phone == null || password == null || confirmPassword == null || status == null) {
            return new Result<>(false, 400, "Todos os campos são obrigatórios.", null);
        }

        if (!UserUtil.validarPasswords(password, confirmPassword)) {
            System.out.println("As senhas são diferentes.");
            return new Result<>(false, 400, "As senhas são diferentes.", null);
        }


        String insertQuery = "INSERT INTO `user` (user_name, user_email, user_phone, user_password, user_status, created_at) VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = Connect.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            String hashPassword = PasswordUtil.createHash(password);
            LocalDateTime createdAt = LocalDateTime.now();

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, hashPassword);
            ps.setString(5, status);
            ps.setTimestamp(6, Timestamp.valueOf(createdAt));

            // executeUpdate() serve para executar comandos SQL que modificam dados no banco, como: INSERT, UPDATE e DELETE
            ps.executeUpdate();

            // pega id gerado
            int idUser = 0;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    idUser = keys.getInt(1);
                }
            }

            User user = new User(idUser, name, email, phone, status, createdAt);
            return new Result<>(true, 201, "Usuário criado com sucesso.", user);

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                return new Result<>(false, 409, "E-mail já cadastrado.", null);
            }
            e.printStackTrace();
            System.out.println("Erro ao cadastrar usuário");
            return new Result<>(false, 500, "Erro ao cadastrar usuário", null);
        }
    }

    public Result<User> login (String email, String password) {
        if (email == null || password == null) {
            return new Result<>(false, 400, "Todos os campos são obrigatórios.", null);
        }
        String selectQuery = "SELECT id_user, user_name, user_phone, user_password, user_status, created_at FROM `user` WHERE user_email = ? LIMIT 1";

        Connection conn = Connect.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(selectQuery)) {
            ps.setString(1, email);

            // executeQuery serve para realizar SELECT. Ele retorna ResultSet que é um objeto java com os atributos desejados na query
            try (ResultSet result = ps.executeQuery()) {
                if (!result.next()) {
                    System.out.println("Usuário não encontrado.");
                    return new Result<>(false, 401 ,"Entre com e-mail e senha corretos.", null);
                }

                String user_status = result.getString("user_status");
                if (!UserUtil.validarStatus(user_status)) {
                    System.out.println("Usuário bloqueado.");
                    return new Result<>(false, 403, "Entre com e-mail e senha corretos.", null);
                }

                String user_password = result.getString("user_password");
                if (!PasswordUtil.checkHash(password, user_password)) {
                    System.out.println("Senha invalida.");
                    return new Result<>(false, 401, "Entre com e-mail e senha corretos.", null);
                }

                int id_user = result.getInt("id_user");
                String user_name = result.getString("user_name");
                String user_phone = result.getString("user_phone");
                LocalDateTime created_at = result.getTimestamp("created_at").toLocalDateTime();

                User user = new User(id_user, user_name, email, user_phone, user_status, created_at);
                return new Result<>(true, 200, "Usuário autenticado com sucesso.", user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erro ao fazer login.");
            return new Result<>(false, 500, "Erro ao fazer login.", null);
        }
    }

    public Result<Void> deleteUser(Integer idUser) {
        if (idUser == null || idUser <= 0) {
            return new Result<>(false, 400, "ID de usuário inválido.", null);
        }

        String deleteQuery = "DELETE FROM `user` WHERE id_user = ?";

        Connection conn = Connect.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(deleteQuery)) {
            ps.setInt(1, idUser);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                return new Result<>(false, 404, "Usuário não encontrado.", null);
            }

            return new Result<>(true, 200, "Usuário deletado com sucesso.", null);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                return new Result<>(false, 409, "Usuário possui registros vinculados e não pode ser deletado.", null);
            }
            e.printStackTrace();
            System.out.println("Erro ao deletar usuário.");
            return new Result<>(false, 500, "Erro ao deletar usuário.", null);
        }
    }

    public Result<User> updateProfile(String name, String email, String phone, String status, Integer idUser) {
        if (name == null || email == null || phone == null || status == null || idUser == null || idUser <= 0) {
            return new Result<>(false, 400, "Todos os campos são obrigatórios.", null);
        }

        String updateQuery = """
        UPDATE `user` SET
            user_name = ?,
            user_email = ?,
            user_phone = ?,
            user_status = ?
        WHERE id_user = ?
        """;

        Connection conn = Connect.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(updateQuery)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, status);
            ps.setInt(5, idUser);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                return new Result<>(false, 404, "Usuário não encontrado.", null);
            }

            User user = new User(idUser, name, email, phone, status);
            return new Result<>(true, 200, "Usuário editado com sucesso.", user);

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                return new Result<>(false, 409, "E-mail já cadastrado.", null);
            }
            e.printStackTrace();
            System.out.println("Erro ao editar usuário.");
            return new Result<>(false, 500, "Erro ao editar usuário.", null);
        }
    }

    public Result<Void> updatePassword(String oldPassword, String newPassword, String confirmNewPassword, Integer idUser) {
        if (oldPassword == null || newPassword == null || confirmNewPassword == null || idUser == null || idUser <= 0) {
            return new Result<>(false, 400, "Todos os campos são obrigatórios.", null);
        }

        if (!UserUtil.validarPasswords(newPassword, confirmNewPassword)) {
            System.out.println("As senhas são diferentes.");
            return new Result<>(false, 400, "As senhas são diferentes.", null);
        }

        String selectQuery = "SELECT user_password FROM `user` WHERE id_user = ?";

        Connection conn = Connect.getConnection();
        try (PreparedStatement psSelect = conn.prepareStatement(selectQuery)) {

            psSelect.setInt(1, idUser);

            try (ResultSet result = psSelect.executeQuery()) {
                if (!result.next()) {
                    System.out.println("Usuário não encontrado.");
                    return new Result<>(false, 404, "Usuário não encontrado.", null);
                }

                String currentUserPassword = result.getString("user_password");
                if (!PasswordUtil.checkHash(oldPassword, currentUserPassword)) {
                    System.out.println("A senha atual está incorreta.");
                    return new Result<>(false, 401, "A senha atual está incorreta.", null);
                }

                if (PasswordUtil.checkHash(newPassword, currentUserPassword)) {
                    System.out.println("A nova senha deve ser diferente da senha atual.");
                    return new Result<>(false, 400, "A nova senha deve ser diferente da senha atual.", null);
                }

                String updateQuery = """
                UPDATE `user` SET
                    user_password = ?
                WHERE id_user = ?
                """;

                try (PreparedStatement psUpdate = conn.prepareStatement(updateQuery)) {

                    String hashNewPassword = PasswordUtil.createHash(newPassword);

                    psUpdate.setString(1, hashNewPassword);
                    psUpdate.setInt(2, idUser);

                    int rowsAffected = psUpdate.executeUpdate();

                    if (rowsAffected == 0) {
                        return new Result<>(false, 404, "Usuário não encontrado.", null);
                    }

                    return new Result<>(true, 200, "Senha alterada com sucesso.", null);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erro ao alterar senha.");
            return new Result<>(false, 500, "Erro ao alterar senha.", null);
        }
    }

    public Result<List<User>> getAllUsers() {
        String selectQuery = """
        SELECT
            id_user,
            user_name,
            user_email,
            user_phone,
            user_status,
            created_at
        FROM `user`
        """;

        Connection conn = Connect.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(selectQuery)) {
            try (ResultSet result = ps.executeQuery()) {
                List<User> users = new ArrayList<>();
                while (result.next()) {
                    users.add(new User(
                            result.getInt("id_user"),
                            result.getString("user_name"),
                            result.getString("user_email"),
                            result.getString("user_phone"),
                            result.getString("user_status"),
                            result.getTimestamp("created_at").toLocalDateTime()
                    ));
                }
                return new Result<>(true, 200, "Usuários listados com sucesso.", users);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erro ao listar usuários.");
            return new Result<>(false, 500, "Erro ao listar usuários.", null);
        }
    }
}
