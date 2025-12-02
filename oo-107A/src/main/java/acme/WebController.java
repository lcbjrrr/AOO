package acme;



import acme.AppFacade;
import acme.author.business.Author;
import acme.paper.business.Paper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
//@RequestMapping("/")
public class WebController {
    private final AppFacade appFacade;

    @Autowired
    public WebController(AppFacade appFacade) {
        this.appFacade = appFacade;
    }

    @GetMapping("/")
    public String home() {
        // Return the name of the Thymeleaf template, placed in templates/home.html
        return "index";
    }
    /**
     * Screen 1: Lists all authors.
     * Maps to /authors/list
     */
    @GetMapping("/authors/list")
    public String listAuthors(Model model) {
        List<Author> authors = appFacade.getOrderedAuthors();
        model.addAttribute("authors", authors);
        model.addAttribute("count", appFacade.countAuthors());
        return "author/list";
    }

    /**
     * Screen 3: Shows a form to insert a new author.
     * Maps to /authors/create (GET)
     */
    @GetMapping("/authors/create")
    public String createAuthorForm(Model model) {
        model.addAttribute("author", new Author());
        return "author/create";
    }

    /**
     * Handles the submission for inserting a new author.
     * Maps to /authors/create (POST)
     */
    @PostMapping("/authors/create")
    public String createAuthor(Author author, RedirectAttributes redirectAttributes) {
        try {
            appFacade.addAuthor(author);
            redirectAttributes.addFlashAttribute("message", "Author '" + author.getName() + "' created successfully!");
            return "redirect:/authors/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating author: " + e.getMessage());
            return "redirect:/authors/create";
        }
    }

    /**
     * Screen 2: Shows a form to update an author's details.
     * Maps to /authors/update/{authorId} (GET)
     */
    @GetMapping("/authors/update/{authorId}")
    public String updateAuthorForm(@PathVariable int authorId, Model model) {
        Author author = appFacade.getAuthor(authorId);
        if (author == null) {
            return "redirect:/authors/list";
        }
        model.addAttribute("author", author);
        return "author/update";
    }

    /**
     * Handles the submission for updating an author.
     * Maps to /authors/update (POST)
     */
    @PostMapping("/authors/update")
    public String updateAuthor(Author author, RedirectAttributes redirectAttributes) {
        try {
            appFacade.updateAuthor(author);
            redirectAttributes.addFlashAttribute("message", "Author '" + author.getName() + "' updated successfully!");
            return "redirect:/authors/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating author: " + e.getMessage());
            return "redirect:/authors/update/" + author.getAuthorId();
        }
    }

    /**
     * Handles the request to delete an author.
     * Maps to /authors/delete/{authorId}
     */
    @GetMapping("/authors/delete/{authorId}")
    public String deleteAuthor(@PathVariable int authorId, RedirectAttributes redirectAttributes) {
        Author author = appFacade.getAuthor(authorId);
        if (author != null) {
            appFacade.deleteAuthor(authorId);
            redirectAttributes.addFlashAttribute("message", "Author '" + author.getName() + "' deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Author ID " + authorId + " not found for deletion.");
        }
        return "redirect:/authors/list";
    }
// package acme.webb;
// ... (imports and class definition remain the same)

// (All Author methods remain the same)

// ==========================================================
// --- NEW PAPER CRUD METHODS (ADJUSTED) ---
// ==========================================================

    /**
     * Screen 1: Lists all papers.
     * Maps to /papers/list
     */
    @GetMapping("/papers/list")
    public String listPapers(Model model) {
        List<Paper> papers = appFacade.getOrderedPapers();
        model.addAttribute("papers", papers);
        model.addAttribute("count", appFacade.countPapers());
        return "paper/list"; // Corresponds to src/main/resources/templates/paper/list.html
    }

    /**
     * Screen 3: Shows a form to insert a new paper.
     * Maps to /papers/create (GET)
     */
    @GetMapping("/papers/create")
    public String createPaperForm(Model model) {
        // Provide an empty Paper object for the form to bind to
        model.addAttribute("paper", new Paper());
        return "paper/create"; // Corresponds to src/main/resources/templates/paper/create.html
    }

    /**
     * Handles the submission for inserting a new paper.
     * Maps to /papers/create (POST)
     * NOTE: Spring MVC automatically binds form fields to the Paper object properties.
     */
    @PostMapping("/papers/create")
    public String createPaper(Paper paper, RedirectAttributes redirectAttributes) {
        try {
            appFacade.addPaper(paper);
            redirectAttributes.addFlashAttribute("message", "Paper '" + paper.getTitle() + "' created successfully!");
            return "redirect:/papers/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating paper: " + e.getMessage());
            // Flash the paper object back so user doesn't lose input
            redirectAttributes.addFlashAttribute("paper", paper);
            return "redirect:/papers/create";
        }
    }

    /**
     * Screen 2: Shows a form to update/delete a paper's details.
     * Maps to /papers/update/{paperId} (GET)
     */
    @GetMapping("/papers/update/{paperId}")
    public String updatePaperForm(@PathVariable int paperId, Model model) {
        Paper paper = appFacade.getPaper(paperId);
        if (paper == null) {
            return "redirect:/papers/list";
        }
        model.addAttribute("paper", paper);
        return "paper/update"; // Corresponds to src/main/resources/templates/paper/update.html
    }

    /**
     * Handles the submission for updating a paper.
     * Maps to /papers/update (POST)
     */
    @PostMapping("/papers/update")
    public String updatePaper(Paper paper, RedirectAttributes redirectAttributes) {
        try {
            appFacade.updatePaper(paper);
            redirectAttributes.addFlashAttribute("message", "Paper '" + paper.getTitle() + "' updated successfully!");
            return "redirect:/papers/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating paper: " + e.getMessage());
            return "redirect:/papers/update/" + paper.getPaperId();
        }
    }

    /**
     * Handles the request to delete a paper.
     * Maps to /papers/delete/{paperId}
     */
    @GetMapping("/papers/delete/{paperId}")
    public String deletePaper(@PathVariable int paperId, RedirectAttributes redirectAttributes) {
        Paper paper = appFacade.getPaper(paperId);
        if (paper != null) {
            appFacade.deletePaper(paperId);
            redirectAttributes.addFlashAttribute("message", "Paper '" + paper.getTitle() + "' deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Paper ID " + paperId + " not found for deletion.");
        }
        return "redirect:/papers/list";
    }
}


