package acme.author.business;


import acme.paper.business.Paper;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "authors")
public class Author implements Comparable<Author> {


    @ManyToMany(mappedBy = "authors")
    private Set<Paper> papers = new LinkedHashSet<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int authorId;
    private String name;
    private String affiliation;
    private String email;

    public Author() {
    }

    public Author(String name, String affiliation, String email) {
        this.name = name;
        this.affiliation = affiliation;
        this.email = email;
    }

    public Author(int authorId, String name, String affiliation, String email) {
        this(name, affiliation, email);
        this.authorId = authorId;
    }

    public void addPaper(Paper paper) {
        this.papers.add(paper);
        paper.getAuthors().add(this);
    }
    public void removePaper(Paper paper) {
        this.papers.remove(paper);
        paper.getAuthors().remove(this);
    }
    public Set<Paper> getPapers() {
        return papers;
    }

    public void setPapers(Set<Paper> papers) {
        this.papers = papers;
    }
    // Getters
    public int getAuthorId() {
        return authorId;
    }

    public String getName() {
        return name;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public String getEmail() {
        return email;
    }

    // Setters
    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int compareTo(Author o) {
        // Sorts authors alphabetically by name
        return this.name.compareTo(o.name);
    }
}
