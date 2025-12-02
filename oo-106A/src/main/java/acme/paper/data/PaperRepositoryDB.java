package acme.paper.data;

import acme.paper.business.IPaperRepository;
import acme.paper.business.Paper;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
//@Repository
public class PaperRepositoryDB implements IPaperRepository {

    private static final String JDBC_DRIVER = "org.sqlite.JDBC";
    private static final String DB_URL = "jdbc:sqlite:paper.db";

    public PaperRepositoryDB() {
        try {
            Class.forName(JDBC_DRIVER);
            createPapersTable();
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found: " + e.getMessage());
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void createPapersTable() {
        // (Existing code remains the same)
        String sql = "CREATE TABLE IF NOT EXISTS Papers (" +
                "paper_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "published_date INTEGER," +
                "abstract TEXT," +
                "keywords TEXT," +
                "entry_id TEXT UNIQUE NOT NULL," +
                "section_id TEXT," +
                "theme_id TEXT" +
                ");";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating Papers table: " + e.getMessage());
        }
    }

    @Override
    public void save(Paper paper) {
        // (Existing code remains the same)
        String sql = "INSERT INTO Papers(title, published_date, abstract, keywords, entry_id, section_id, theme_id) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, paper.getTitle());
            if(paper.getPublishedDate() != null) {
                pstmt.setLong(2, paper.getPublishedDate().getTime());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setString(3, paper.getAbstractText());
            pstmt.setString(4, paper.getKeywords());
            pstmt.setString(5, paper.getEntryId());
            pstmt.setString(6, paper.getSectionId());
            pstmt.setString(7, paper.getThemeId());

            pstmt.executeUpdate();
            System.out.println("Paper added: " + paper.getTitle());
        } catch (SQLException e) {
            System.err.println("Error adding paper: " + e.getMessage());
        }
    }

    // --- NEW UPDATE OPERATION ---
    @Override
    public void update(Paper paper) {
        String sql = "UPDATE Papers SET title=?, published_date=?, abstract=?, keywords=?, entry_id=?, section_id=?, theme_id=? WHERE paper_id=?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, paper.getTitle());

            // Handle nullable date
            if(paper.getPublishedDate() != null) {
                pstmt.setLong(2, paper.getPublishedDate().getTime());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            pstmt.setString(3, paper.getAbstractText());
            pstmt.setString(4, paper.getKeywords());
            pstmt.setString(5, paper.getEntryId());
            pstmt.setString(6, paper.getSectionId());
            pstmt.setString(7, paper.getThemeId());

            // The WHERE clause parameter
            pstmt.setInt(8, paper.getPaperId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Paper updated: " + paper.getTitle());
            } else {
                System.out.println("Update failed: Paper ID not found.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating paper: " + e.getMessage());
        }
    }

    // --- NEW DELETE OPERATION ---
    @Override
    public void deleteById(int paperId) {
        String sql = "DELETE FROM Papers WHERE paper_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, paperId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Paper deleted with ID: " + paperId);
            } else {
                System.out.println("Delete failed: Paper ID not found.");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting paper: " + e.getMessage());
        }
    }

    @Override
    public Paper findById(int paperId) {
        // (Existing code remains the same)
        String sql = "SELECT * FROM Papers WHERE paper_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, paperId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRowToPaper(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting paper by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Paper findByEntryId(String entryId) {
        // (Existing code remains the same)
        String sql = "SELECT * FROM Papers WHERE entry_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, entryId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRowToPaper(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding paper by entry_id: " + e.getMessage());
        }
        return null;
    }

    @Override
    public long count() {
        // (Existing code remains the same)
        String sql = "SELECT COUNT(*) AS count FROM Papers";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting paper count: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public List<Paper> findAllByOrderByTitle() {
        // (Existing code remains the same)
        List<Paper> papers = new ArrayList<>();
        String sql = "SELECT * FROM Papers ORDER BY title ASC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                papers.add(mapRowToPaper(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all papers: " + e.getMessage());
        }
        return papers;
    }

    private Paper mapRowToPaper(ResultSet rs) throws SQLException {
        // (Existing code remains the same)
        return new Paper(
                rs.getInt("paper_id"),
                rs.getString("title"),
                new Date(rs.getLong("published_date")),
                rs.getString("abstract"),
                rs.getString("keywords"),
                rs.getString("entry_id"),
                rs.getString("section_id"),
                rs.getString("theme_id")
        );
    }
}