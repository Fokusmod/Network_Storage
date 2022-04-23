package ru.gb.storage.server.Database;

import java.sql.*;

public class Settings {
    private final static String DRIVER = "org.sqlite.JDBC";
    private final static String CONNECTION = "jdbc:sqlite:network-storage-template-master/server/Auth.db";
    private final static String CREATE_TABLE = "create table if not exists Account (id integer primary key autoincrement,login text UNIQUE not null, password text not null);";
    private final static String DROP_TABLE = "drop table if exists Account;";
    private final static String ADD_CLIENT = "insert into Account  (login, password) values ('admin', 'admin')";

    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement preparedStatement;

//    public static void main(String[] args) {
//
//        try {
//            connect();
////            dropTable();
//            createTable();
//            addClient();
//        } catch (SQLException | ClassNotFoundException throwables) {
//            throwables.printStackTrace();
//        }
//
//    }

    public void connect() throws SQLException, ClassNotFoundException {
        System.out.println("Подключение к базе данных.");
        Class.forName(DRIVER);
        connection = DriverManager.getConnection(CONNECTION);
        statement = connection.createStatement();


    }

    public static void disconnect() {
        try {
            if (statement != null) statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (connection != null) connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createTable() throws SQLException {
        statement.execute(CREATE_TABLE);

    }


    private static void dropTable() throws SQLException {
        statement.execute(DROP_TABLE);
    }

    private static void addClient() throws SQLException {
        statement.executeUpdate(ADD_CLIENT);
    }

    public boolean login(String login, String pass) {
        try {

            preparedStatement = connection.prepareStatement("select * from Account Where login = ? and password = ?");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, pass);
            ResultSet rs = preparedStatement.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}

