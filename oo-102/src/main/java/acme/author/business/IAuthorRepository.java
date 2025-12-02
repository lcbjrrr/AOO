package acme.author.business;

import java.util.List;

public interface IAuthorRepository {


    public void save(Author author);

    // New Operations
    public void update(Author author);
    public void deleteById(int authorId);

    public Author findById(int authorId);
    public long count();
    public List<Author> findAllByOrderByName();
    public Author findByEmail(String email);
}