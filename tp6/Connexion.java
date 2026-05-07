import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connexion {
    private static String login = "root";
    private static String password = "";
    private static String url = "jdbc:mysql://localhost/demoJDBC";
    private static Connection cn;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            cn = DriverManager.getConnection(url, login, password);
            System.out.println("Connexion établie avec succès !");
        } catch (ClassNotFoundException ex) {
            System.out.println("Impossible de charger le driver");
            ex.printStackTrace();
        } catch (SQLException ex) {
            System.out.println("Erreur de connexion");
            ex.printStackTrace();
        }
    }

    public static Connection getCn() {
        return cn;
    }
}
