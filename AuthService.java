package com.portfolio.bank.service;
import com.portfolio.bank.data.Database;
import com.portfolio.bank.model.User;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

public class AuthService {
    public User login(String email, String password) throws SQLException {
        try (Connection c = Database.connect(); PreparedStatement ps = c.prepareStatement("SELECT * FROM users WHERE lower(email)=lower(?)")) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next() || !BCrypt.checkpw(password, rs.getString("password_hash"))) return null;
                return map(rs);
            }
        }
    }
    public void register(String name, String email, String password) throws SQLException {
        if (name.isBlank() || email.isBlank() || password.length() < 8) throw new IllegalArgumentException("Complete all fields and use a password of at least 8 characters.");
        String sql = "INSERT INTO users(full_name,email,password_hash,account_number,balance) VALUES(?,?,?,?,0)";
        try (Connection c = Database.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name.trim()); ps.setString(2, email.trim().toLowerCase());
            ps.setString(3, BCrypt.hashpw(password, BCrypt.gensalt(12)));
            ps.setString(4, "NV" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
            ps.executeUpdate();
        }
    }
    private User map(ResultSet rs) throws SQLException {
        return new User(rs.getLong("id"), rs.getString("full_name"), rs.getString("email"), rs.getString("account_number"), rs.getDouble("balance"));
    }
}
