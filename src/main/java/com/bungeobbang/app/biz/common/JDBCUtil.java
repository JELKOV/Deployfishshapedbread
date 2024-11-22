package com.bungeobbang.app.biz.common;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class JDBCUtil {

    private static String driverName;
    private static String url;
    private static String userName;
    private static String password;

    static {
        // 프로퍼티 파일 읽기
        try (InputStream input = JDBCUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
            Properties properties = new Properties();
            if (input != null) {
                properties.load(input);
                driverName = properties.getProperty("DB_DRIVER", "com.mysql.cj.jdbc.Driver");
                url = properties.getProperty("DB_URL", "jdbc:mysql://localhost:3306/fishshapedbread");
                userName = properties.getProperty("DB_USERNAME", "root");
                password = properties.getProperty("DB_PASSWORD", "1234");
            } else {
                System.err.println("application.properties 파일을 찾을 수 없습니다.");
            }
        } catch (IOException e) {
            System.err.println("application.properties 파일을 읽을 수 없습니다.");
        }
    }

    public static Connection connect() {
        Connection conn = null;

        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            System.err.println("Class.forName(driverName) fail");
        } finally {
            System.out.println("드라이버를 메모리에 로드(load,적재)");
        }

        try {
            conn = DriverManager.getConnection(url, userName, password);
        } catch (SQLException e) {
            System.err.println("Connection fail");
        } finally {
            System.out.println("연결 객체 확보");
        }

        return conn;
    }

    public static boolean disconnect(Connection conn, PreparedStatement pstmt) {
        try {
            if (pstmt == null || conn == null) {
                System.err.println("pstmt, conn null");
                return false;
            }
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("pstmt, conn close fail");
            return false;
        } finally {
            System.out.println("연결 해제");
        }
        return true;
    }
}
