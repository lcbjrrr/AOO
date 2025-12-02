package acme.paper.business;

import acme.author.business.Author;
import jakarta.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "papers")
public class Paper implements Comparable<Paper>{


    //PAPER
    @ManyToMany(cascade = {
            CascadeType.PERSIST, // When Paper is saved, Authors are saved if new
            CascadeType.MERGE    // When Paper is updated, Authors are merged if necessary
    })
    @JoinTable(
            name = "paper_author", // Name of the join table
            joinColumns = @JoinColumn(name = "paper_id"), // FK to the Paper table
            inverseJoinColumns = @JoinColumn(name = "author_id") // FK to the Author table
    )
    private Set<Author> authors = new LinkedHashSet<>();


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int paperId;
    private String title;
    private Date publishedDate;
    private String abstractText; // 'abstract' is a reserved keyword in Java
    private String keywords;
    private String entryId;
    private String sectionId;
    private String themeId;

    public Paper(){}

    // Constructor for creating new papers (without DB-assigned ID)
    public Paper(String title, Date publishedDate, String abstractText, String keywords, String entryId, String sectionId, String themeId) {
        this.title = title;
        this.publishedDate = publishedDate;
        this.abstractText = abstractText;
        this.keywords = keywords;
        this.entryId = entryId;
        this.sectionId = sectionId;
        this.themeId = themeId;
    }

    // Constructor for retrieving from DB (with ID)
    public Paper(int paperId, String title, Date publishedDate, String abstractText, String keywords, String entryId, String sectionId, String themeId) {
        this(title, publishedDate, abstractText, keywords, entryId, sectionId, themeId);
        this.paperId = paperId;
    }

    public void addAuthor(Author author) {
        this.authors.add(author);
        author.getPapers().add(this);
    }

    public void removeAuthor(Author author) {
        this.authors.remove(author);
        author.getPapers().remove(this);
    }
    public Set<Author> getAuthors() {
        return authors;
    }
    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }
    // --- Getters and Setters ---
    public int getPaperId() { return paperId; }
    public void setPaperId(int paperId) { this.paperId = paperId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Date getPublishedDate() { return publishedDate; }
    public void setPublishedDate(Date publishedDate) { this.publishedDate = publishedDate; }

    public String getAbstractText() { return abstractText; }
    public void setAbstractText(String abstractText) { this.abstractText = abstractText; }

    public String getKeywords() { return keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }

    public String getEntryId() { return entryId; }
    public void setEntryId(String entryId) { this.entryId = entryId; }

    public String getSectionId() { return sectionId; }
    public void setSectionId(String sectionId) { this.sectionId = sectionId; }

    public String getThemeId() { return themeId; }
    public void setThemeId(String themeId) { this.themeId = themeId; }

    @Override
    public int compareTo(Paper o) {
        // Sort by Title
        return this.title.compareTo(o.title);
    }
}
