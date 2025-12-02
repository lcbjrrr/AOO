package acme.author.data;

import acme.author.business.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepositoryJPA extends JpaRepository<Author, Integer> { // and IStudentRepository{
    // Spring Data JPA automatically provides:
    // save(Student student)
    // update: uses save with a not null id
    // deleteById(Integer id)
    // findById(Integer id)
    // findAll()
    // count()
    Author findByEmail(String email); // JPA will create those
    List<Author> findAllByOrderByName();
}




