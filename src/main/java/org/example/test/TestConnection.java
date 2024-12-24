package org.example.test;
import java.sql.Connection;
import java.sql.DriverManager;

public class TestConnection {
    public static void main(String[] args) {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=ChattingApp;TrustServerCertificate=True;";
        String username = "sa";
        String password = "Rot@2112002Ma"; 
        try (Connection connection = DriverManager.getConnection(url,username ,password )) {
            System.out.println("Connection successful!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
