package acme.author.business;


public class Author implements Comparable<Author> {
    private int authorId;
    private String name;
    private String affiliation;
    private String email;
    public Author(){}
    public Author(String name, String affiliation, String email) {
        this.name = name;
        this.affiliation = affiliation;
        this.email = email;
    }

    public Author(int authorId, String name, String affiliation, String email) {
        this(name, affiliation, email);
        this.authorId = authorId;
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
