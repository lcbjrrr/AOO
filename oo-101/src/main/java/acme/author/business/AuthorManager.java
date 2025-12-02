package acme.author.business;

import java.util.List;

public class AuthorManager {
    private IAuthorRepository repository;

    public AuthorManager(IAuthorRepository repository) {
        this.repository = repository;
    }

    public void addAuthor(Author author) throws AuthorAlreadyRegisteredException {
        if (!isAlreadyRegistered(author.getEmail())) {
            repository.save(author);
        } else {
            throw new AuthorAlreadyRegisteredException(author);
        }
    }

    // --- NEW UPDATE OPERATION ---
    public void updateAuthor(Author author) throws IllegalArgumentException {
        // 1. Check if the author actually exists
        Author existingAuthor = repository.findById(author.getAuthorId());
        if (existingAuthor == null) {
            throw new IllegalArgumentException("Cannot update: Author with ID " + author.getAuthorId() + " does not exist.");
        }

        // 2. Check if email is changing, and if the NEW email is already taken by someone else
        if (!existingAuthor.getEmail().equals(author.getEmail())) {
            Author emailCheck = repository.findByEmail(author.getEmail());
            if (emailCheck != null) {
                throw new IllegalArgumentException("Cannot update: Email " + author.getEmail() + " is already in use by another author.");
            }
        }

        repository.update(author);
    }

    // --- NEW DELETE OPERATION ---
    public void deleteAuthor(int authorId) {
        if (repository.findById(authorId) != null) {
            repository.deleteById(authorId);
        } else {
            System.err.println("Cannot delete: Author ID " + authorId + " not found.");
        }
    }

    public Author getAuthor(int authorId) {
        return repository.findById(authorId);
    }

    public List<Author> getOrderedAuthors() {
        return repository.findAllByOrderByName();
    }

    public boolean isAlreadyRegistered(String authorEmail) {
        Author author = repository.findByEmail(authorEmail);
        return author != null;
    }

    public long countAuthors() {
        return repository.count();
    }
}