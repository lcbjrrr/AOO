package acme.paper.business;

import java.util.List;

public interface IPaperRepository {
    public void save(Paper paper);
    public void update(Paper paper);
    public void deleteById(int paperId);
    public Paper findById(int paperId);
    public long count();
    public List<Paper> findAllByOrderByTitle();
    public Paper findByEntryId(String entryId);
}