package acme.paper.business;

import acme.paper.data.PaperRepositoryJPA;

import java.util.List;

public class PaperManager {
    //private IPaperRepository repository;
    private PaperRepositoryJPA repository;
    public PaperManager(PaperRepositoryJPA repository) {
        this.repository = repository;
    }

    public void addPaper(Paper paper) throws PaperAlreadyExistsException {
        if (!isAlreadyExisting(paper.getEntryId())) {
            repository.save(paper);
        } else {
            throw new PaperAlreadyExistsException(paper);
        }
    }

    // --- NEW UPDATE OPERATION ---
    public void updatePaper(Paper paper) throws IllegalArgumentException {
        // 1. Check if the paper actually exists
        Paper existingPaper = repository.findById(paper.getPaperId()).get();
        if (existingPaper == null) {
            throw new IllegalArgumentException("Cannot update: Paper with ID " + paper.getPaperId() + " does not exist.");
        }

        // 2. Check if entry_id is changing, and if the NEW entry_id is already taken
        if (!existingPaper.getEntryId().equals(paper.getEntryId())) {
            Paper collisionCheck = repository.findByEntryId(paper.getEntryId());
            if (collisionCheck != null) {
                throw new IllegalArgumentException("Cannot update: Entry ID " + paper.getEntryId() + " is already in use by another paper.");
            }
        }
        repository.save(paper);
        //repository.update(paper);
    }

    // --- NEW DELETE OPERATION ---
    public void deletePaper(int paperId) {
        if (repository.findById(paperId) != null) {
            repository.deleteById(paperId);
        } else {
            System.err.println("Cannot delete: Paper ID " + paperId + " not found.");
        }
    }

    public Paper getPaper(int paperId) {
        return repository.findById(paperId).get();
    }

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