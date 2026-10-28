package org.example.DAO;

import org.example.db.Connect;
import org.example.dto.Result;
import org.example.entity.User;
import org.example.util.PasswordUtil;
import org.example.util.UserUtil;

import java.sql.*;
import java.time.LocalDateTime;

public class UserDAO {

    public Result<User> createUser(String name, String email, String phone, String password, String confirmPassword, String status) {
        if (name == null || email == null || phone == null || password == null || confirmPassword == null || status == null) {
            return new Result<>(false, 400, "Todos os campos são obrigatórios.", null);
        }

        String insertQuery = "INSERT INTO `user` (user_name, user_email, user_phone, user_password, user_status, created_at) VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = Connect.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {

            if (!UserUtil.validarPasswords(password, confirmPassword)) {
                System.out.println("As senhas são diferentes.");
                return new Result<>(false, 400, "As senhas são diferentes.", null);
            }

            String hashPassword = PasswordUtil.criarHash(password);

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
                if (!PasswordUtil.verificarHash(password, user_password)) {
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
}
