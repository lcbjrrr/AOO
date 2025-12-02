package acme.author.data;

import acme.author.business.Author;
import acme.author.business.IAuthorRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
@Repository
public class AuthorRepositoryDB implements IAuthorRepository {

    private static final String JDBC_DRIVER = "org.sqlite.JDBC";
    private static final String DB_URL = "jdbc:sqlite:paper.db";

    public AuthorRepositoryDB() {
        try {
            Class.forName(JDBC_DRIVER);
            createAuthorsTable();
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found: " + e.getMessage());
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void createAuthorsTable() {
        // (Existing code remains the same)
        String sql = "CREATE TABLE IF NOT EXISTS Authors (" +
                "author_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "affiliation TEXT," +
                "email TEXT UNIQUE NOT NULL" +
                ");";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating Authors table: " + e.getMessage());
        }
    }

    private Author mapRowToAuthor(ResultSet rs) throws SQLException {
        // (Existing code remains the same)
        return new Author(
                rs.getInt("author_id"),
                rs.getString("name"),
                rs.getString("affiliation"),
                rs.getString("email")
        );
    }

    @Override
    public void save(Author author) {
        // (Existing code remains the same)
        String sql = "INSERT INTO Authors(name, affiliation, email) VALUES(?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, author.getName());
            pstmt.setString(2, author.getAffiliation());
            pstmt.setString(3, author.getEmail());
            pstmt.executeUpdate();
            System.out.println("Author added: " + author.getName());
        } catch (SQLException e) {
            System.err.println("Error adding author: " + e.getMessage());
        }
    }

    // --- NEW UPDATE OPERATION ---
    @Override
    public void update(Author author) {
        String sql = "UPDATE Authors SET name = ?, affiliation = ?, email = ? WHERE author_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, author.getName());
            pstmt.setString(2, author.getAffiliation());
            pstmt.setString(3, author.getEmail());
            pstmt.setInt(4, author.getAuthorId()); // Use ID to find the record to update

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Author updated: " + author.getName());
            } else {
                System.out.println("Author update failed: ID not found.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating author: " + e.getMessage());
        }
    }

    // --- NEW DELETE OPERATION ---
    @Override
    public void deleteById(int authorId) {
        String sql = "DELETE FROM Authors WHERE author_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, authorId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Author deleted with ID: " + authorId);
            } else {
                System.out.println("Delete failed: Author ID not found.");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting author: " + e.getMessage());
        }
    }

    @Override
    public Author findById(int authorId) {
        // (Existing code remains the same)
        String sql = "SELECT * FROM Authors WHERE author_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, authorId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRowToAuthor(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting author by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Author findByEmail(String email) {
        // (Existing code remains the same)
        String sql = "SELECT * FROM Authors WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRowToAuthor(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding author by email: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Author> findAllByOrderByName() {
        // (Existing code remains the same)
        List<Author> authors = new ArrayList<>();
        String sql = "SELECT * FROM Authors ORDER BY name ASC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                authors.add(mapRowToAuthor(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all authors: " + e.getMessage());
        }
        return authors;
    }

    @Override
    public long count() {
        // (Existing code remains the same)
        String sql = "SELECT COUNT(*) AS count FROM Authors";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting author count: " + e.getMessage());
        }
        return 0;
    }
}