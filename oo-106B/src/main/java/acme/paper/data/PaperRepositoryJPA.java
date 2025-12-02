package acme.paper.data;




import acme.paper.business.Paper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PaperRepositoryJPA extends JpaRepository<Paper, Integer> { // and IStudentRepository{
    // Spring Data JPA automatically provides:
    // save(Student student)
    // update: uses save with a not null id
    // deleteById(Integer id)
    // findById(Integer id)
    // findAll()
    // count()
    // Custom query methods (Spring Data JPA will implement these for you)
    List<Paper> findAllByOrderByTitle();
    Paper findByEntryId(String entryId);
    Paper findByTitle(String title);
}

