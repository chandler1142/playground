package com.chandler.jdbc;

import java.sql.*;

public class JDBCTest {

    public static void main(String[] args) {
        String username = "root";
        String password = "root";
        String url = "jdbc:mysql://localhost:3306/test";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url,username, password);
            stmt = conn.createStatement();
            rs = stmt.executeQuery("select * from user");
            while(rs.next()) {
                String name = rs.getString(2);
                System.out.println("name : " + name);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

    }

}
