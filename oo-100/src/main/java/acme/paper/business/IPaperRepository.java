package acme.paper.business;


import java.util.List;

public interface IPaperRepository {
    public void save(Paper paper);
    public Paper findById(int paperId);
    public void deleteById(int paperId);
    public long count();
    public List<Paper> findAllByOrderByTitle();
    public Paper findByEntryId(String entryId); // Added for uniqueness check
}
