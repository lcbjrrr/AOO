package acme.paper.business;



public class PaperAlreadyExistsException extends Exception {
    private Paper paper;

    public PaperAlreadyExistsException(Paper paper) {
        super("Paper with entry_id " + paper.getEntryId() + " already exists.");
        this.paper = paper;
    }
}