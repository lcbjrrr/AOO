package acme;


import acme.author.business.Author;
import acme.author.business.AuthorAlreadyRegisteredException;
import acme.paper.business.Paper;
import acme.paper.business.PaperAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class RESTController {

    private final AppFacade app; // Dependency to be injected

    public RESTController(AppFacade app) {
        this.app = app;
    }

    // ==========================================
    //           AUTHOR ENDPOINTS
    // ==========================================

    /**
     * POST /authors
     * Adds a new Author. Returns 201 on success, 400 if already registered.
     */
    @PostMapping("/authors")
    public ResponseEntity<?> addAuthor(@RequestBody Author author) {
        try {
            app.addAuthor(author);
            return new ResponseEntity<>(HttpStatus.CREATED); // 201 Created
        } catch (AuthorAlreadyRegisteredException e) {
            return new ResponseEntity<>("Author with email " + author.getEmail() + " is already registered.",
                    HttpStatus.BAD_REQUEST); // 400 Bad Request
        }
    }

    /**
     * PUT /authors/{authorId}
     * Updates an existing Author.
     */
    @PutMapping("/authors/{authorId}")
    public ResponseEntity<?> updateAuthor(@PathVariable int authorId, @RequestBody Author author) {
        // Ensure the ID in the path matches the ID in the body
        if (author.getAuthorId() != authorId) {
            return new ResponseEntity<>("Path ID and Body ID do not match.", HttpStatus.BAD_REQUEST);
        }

        try {
            app.updateAuthor(author);
            return new ResponseEntity<>(HttpStatus.OK); // 200 OK
        } catch (IllegalArgumentException e) {
            // Catches both "ID not found" and "Email already in use"
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * DELETE /authors/{authorId}
     * Deletes an Author.
     */
    @DeleteMapping("/authors/{authorId}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable int authorId) {
        app.deleteAuthor(authorId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
    }

    /**
     * GET /authors/{authorId}
     * Retrieves a specific Author. Returns 200 on success, 404 if not found.
     */
    @GetMapping("/authors/{authorId}")
    public ResponseEntity<Author> getAuthor(@PathVariable int authorId) {
        Author author = app.getAuthor(authorId);
        if (author != null) {
            return new ResponseEntity<>(author, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * GET /authors
     * Retrieves all ordered Authors.
     */
    @GetMapping("/authors")
    public ResponseEntity<List<Author>> getOrderedAuthors() {
        List<Author> authors = app.getOrderedAuthors();
        return new ResponseEntity<>(authors, HttpStatus.OK);
    }

    // --- Auxiliary Author Endpoints ---

    @GetMapping("/authors/count")
    public ResponseEntity<Long> countAuthors() {
        return new ResponseEntity<>(app.countAuthors(), HttpStatus.OK);
    }

    @GetMapping("/authors/is-registered")
    public ResponseEntity<Boolean> isAuthorAlreadyRegistered(@RequestParam String email) {
        boolean isRegistered = app.isAuthorAlreadyRegistered(email);
        return new ResponseEntity<>(isRegistered, HttpStatus.OK);
    }

    // ==========================================
    //           PAPER ENDPOINTS
    // ==========================================

    /**
     * POST /papers
     * Adds a new Paper. Returns 201 Created, or 400 if it exists.
     */
    @PostMapping("/papers")
    public ResponseEntity<?> addPaper(@RequestBody Paper paper) {
        try {
            app.addPaper(paper);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (PaperAlreadyExistsException e) {
            return new ResponseEntity<>("Paper with entryId " + paper.getEntryId() + " already exists.",
                    HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * PUT /papers/{paperId}
     * Updates an existing Paper.
     */
    @PutMapping("/papers/{paperId}")
    public ResponseEntity<?> updatePaper(@PathVariable int paperId, @RequestBody Paper paper) {
        // Ensure the ID in the path matches the ID in the body
        if (paper.getPaperId() != paperId) {
            return new ResponseEntity<>("Path ID and Body ID do not match.", HttpStatus.BAD_REQUEST);
        }

        try {
            app.updatePaper(paper);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // Catches both "ID not found" and "EntryID already in use"
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * DELETE /papers/{paperId}
     * Deletes a Paper.
     */
    @DeleteMapping("/papers/{paperId}")
    public ResponseEntity<Void> deletePaper(@PathVariable int paperId) {
        app.deletePaper(paperId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * GET /papers/{paperId}
     * Retrieves a specific Paper.
     */
    @GetMapping("/papers/{paperId}")
    public ResponseEntity<Paper> getPaper(@PathVariable int paperId) {
        Paper paper = app.getPaper(paperId);
        if (paper != null) {
            return new ResponseEntity<>(paper, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * GET /papers
     * Retrieves all ordered Papers.
     */
    @GetMapping("/papers")
    public ResponseEntity<List<Paper>> getOrderedPapers() {
        List<Paper> papers = app.getOrderedPapers();
        return new ResponseEntity<>(papers, HttpStatus.OK);
    }

    // --- Auxiliary Paper Endpoints ---

    @GetMapping("/papers/count")
    public ResponseEntity<Long> countPapers() {
        return new ResponseEntity<>(app.countPapers(), HttpStatus.OK);
    }

    @GetMapping("/papers/is-existing")
    public ResponseEntity<Boolean> isPaperAlreadyExisting(@RequestParam String entryId) {
        boolean isExisting = app.isPaperAlreadyExisting(entryId);
        return new ResponseEntity<>(isExisting, HttpStatus.OK);
    }

    @GetMapping("/keywords")
    public ResponseEntity<String> extractKeywordsFromAbstracts() {
        app.extractKeywordsFromAbstracts();
        return new ResponseEntity<>("DONE!", HttpStatus.OK);
    }
    @GetMapping("/pdf/{paperId}")
    public ResponseEntity<String> downloadPaper(@PathVariable int paperId) {
        String pdf = app.downloadPaper(paperId);
        return new ResponseEntity<>("Check your email for PDF "+pdf, HttpStatus.OK);
    }

    @PutMapping("/authors/{authorId}/papers/{paperId}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204 No Content is appropriate for a successful update with no body
    public void linkPaperToAuthorEndpoint(@PathVariable int authorId, @PathVariable int paperId) {
        app.linkPaperToAuthor(authorId, paperId);
    }

    @DeleteMapping("/authors/{authorId}/papers/{paperId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unlinkPaperFromAuthorEndpoint(@PathVariable int authorId, @PathVariable int paperId) {
        app.unlinkPaperFromAuthor(authorId, paperId);
    }

    @PutMapping("/papers/{paperId}/authors/{authorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void linkAuthorToPaperEndpoint(@PathVariable int paperId, @PathVariable int authorId) {
        app.linkAuthorToPaper(paperId, authorId);
    }

    @DeleteMapping("/papers/{paperId}/authors/{authorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unlinkAuthorFromPaperEndpoint(@PathVariable int paperId, @PathVariable int authorId) {
        app.unlinkAuthorFromPaper(paperId, authorId);
    }



}