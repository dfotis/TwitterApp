import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by manos on 9/1/2017.
 */
public class DatabaseConnection {

    DatabaseConnection(){
        String url = "jdbc:mysql://localhost:3306/javabase";
        String username = "java";
        String password = "password";

        System.out.println("Connecting database...");

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Database connected!");
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }
    }


}
