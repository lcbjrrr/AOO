package acme;

import acme.author.business.*;
import acme.author.data.AuthorRepositoryJPA;
import acme.integration.EmailIntegration;
import acme.integration.ILLMIntegration;
import acme.paper.business.*;
import acme.paper.data.PaperRepositoryJPA;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
@Service
public class AppFacade {

    private final AuthorManager authorManager;
    private final PaperManager paperManager;
    private ILLMIntegration llmIntegration;
    private EmailIntegration emailIntegration;


    /**
     * Constructor for the AppFacade.
     * It requires initialized Manager instances (which typically hold Repository instances).
     */
    public AppFacade(AuthorRepositoryJPA authorRepo, PaperRepositoryJPA paperRepo, ILLMIntegration llmIntegration, EmailIntegration emailIntegration) {
        this.authorManager = new AuthorManager(authorRepo);
        this.paperManager = new PaperManager(paperRepo);
        this.llmIntegration = llmIntegration;
        this.emailIntegration = emailIntegration;
    }

    // --- AUTHOR MANAGEMENT FACADE METHODS ---

    /**
     * Adds a new Author to the system.
     * @param author The Author object to add.
     * @throws AuthorAlreadyRegisteredException if an Author with the same email already exists.
     */
    public void addAuthor(Author author) throws AuthorAlreadyRegisteredException {
        authorManager.addAuthor(author);
    }

    /**
     * Updates an existing Author's details.
     * @param author The Author object containing updated data.
     * @throws IllegalArgumentException if the ID doesn't exist or the new email is already taken.
     */
    public void updateAuthor(Author author) throws IllegalArgumentException {
        authorManager.updateAuthor(author);
    }

    /**
     * Deletes an Author from the system.
     * @param authorId The ID of the author to delete.
     */
    public void deleteAuthor(int authorId) {
        authorManager.deleteAuthor(authorId);
    }


    /**
     * Retrieves an Author by their unique database ID.
     * @param authorId The ID of the author.
     * @return The Author object, or null if not found.
     */
    public Author getAuthor(int authorId) {
        return authorManager.getAuthor(authorId);
    }

    /**
     * Retrieves a list of all Authors, ordered by name.
     * @return A List of Author objects.
     */
    public List<Author> getOrderedAuthors() {
        return authorManager.getOrderedAuthors();
    }

    /**
     * Gets the total count of registered Authors.
     * @return The total number of authors.
     */
    public long countAuthors() {
        return authorManager.countAuthors();
    }

    /**
     * Checks if an Author is already registered via their email.
     * @param authorEmail The email to check for uniqueness.
     * @return true if an author is registered with that email, false otherwise.
     */
    public boolean isAuthorAlreadyRegistered(String authorEmail) {
        return authorManager.isAlreadyRegistered(authorEmail);
    }

    // --- PAPER MANAGEMENT FACADE METHODS ---

    /**
     * Adds a new Paper to the system.
     * @param paper The Paper object to add.
     * @throws PaperAlreadyExistsException if a Paper with the same entryId already exists.
     */
    public void addPaper(Paper paper) throws PaperAlreadyExistsException {
        paperManager.addPaper(paper);
    }

    /**
     * Updates an existing Paper's details.
     * @param paper The Paper object containing updated data.
     * @throws IllegalArgumentException if the ID doesn't exist or the new entryId is already taken.
     */
    public void updatePaper(Paper paper) throws IllegalArgumentException {
        paperManager.updatePaper(paper);
    }

    /**
     * Deletes a Paper from the system.
     * @param paperId The ID of the paper to delete.
     */
    public void deletePaper(int paperId) {
        paperManager.deletePaper(paperId);
    }

    /**
     * Retrieves a Paper by its unique database ID.
     * @param paperId The ID of the paper.
     * @return The Paper object, or null if not found.
     */
    public Paper getPaper(int paperId) {
        Paper p= paperManager.getPaper(paperId);
        System.out.println("------ID: " + p.getTitle() + ":"+ p.getAuthors().size());
        return paperManager.getPaper(paperId);
    }

    /**
     * Retrieves a list of all Papers, ordered by title.
     * @return A List of Paper objects.
     */
    public List<Paper> getOrderedPapers() {
        return paperManager.getOrderedPapers();
    }

    /**
     * Gets the total count of Papers in the system.
     * @return The total number of papers.
     */
    public long countPapers() {
        return paperManager.countPapers();
    }

    /**
     * Checks if a Paper is already existing via its entry ID.
     * @param entryId The unique entry ID to check.
     * @return true if a paper exists with that entry ID, false otherwise.
     */
    public boolean isPaperAlreadyExisting(String entryId) {
        return paperManager.isAlreadyExisting(entryId);
    }

    public void extractKeywordsFromAbstracts() {
        List<Paper> papers = paperManager.getOrderedPapers();
        for (Paper p: papers){
            try {
                String prompt = "Extract 3 to 5 keywords. Return only those separated by comma " + p.getAbstractText();
                String msg = p.getPaperId()+","+ prompt;
                String ok = llmIntegration.askAquestion(msg);
                System.out.println("---->"+ok);
            }catch (Exception e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
//        List<Paper> papers = paperManager.getOrderedPapers();
//        for (Paper p: papers){
//            String prompt = "Extract 3 to 5 keywords. Return only those separated by comma " + p.getAbstractText();
//            try {
//                String keys = llmIntegration.askAquestion(prompt);
//                p.setKeywords(keys);
//                System.out.println("======>"+keys);
//                paperManager.updatePaper(p);
//            }catch (Exception e){System.out.println(e.getMessage());}
//        }
    }
    public String downloadPaper(int paperId) {
        Paper paper = paperManager.getPaper(paperId);
        String path = paper.getEntryId();
        String url = "https://arxiv.org/pdf/{{FILE}}";
        String filename = path.substring(path.lastIndexOf('/') + 1);
        try {
            emailIntegration.downloadPDF(paper.getEntryId());
        }catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return url.replace("{{FILE}}", filename);
    }

    public void linkPaperToAuthor(int authorId, int paperId) throws IllegalArgumentException {
        Author author = this.authorManager.getAuthor(authorId);
        if (author==null) {throw new IllegalArgumentException("Author with ID " + authorId + " not found.");}
        Paper paper = this.paperManager.getPaper(paperId);
        if (paper==null) {throw new IllegalArgumentException("Paper with ID " + paperId + " not found.");}
        author.addPaper(paper);
        this.authorManager.updateAuthor(author);
    }

    public void unlinkPaperFromAuthor(int authorId, int paperId) throws IllegalArgumentException {
        Author author = this.authorManager.getAuthor(authorId);
        if (author==null) {throw new IllegalArgumentException("Author with ID " + authorId + " not found.");}
        Paper paper = this.paperManager.getPaper(paperId);
        if (paper == null) {throw new IllegalArgumentException("Paper with ID " + paperId + " not found.");}
        author.removePaper(paper);
        this.authorManager.updateAuthor(author);
    }

    public void linkAuthorToPaper(int paperId, int authorId) throws IllegalArgumentException {
        Paper paper = this.paperManager.getPaper(paperId);
        if (paper==null) {throw new IllegalArgumentException("Paper with ID " + paperId + " not found.");}
        Author author  = this.authorManager.getAuthor(authorId);
        if (author==null) {throw new IllegalArgumentException("Author with ID " + authorId + " not found.");}
        paper.addAuthor(author);
        this.paperManager.updatePaper(paper);
    }

    public void unlinkAuthorFromPaper(int paperId, int authorId) throws IllegalArgumentException {
        Paper paper = this.paperManager.getPaper(paperId);
        if (paper==null) {throw new IllegalArgumentException("Paper with ID " + paperId + " not found.");}
        Author author = this.authorManager.getAuthor(authorId);
        if (author ==null) {throw new IllegalArgumentException("Author with ID " + authorId + " not found.");}
        paper.removeAuthor(author);
        this.paperManager.updatePaper(paper);
    }


}