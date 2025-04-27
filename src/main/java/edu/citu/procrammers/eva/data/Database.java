package edu.citu.procrammers.eva.data;

import edu.citu.procrammers.eva.utils.Hash;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.sql.*;

import static edu.citu.procrammers.eva.data.Database.Error.*;

public class Database {

    public static Database instance = null;

    private static final String LOCALHOST = "jdbc:mysql://localhost:3306/";
    private static final String DATABASE_NAME = "dbprojecteva";

    private final String DATABASE_URL = LOCALHOST + DATABASE_NAME;
    private String DB_USERNAME = "root";
    private String DB_PASSWORD = "";

    public static class Error {
        public static final int UNKNOWN_ERROR = -1;
        public static final int NO_ERROR = 0;
        public static final int INVALID_URL = 1;
        public static final int APACHE_NOT_RUNNING = 2;
        public static final int MYSQL_NOT_RUNNING = 3;
        public static final int BOTH_NOT_RUNNING = 4;
        public static final int USERNAME_TAKEN = 5;
    }

    private Connection connection;

    private Database() {}

    public static Database getInstance() {
        if (instance == null)
           instance = new Database();

        return instance;
    }

    /** This function returns a corresponding error if there
     *  is an issue with establishing a connection to the database.
     *  Prompt the user to troubleshoot accordingly.
     */
    public int establishConnection() {
        int serverStatus = checkServerStatus();
        if (serverStatus != NO_ERROR)
            return serverStatus;

        try {
            connection = DriverManager.getConnection(
                DATABASE_URL,
                DB_USERNAME,
                DB_PASSWORD
            );
        } catch (SQLException e) {
            return INVALID_URL;
        }

        return NO_ERROR;
    }

    public User login(String username, String password) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM tbluser WHERE username=? AND password=?"
        ))  {
            String hashedPassword = Hash.hashPassword(password);
            statement.setString(1, username);
            statement.setString(2, hashedPassword);
            ResultSet resultSet = statement.executeQuery();

            int resultUID = -1;
            String resultUsername = null;
            String resultPassword = null;

            while (resultSet.next()) {
                resultUID = resultSet.getInt("uid");
                resultUsername = resultSet.getString("username");
                resultPassword = resultSet.getString("password");
            }

            // TODO: Find a way to implement Null Object Pattern here.
            if (resultUID == -1 || resultUsername == null || resultPassword == null) {
                return null;
            }

            User loggedInUser = new User(resultUID);
            loggedInUser.username = resultUsername;
            loggedInUser.hashedPassword = resultPassword;

            return loggedInUser;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public int register(String username, String password) {
        if (!isUsernameAvailable(username))
            return USERNAME_TAKEN;

        try (PreparedStatement statement = connection.prepareStatement(
            "INSERT INTO tbluser (username, password) values (?, ?)"
        )) {
            String hashedPassword = Hash.hashPassword(password);
            statement.setString(1, username);
            statement.setString(2, hashedPassword);

            statement.executeUpdate();

            return NO_ERROR;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return UNKNOWN_ERROR;
        }
    }

    private boolean isUsernameAvailable(String username) {
        try (PreparedStatement statement = connection.prepareStatement(
            "SELECT 1 FROM tbluser WHERE username=?"
        )) {
            statement.setString(1, username);
            ResultSet usersWithMatchingUsername = statement.executeQuery();
            return !usersWithMatchingUsername.next();
        } catch (SQLException e) {
            return false;
        }
    }

    private static int checkServerStatus() {
        boolean apacheRunning = isPortOpen(80);
        boolean mysqlRunning = isPortOpen(3306);

        if (apacheRunning && mysqlRunning)
            return NO_ERROR;
        else if (!apacheRunning && !mysqlRunning)
            return BOTH_NOT_RUNNING;
        else if (!apacheRunning)
            return APACHE_NOT_RUNNING;
        else
            return MYSQL_NOT_RUNNING;
    }

    private static boolean isPortOpen(int port) {
        try (Socket socket = new Socket()) {
            SocketAddress socketAddress = new InetSocketAddress("localhost", port);
            socket.connect(socketAddress, 2000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
