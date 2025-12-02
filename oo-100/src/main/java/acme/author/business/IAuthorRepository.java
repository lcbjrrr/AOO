package acme.author.business;


import java.util.List;

public interface IAuthorRepository {
    public void save(Author author);
    public Author findById(int authorId);
    public void deleteById(int authorId);
    public long count();
    public List<Author> findAllByOrderByName();
    public Author findByEmail(String email);
}
