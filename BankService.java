package com.portfolio.bank.service;
import com.portfolio.bank.data.Database;
import com.portfolio.bank.model.*;
import java.sql.*;
import java.util.*;

public class BankService {
    public User getUser(long id) throws SQLException {
        try (Connection c = Database.connect(); PreparedStatement ps = c.prepareStatement("SELECT * FROM users WHERE id=?")) {
            ps.setLong(1,id); try (ResultSet rs=ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("User account not found");
                return new User(rs.getLong("id"),rs.getString("full_name"),rs.getString("email"),rs.getString("account_number"),rs.getDouble("balance"));
            }
        }
    }
    public void deposit(long id,double amount) throws SQLException { transact(id,amount,"DEPOSIT","Cash deposit"); }
    public void withdraw(long id,double amount) throws SQLException {
        validate(amount);
        try (Connection c=Database.connect()) {
            c.setAutoCommit(false);
            try {
                if (amount>balance(c,id)) throw new IllegalArgumentException("Insufficient balance.");
                update(c,id,-amount); insert(c,id,"WITHDRAWAL",amount,"Cash withdrawal"); c.commit();
            } catch(Exception e) { c.rollback(); if(e instanceof SQLException se) throw se; throw e; }
        }
    }
    public void transfer(long from,String destination,double amount) throws SQLException {
        validate(amount);
        try (Connection c=Database.connect()) {
            c.setAutoCommit(false);
            try {
                long to;
                try(PreparedStatement ps=c.prepareStatement("SELECT id FROM users WHERE account_number=?")) {
                    ps.setString(1,destination.trim()); try(ResultSet rs=ps.executeQuery()) {
                        if(!rs.next()) throw new IllegalArgumentException("Destination account was not found."); to=rs.getLong(1);
                    }
                }
                if(to==from) throw new IllegalArgumentException("You cannot transfer to your own account.");
                if(amount>balance(c,from)) throw new IllegalArgumentException("Insufficient balance.");
                update(c,from,-amount); update(c,to,amount);
                insert(c,from,"TRANSFER",amount,"Transfer to "+destination);
                insert(c,to,"TRANSFER_RECEIVED",amount,"Transfer received"); c.commit();
            } catch(Exception e) { c.rollback(); if(e instanceof SQLException se) throw se; throw e; }
        }
    }
    private void transact(long id,double amount,String type,String desc) throws SQLException {
        validate(amount);
        try(Connection c=Database.connect()) {
            c.setAutoCommit(false);
            try { update(c,id,amount); insert(c,id,type,amount,desc); c.commit(); }
            catch(Exception e) { c.rollback(); if(e instanceof SQLException se) throw se; throw e; }
        }
    }
    private void validate(double amount) { if(!Double.isFinite(amount)||amount<=0) throw new IllegalArgumentException("Enter a valid amount greater than zero."); }
    private double balance(Connection c,long id) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement("SELECT balance FROM users WHERE id=?")) {
            ps.setLong(1,id); try(ResultSet rs=ps.executeQuery()) { if(!rs.next()) throw new SQLException("Account not found"); return rs.getDouble(1); }
        }
    }
    private void update(Connection c,long id,double delta) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement("UPDATE users SET balance=balance+? WHERE id=?")) { ps.setDouble(1,delta); ps.setLong(2,id); ps.executeUpdate(); }
    }
    private void insert(Connection c,long id,String type,double amount,String desc) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement("INSERT INTO transactions(user_id,type,amount,description) VALUES(?,?,?,?)")) {
            ps.setLong(1,id); ps.setString(2,type); ps.setDouble(3,amount); ps.setString(4,desc); ps.executeUpdate();
        }
    }
    public List<BankTransaction> history(long id) throws SQLException {
        List<BankTransaction> out=new ArrayList<>();
        try(Connection c=Database.connect(); PreparedStatement ps=c.prepareStatement("SELECT * FROM transactions WHERE user_id=? ORDER BY id DESC LIMIT 100")) {
            ps.setLong(1,id); try(ResultSet rs=ps.executeQuery()) {
                while(rs.next()) out.add(new BankTransaction(rs.getLong("id"),rs.getString("type"),rs.getDouble("amount"),rs.getString("description"),rs.getString("created_at")));
            }
        } return out;
    }
}
