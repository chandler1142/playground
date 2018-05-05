package com.chandler.jdbc;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.KeyedHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CommonDBUtilsTest {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        QueryRunner runner = new QueryRunner();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root","root");
            Map<Integer, Map<String, Object>> result = runner.query(conn,  "select * from user ", new KeyedHandler<Integer>(2));

            DbUtils.close(conn);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
