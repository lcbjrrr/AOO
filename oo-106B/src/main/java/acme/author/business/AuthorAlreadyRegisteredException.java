package acme.author.business;


public class AuthorAlreadyRegisteredException extends Exception {
    private Author author;

    public AuthorAlreadyRegisteredException(Author author) {
        super("Author with email " + author.getEmail() + " is already registered.");
        this.author = author;
    }
}