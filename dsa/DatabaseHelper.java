import java.sql.*;
import java.util.ArrayList;
import java.util.List;
                                //jdbc
public class DatabaseHelper {
    private static final String URL = "jdbc:mysql://localhost:3306/ContactBook";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); //driver load
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD); //connect the driver to user sql query
    }

    public static List<String[]> getAllContacts() {
        List<String[]> contacts = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM contacts ORDER BY name")) {

            while (rs.next()) {
                contacts.add(new String[]{
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("email")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contacts;
    }

    public static boolean addContact(String name, String phone, String email) {
        String query = "INSERT INTO contacts (name, phone, email) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.setString(3, email);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateContact(String name, String phone, String email) {
        String query = "UPDATE contacts SET phone = ?, email = ? WHERE name = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, phone);
            pstmt.setString(2, email);
            pstmt.setString(3, name);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteContact(String name) {
        String query = "DELETE FROM contacts WHERE name = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, name);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
