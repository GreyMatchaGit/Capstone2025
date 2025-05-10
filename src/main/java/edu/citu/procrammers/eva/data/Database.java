package edu.citu.procrammers.eva.data;

import edu.citu.procrammers.eva.utils.Hash;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.sql.*;
import java.util.ArrayList;

import static edu.citu.procrammers.eva.data.Database.Error.*;

public class Database {

    public static Database instance = null;

    private static final String LOCALHOST = "jdbc:mysql://localhost:3306/";
    private static final String DATABASE_NAME = "dbprojecteva";

    private final String DATABASE_URL = LOCALHOST + DATABASE_NAME;
    private final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
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
        public static final int EMPTY_USERNAME = 6;
        public static final int EMPTY_PASSWORD = 7;
        public static final int EMPTY_USERNAME_PASSWORD = 8;
        public static final int TOO_LONG_USERNAME =  9;
        public static final int TOO_LONG_PASSWORD = 10;
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
        if (serverStatus != NO_ERROR) {
            String message = "";
            switch (serverStatus) {
                case UNKNOWN_ERROR -> message = "An unknown error has occurred.";
                case INVALID_URL -> message = "The database connection URL is invalid.";
                case BOTH_NOT_RUNNING -> message = "Both Apache and MySQL modules aren't running.";
                case APACHE_NOT_RUNNING -> message = "The Apache module in XAMPP isn't running.";
                case MYSQL_NOT_RUNNING -> message = "The MySQL module in XAMPP isn't running.";
            }
            System.out.println("[Database] ERROR: " + message);
            return serverStatus;
        }

        try {
            connection = DriverManager.getConnection(
                DATABASE_URL,
                DB_USERNAME,
                DB_PASSWORD
            );
        } catch (SQLException e) {
            createDatabase();
        }

        return NO_ERROR;
    }

    public User login(String username, String password) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM `" + DATABASE_NAME + "`.`tblusers` WHERE username=?"
        ))  {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            int resultUID = -1;
            String resultUsername = null;
            String resultPassword = null;

            while (resultSet.next()) {
                resultUID = resultSet.getInt("id");
                resultUsername = resultSet.getString("username");
                resultPassword = resultSet.getString("password");
            }

            // TODO: Find a way to implement Null Object Pattern here.
            if (resultUID == -1 || resultUsername == null || resultPassword == null) {
                System.out.println("[Database] Login: User not found");
                return null;
            }

            if (Hash.verifyPassword(password, resultPassword)) {
                User loggedInUser = new User(resultUID);
                loggedInUser.username = resultUsername;
                loggedInUser.hashedPassword = resultPassword;

                return loggedInUser;
            }
        } catch (SQLException e) {
            System.out.println("[Database] Login error: " + e.getMessage());
            return null;
        }

        return null;
    }

    public int register(String username, String password) {
        int validationResult = validateUsernamePassword(username, password);

        if (validationResult != NO_ERROR) {
            return validationResult;
        }

        if (!isUsernameAvailable(username)) {
            System.out.printf("[Database] ERROR: The username %s has already been taken.\n", username);
            return USERNAME_TAKEN;
        }

        try (PreparedStatement statement = connection.prepareStatement(
            "INSERT INTO `" + DATABASE_NAME + "`.`tblusers` (username, password) values (?, ?)"
        )) {
            String hashedPassword = Hash.hashPassword(password);
            statement.setString(1, username);
            statement.setString(2, hashedPassword);

            statement.executeUpdate();

            System.out.printf("[Database] %s has been successfully registered.\n", username);
            return NO_ERROR;
        } catch (SQLException e) {
            System.out.println("[Database] Registration error: " + e.getMessage());
            return UNKNOWN_ERROR;
        }
    }

    public int validateUsernamePassword(String username, String password) {
        if (username.isEmpty() && password.isEmpty()) {
            System.out.println("[Database] Both username and password can't be empty.");
            return EMPTY_USERNAME_PASSWORD;
        } else if (username.isEmpty()) {
            System.out.println("[Database] Username can't be empty.");
            return EMPTY_USERNAME;
        } else if (password.isEmpty()) {
            System.out.println("[Database] Password can't be empty.");
            return EMPTY_PASSWORD;
        } else if (username.length() > 20) {
            System.out.println("[Database] Username can't exceed 20 characters.");
            return TOO_LONG_USERNAME;
        } else if (password.length() > 25) {
            System.out.println("[Database] Password can't exceed 25 characters.");
            return TOO_LONG_PASSWORD;
        }

        return NO_ERROR;
    }

    private boolean isUsernameAvailable(String username) {
        try (PreparedStatement statement = connection.prepareStatement(
            "SELECT 1 FROM `" + DATABASE_NAME + "`.`tblusers` WHERE username=?"
        )) {
            statement.setString(1, username);
            ResultSet usersWithMatchingUsername = statement.executeQuery();
            return !usersWithMatchingUsername.next();
        } catch (SQLException e) {
            System.out.println("[Database] Username check error: " + e.getMessage());
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

    private void createDatabase() {
        try(Connection initializerConnection = DriverManager.getConnection(LOCALHOST, "root", "")) {
            Class.forName(JDBC_DRIVER);
            
            try {
                PreparedStatement checkDb = initializerConnection.prepareStatement(
                    "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ?"
                );
                checkDb.setString(1, DATABASE_NAME);
                ResultSet rs = checkDb.executeQuery();
                
                if (!rs.next()) {
                    System.out.println("[Database] Creating database...");
                    PreparedStatement createDatabase = initializerConnection.prepareStatement(
                        "CREATE DATABASE " + DATABASE_NAME + ";"
                    );
                    createDatabase.execute();
                    System.out.println("[Database] Database created successfully.");
                } else {
                    System.out.println("[Database] Database already exists.");
                }
            } catch (SQLException e) {
                System.out.println("[Database] Error checking/creating database: " + e.getMessage());
                throw e;
            }

            establishDatabaseConnection();

            createUserTable();
            importLessonsTable();
            
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("[Database] Database initialization error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    private void createUserTable() {
        try {
            String currentCatalog = connection.getCatalog();
            System.out.println("[Database] Creating user table in database: " + currentCatalog);

            boolean tableExists = false;
            DatabaseMetaData dbm = connection.getMetaData();

            try (ResultSet tables = dbm.getTables(currentCatalog, null, "%", new String[] {"TABLE"})) {
                System.out.println("[Database] Tables in " + currentCatalog + ":");
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    String tableCatalog = tables.getString("TABLE_CAT");
                    System.out.println("[Database] Found table: " + tableName + " in " + tableCatalog);
                    if (tableName.equalsIgnoreCase("tblusers")) {
                        tableExists = true;
                    }
                }
            }
            
            if (tableExists) {
                System.out.println("[Database] User table already exists in " + currentCatalog);

                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery("DESCRIBE `tblusers`")) {
                    System.out.println("[Database] Verifying tblusers structure:");
                    while (rs.next()) {
                        System.out.println("[Database] Column: " + rs.getString("Field") + 
                                           ", Type: " + rs.getString("Type"));
                    }
                } catch (SQLException e) {
                    System.out.println("[Database] Error verifying table structure: " + e.getMessage());
                    tableExists = false;
                }
            }
            
            if (!tableExists) {
                System.out.println("[Database] Creating user table...");

                try (Statement dropStatement = connection.createStatement()) {
                    dropStatement.executeUpdate("DROP TABLE IF EXISTS `tblusers`");
                    System.out.println("[Database] Dropped existing tblusers table if it existed");
                } catch (SQLException e) {
                    System.out.println("[Database] Could not drop table: " + e.getMessage());
                }

                String createUserTable = 
                    "CREATE TABLE `" + DATABASE_NAME + "`.`tblusers` (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "username VARCHAR(50), " +
                    "password VARCHAR(255)" +
                    ")";
                
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate(createUserTable);
                    System.out.println("[Database] User table created successfully.");

                    String adminUsername = "admin";
                    String adminPassword = Hash.hashPassword("admin");
                    
                    String createAdmin = 
                        "INSERT INTO `" + DATABASE_NAME + "`.`tblusers` (username, password) " +
                        "VALUES (?, ?)";
                    
                    try (PreparedStatement createAdminStatement = connection.prepareStatement(createAdmin)) {
                        createAdminStatement.setString(1, adminUsername);
                        createAdminStatement.setString(2, adminPassword);
                        createAdminStatement.executeUpdate();
                        System.out.println("[Database] Admin user created successfully.");
                    }

                    try (ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM `" + DATABASE_NAME + "`.`tblusers`")) {
                        if (rs.next()) {
                            int count = rs.getInt(1);
                            System.out.println("[Database] Verified table creation, row count: " + count);
                        }
                    } catch (SQLException e) {
                        System.out.println("[Database] Error verifying table: " + e.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("[Database] Error creating user table: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void importLessonsTable() {
        try {
            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "tbllessons", null);
            
            if (tables.next()) {
                System.out.println("[Database] Lessons table already exists.");
                return;
            }
            
            String resourcePath = "/edu/citu/procrammers/eva/sql/tbllessons.sql";
            InputStream inputStream = getClass().getResourceAsStream(resourcePath);
            if (inputStream == null) {
                System.out.println("[Database] ERROR: SQL file not found at path: " + resourcePath);
                return;
            }
            
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                boolean inTableDefinition = false;

                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("--") || line.trim().isEmpty() || 
                        line.startsWith("/*!") || line.startsWith("SET") || 
                        line.startsWith("START") || line.contains("time_zone")) {
                        continue;
                    }
                    
                    if (line.contains("Database:") || line.contains("dbprojecteva")) {
                        continue;
                    }
                    
                    if (line.contains("CREATE TABLE") && line.contains("tbllessons")) {
                        inTableDefinition = true;
                        sb.append("CREATE TABLE `tbllessons` (\n");
                        continue;
                    }
                    
                    if (inTableDefinition) {
                        if (line.contains("ENGINE=")) {
                            sb.append(");\n");
                            break;
                        }
                        
                        if (!line.contains("--")) {
                            sb.append(line).append("\n");
                        }
                    }
                }
            }

            try (Statement stmt = connection.createStatement()) {
                String createTableSQL = sb.toString().trim();
                if (!createTableSQL.isEmpty()) {
                    System.out.println("[Database] Creating lessons table...");
                    stmt.execute(createTableSQL);
                    System.out.println("[Database] Lessons table created successfully.");
                } else {
                    System.out.println("[Database] ERROR: No CREATE TABLE statement found in SQL file.");
                    return;
                }
            }

            inputStream = getClass().getResourceAsStream(resourcePath);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                 Statement stmt = connection.createStatement()) {
                
                StringBuilder insertBuilder = new StringBuilder();
                boolean inInsertData = false;
                String line;
                int insertCount = 0;
                
                while ((line = reader.readLine()) != null) {
                    if (line.contains("INSERT INTO") && line.contains("tbllessons")) {
                        inInsertData = true;
                        insertBuilder.setLength(0);
                        insertBuilder.append(line);
                    } else if (inInsertData) {
                        insertBuilder.append(line);
                        
                        if (line.endsWith(");")) {
                            try {
                                String insertSQL = insertBuilder.toString();
                                stmt.execute(insertSQL);
                                insertCount++;
                                
                                if (insertCount % 2 == 0) {
                                    System.out.println("[Database] Inserted " + insertCount + " lessons...");
                                }
                                inInsertData = false;
                            } catch (SQLException e) {
                                System.out.println("[Database] Error inserting lesson: " + e.getMessage());
                            }
                        }
                    }
                }
                
                System.out.println("[Database] Successfully imported " + insertCount + " lessons.");
            }
            
        } catch (SQLException | IOException e) {
            System.out.println("[Database] Error importing lessons table: " + e.getMessage());
        }
    }

    public ArrayList<LessonContent> loadLessons() throws SQLException {
        ArrayList<LessonContent> lessonsList = new ArrayList<>();

        String query = "SELECT lesson_id, topic_title, lesson_text, code_snippet, visualizer_path FROM tbllessons";

        try (
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                int id = resultSet.getInt("lesson_id");
                String topicTitle = resultSet.getString("topic_title");
                String lessonText = resultSet.getString("lesson_text");
                String codeSnippet = resultSet.getString("code_snippet");
                String visualizerPath = resultSet.getString("visualizer_path");

                LessonContent lesson = new LessonContent(id, topicTitle, lessonText, codeSnippet, visualizerPath);
                lessonsList.add(lesson);
            }
        }

        return lessonsList;
    }

    private void fixDatabase() {
        /**
         * TODO: Fix the entire database structure if the user's current database structure does not match
         *  the correct database structure.
         */
    }

    private void establishDatabaseConnection() {
        try {
            connection = DriverManager.getConnection(
                DATABASE_URL,
                DB_USERNAME,
                DB_PASSWORD
            );

            String currentDb = connection.getCatalog();
            System.out.println("[Database] Connected to database: " + currentDb);
            
            if (!DATABASE_NAME.equalsIgnoreCase(currentDb)) {
                System.out.println("[Database] WARNING: Connected to wrong database. Expected: " + DATABASE_NAME + ", Got: " + currentDb);
            }
            
            System.out.println("[Database] Connected to database successfully.");
        } catch (SQLException e) {
            System.out.println("[Database] Error connecting to database: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
