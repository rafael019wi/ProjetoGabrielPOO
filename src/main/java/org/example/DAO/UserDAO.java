package org.example.DAO;

import org.example.db.Connect;
import org.example.entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDAO {

    public boolean createUser(User user) {
        String insertQuery = "INSERT INTO `user` (user_name, user_email, user_phone, user_password, user_status, created_at) VALUES (?, ?, ?, ?, ?, NOW())";

        Connection connection = Connect.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement(insertQuery);
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getStatus());

            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erro ao cadastrar usuário");
            return false;
        }
    }
}
