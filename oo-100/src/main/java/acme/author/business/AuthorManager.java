package acme.author.business;


import java.util.List;

public class AuthorManager {
    private IAuthorRepository repository;

    public AuthorManager(IAuthorRepository repository) {
        this.repository = repository;
    }

    public void addAuthor(Author author) throws AuthorAlreadyRegisteredException {
        // Assuming email must be unique
        if (!isAlreadyRegistered(author.getEmail())) {
            repository.save(author);
        } else {
            throw new AuthorAlreadyRegisteredException(author);
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
