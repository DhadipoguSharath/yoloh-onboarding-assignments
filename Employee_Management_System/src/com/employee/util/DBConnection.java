package com.employee.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class DBConnection {
    
    private static final String URL = System.getenv("URL");
    private static final String USERNAME = System.getenv("USERNAME");
    private static final String PASSWORD = System.getenv("PASSWORD");

    static {
        try
        {
            Class.forName("org.postgresql.Driver");
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void close(Connection con, PreparedStatement pstmt, ResultSet rs) {

        try {
            if (rs != null)
                rs.close();

            if (pstmt != null)
                pstmt.close();

            if (con != null)
                con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
