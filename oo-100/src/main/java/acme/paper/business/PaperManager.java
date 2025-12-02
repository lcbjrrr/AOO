package acme.paper.business;


import java.util.List;

public class PaperManager {
    private IPaperRepository repository;

    public PaperManager(IPaperRepository repository) {
        this.repository = repository;
    }

    public void addPaper(Paper paper) throws PaperAlreadyExistsException {
        // Check for uniqueness based on entryId, similar to StudentManager checking name
        if (!isAlreadyExisting(paper.getEntryId())) {
            repository.save(paper);
        } else {
            throw new PaperAlreadyExistsException(paper);
        }
    }

    public Paper getPaper(int paperId) {
        return repository.findById(paperId);
    }

    // Retains a calculation method, but adapted for counting papers instead of grades
    public long countPapers() {
        return repository.count();
    }

    public List<Paper> getOrderedPapers() {
        return repository.findAllByOrderByTitle();
    }

    public boolean isAlreadyExisting(String entryId) {
        Paper paper = repository.findByEntryId(entryId);
        return paper != null;
    }
}
