package acme;

import acme.author.business.Author;
import acme.author.business.AuthorAlreadyRegisteredException;
import acme.paper.business.Paper;
import acme.paper.business.PaperAlreadyExistsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
        import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/") // Base mapping for simplicity
public class WebController {

    private final AppFacade appFacade;

    public WebController(AppFacade appFacade) {
        this.appFacade = appFacade;
    }

    // --- HOME ---
    @GetMapping("/")
    public String home() {
        return "index"; // Redirect to the primary entity list
    }

    // =================================================================
    //                           AUTHOR MANAGEMENT
    // =================================================================

    // --- LISTING ---
    @GetMapping("/authors")
    public String listAuthors(Model model) {
        List<Author> authors = appFacade.getOrderedAuthors();
        model.addAttribute("authors", authors);
        return "authors/list";
    }

    // --- CREATE (GET) ---
    @GetMapping("/authors/create")
    public String createAuthorForm(Model model) {
        model.addAttribute("author", new Author());
        model.addAttribute("pageTitle", "Create New Author");
        return "authors/create";
    }

    // --- CREATE (POST) ---
    @PostMapping("/authors")
    public String saveAuthor(@ModelAttribute Author author, RedirectAttributes redirectAttributes) {
        try {
            appFacade.addAuthor(author);
            redirectAttributes.addFlashAttribute("message", "Author '" + author.getName() + "' created successfully!");
        } catch (AuthorAlreadyRegisteredException e) {
            redirectAttributes.addFlashAttribute("error", "Error: Author with that email already exists!");
            return "redirect:/authors/create";
        }
        return "redirect:/authors";
    }


//    // --- UPDATE (POST) ---
//    @PostMapping("/authors/update")
//    public String updateAuthor(@ModelAttribute Author author, RedirectAttributes redirectAttributes) {
//        try {
//            appFacade.updateAuthor(author);
//            redirectAttributes.addFlashAttribute("message", "Author '" + author.getName() + "' updated successfully!");
//        } catch (IllegalArgumentException e) {
//            redirectAttributes.addFlashAttribute("error", "Error updating author: " + e.getMessage());
//            return "redirect:/authors/" + author.getAuthorId() + "/edit";
//        }
//        return "redirect:/authors";
//    }

    // --- DELETE (POST) ---
    @PostMapping("/authors/{id}/delete")
    public String deleteAuthor(@PathVariable("id") int authorId, RedirectAttributes redirectAttributes) {
        appFacade.deleteAuthor(authorId);
        redirectAttributes.addFlashAttribute("message", "Author ID " + authorId + " deleted successfully!");
        return "redirect:/authors";
    }

    // =================================================================
    //                           PAPER MANAGEMENT
    // =================================================================

    // --- LISTING ---
    @GetMapping("/papers")
    public String listPapers(Model model) {
        List<Paper> papers = appFacade.getOrderedPapers();
        model.addAttribute("papers", papers);
        return "papers/list";
    }

    // --- CREATE (GET) ---
    @GetMapping("/papers/create")
    public String createPaperForm(Model model) {
        model.addAttribute("paper", new Paper());
        model.addAttribute("pageTitle", "Create New Paper");
        return "papers/create";
    }

    // --- CREATE (POST) ---
    @PostMapping("/papers")
    public String savePaper(@ModelAttribute Paper paper, RedirectAttributes redirectAttributes) {
        try {
            appFacade.addPaper(paper);
            redirectAttributes.addFlashAttribute("message", "Paper '" + paper.getTitle() + "' created successfully!");
        } catch (PaperAlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("error", "Error: Paper with that entry ID already exists!");
            return "redirect:/papers/create";
        }
        return "redirect:/papers";
    }

//    // --- UPDATE (GET) ---
//    @GetMapping("/papers/{id}/edit")
//    public String editPaperForm(@PathVariable("id") int paperId, Model model, RedirectAttributes redirectAttributes) {
//        Paper paper = appFacade.getPaper(paperId);
//        if (paper == null) {
//            redirectAttributes.addFlashAttribute("error", "Paper ID " + paperId + " not found!");
//            return "redirect:/papers";
//        }
//        model.addAttribute("paper", paper);
//        model.addAttribute("pageTitle", "Edit Paper (ID: " + paperId + ")");
//        return "papers/update";
//    }

//    // --- UPDATE (POST) ---
//    @PostMapping("/papers/update")
//    public String updatePaper(@ModelAttribute Paper paper, RedirectAttributes redirectAttributes) {
//        try {
//            appFacade.updatePaper(paper);
//            redirectAttributes.addFlashAttribute("message", "Paper '" + paper.getTitle() + "' updated successfully!");
//        } catch (IllegalArgumentException e) {
//            redirectAttributes.addFlashAttribute("error", "Error updating paper: " + e.getMessage());
//            return "redirect:/papers/" + paper.getPaperId() + "/edit";
//        }
//        return "redirect:/papers";
//    }

    // --- DELETE (POST) ---
    @PostMapping("/papers/{id}/delete")
    public String deletePaper(@PathVariable("id") int paperId, RedirectAttributes redirectAttributes) {
        appFacade.deletePaper(paperId);
        redirectAttributes.addFlashAttribute("message", "Paper ID " + paperId + " deleted successfully!");
        return "redirect:/papers";
    }


    //////////////////////////


        // --- AUTHOR UPDATE (GET) ---
        @GetMapping("/authors/{id}/edit")
        public String editAuthorForm(@PathVariable("id") int authorId, Model model, RedirectAttributes redirectAttributes) {
            // Fetch Author being edited
            Author author = appFacade.getAuthor(authorId);
            if (author == null) {
                redirectAttributes.addFlashAttribute("error", "Author ID " + authorId + " not found!");
                return "redirect:/authors";
            }

            // Fetch ALL papers to populate the ADD combobox
            List<Paper> allPapers = appFacade.getOrderedPapers();

            // Filter papers already linked to the author
            // NOTE: We rely on the Author object's list of papers (author.getPapers()) for the current list.
            List<Integer> linkedPaperIds = author.getPapers().stream().map(Paper::getPaperId).toList();

            // Filter the available papers (for the combobox)
            List<Paper> availablePapers = allPapers.stream()
                    .filter(p -> !linkedPaperIds.contains(p.getPaperId()))
                    .toList();

            model.addAttribute("author", author);
            model.addAttribute("availablePapers", availablePapers); // <-- UPDATED for combobox
            model.addAttribute("pageTitle", "Edit Author (ID: " + authorId + ")");
            return "authors/update";
        }

        // --- AUTHOR ADD PAPER (NEW POST) ---
        @PostMapping("/authors/addPaper")
        public String addPaperToAuthor(@RequestParam("authorId") int authorId, @RequestParam("paperToAddId") int paperId, RedirectAttributes redirectAttributes) {
            try {
                appFacade.linkPaperToAuthor(authorId, paperId);
                redirectAttributes.addFlashAttribute("message", "Paper added successfully!");
            } catch (IllegalArgumentException e) {
                redirectAttributes.addFlashAttribute("error", "Error adding paper: " + e.getMessage());
            }
            return "redirect:/authors/" + authorId + "/edit";
        }

        // --- AUTHOR REMOVE PAPER (NEW POST) ---
        @PostMapping("/authors/removePaper")
        public String removePaperFromAuthor(@RequestParam("authorId") int authorId, @RequestParam("paperToRemoveId") int paperId, RedirectAttributes redirectAttributes) {
            try {
                appFacade.unlinkPaperFromAuthor(authorId, paperId);
                redirectAttributes.addFlashAttribute("message", "Paper removed successfully!");
            } catch (IllegalArgumentException e) {
                redirectAttributes.addFlashAttribute("error", "Error removing paper: " + e.getMessage());
            }
            return "redirect:/authors/" + authorId + "/edit";
        }

        // --- AUTHOR UPDATE PRIMARY FIELDS (Existing POST) ---
        @PostMapping("/authors/update")
        public String updateAuthor(@ModelAttribute Author author, RedirectAttributes redirectAttributes) {
            try {
                // Logic for updating Name/Email only
                Author existingAuthor = appFacade.getAuthor(author.getAuthorId());
                existingAuthor.setName(author.getName());
                existingAuthor.setEmail(author.getEmail());
                appFacade.updateAuthor(existingAuthor);

                redirectAttributes.addFlashAttribute("message", "Author details updated successfully!");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error updating author details: " + e.getMessage());
                return "redirect:/authors/" + author.getAuthorId() + "/edit";
            }
            return "redirect:/authors/" + author.getAuthorId() + "/edit"; // Stay on edit page after primary update
        }


        // =================================================================
        //                           PAPER MANAGEMENT
        // =================================================================

        // --- PAPER UPDATE (GET) ---
        @GetMapping("/papers/{id}/edit")
        public String editPaperForm(@PathVariable("id") int paperId, Model model, RedirectAttributes redirectAttributes) {
            Paper paper = appFacade.getPaper(paperId);
            if (paper == null) {
                redirectAttributes.addFlashAttribute("error", "Paper ID " + paperId + " not found!");
                return "redirect:/papers";
            }

            // Fetch ALL authors
            List<Author> allAuthors = appFacade.getOrderedAuthors();

            // Filter authors already linked to the paper
            List<Integer> linkedAuthorIds = paper.getAuthors().stream().map(Author::getAuthorId).toList();

            // Filter the available authors (for the combobox)
            List<Author> availableAuthors = allAuthors.stream()
                    .filter(a -> !linkedAuthorIds.contains(a.getAuthorId()))
                    .toList();

            model.addAttribute("paper", paper);
            model.addAttribute("availableAuthors", availableAuthors); // <-- UPDATED for combobox
            model.addAttribute("pageTitle", "Edit Paper (ID: " + paperId + ")");
            return "papers/update";
        }

        // --- PAPER ADD AUTHOR (NEW POST) ---
        @PostMapping("/papers/addAuthor")
        public String addAuthorToPaper(@RequestParam("paperId") int paperId, @RequestParam("authorToAddId") int authorId, RedirectAttributes redirectAttributes) {
            try {
                appFacade.linkAuthorToPaper(paperId, authorId);
                redirectAttributes.addFlashAttribute("message", "Author added successfully!");
            } catch (IllegalArgumentException e) {
                redirectAttributes.addFlashAttribute("error", "Error adding author: " + e.getMessage());
            }
            return "redirect:/papers/" + paperId + "/edit";
        }

        // --- PAPER REMOVE AUTHOR (NEW POST) ---
        @PostMapping("/papers/removeAuthor")
        public String removeAuthorFromPaper(@RequestParam("paperId") int paperId, @RequestParam("authorToRemoveId") int authorId, RedirectAttributes redirectAttributes) {
            try {
                appFacade.unlinkAuthorFromPaper(paperId, authorId);
                redirectAttributes.addFlashAttribute("message", "Author removed successfully!");
            } catch (IllegalArgumentException e) {
                redirectAttributes.addFlashAttribute("error", "Error removing author: " + e.getMessage());
            }
            return "redirect:/papers/" + paperId + "/edit";
        }

        // --- PAPER UPDATE PRIMARY FIELDS (Existing POST) ---
        @PostMapping("/papers/update")
        public String updatePaper(@ModelAttribute Paper paper, RedirectAttributes redirectAttributes) {
            try {
                // Logic for updating Title/EntryId/Abstract only
                Paper existingPaper = appFacade.getPaper(paper.getPaperId());
                existingPaper.setTitle(paper.getTitle());
                existingPaper.setEntryId(paper.getEntryId());
                existingPaper.setAbstractText(paper.getAbstractText());
                appFacade.updatePaper(existingPaper);

                redirectAttributes.addFlashAttribute("message", "Paper details updated successfully!");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error updating paper details: " + e.getMessage());
                return "redirect:/papers/" + paper.getPaperId() + "/edit";
            }
            return "redirect:/papers/" + paper.getPaperId() + "/edit"; // Stay on edit page after primary update
        }

 }
