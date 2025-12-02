package acme.author.data;

import acme.author.business.Author;
import acme.author.business.IAuthorRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuthorRepositoryJPA extends JpaRepository<Author, Integer> { // and IStudentRepository{
    // Spring Data JPA automatically provides:
    // save(Student student)
    // update: uses save with a not null id
    // deleteById(Integer id)
    // findById(Integer id)
    // findAll()
    // count()
    // Custom query methods (Spring Data JPA will implement these for you)
    Author findByEmail(String email); // Equivalent to your findByName
    List<Author> findAllByOrderByName();


}

