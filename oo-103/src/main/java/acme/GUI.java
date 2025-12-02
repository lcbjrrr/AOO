package acme;

import acme.author.data.AuthorRepositoryDB;
import acme.paper.data.PaperRepositoryDB;
import acme.author.business.*;
import acme.paper.business.*;


import java.util.List;

public class GUI {
    private AppFacade app;
    public GUI(IAuthorRepository authorRepo, IPaperRepository paperRepo) {
        this. app = new AppFacade(authorRepo, paperRepo);
    }

    void run(){

        List<Author> all = app.getOrderedAuthors();
        for (Author a : all) {
            System.out.println(a.getAuthorId()+":"+a.getName());
        }

        List<Paper> alls = app.getOrderedPapers();
        for (Paper p : alls) {
            System.out.println(p.getTitle().substring(0,20)+":"+p.getEntryId());
        }
        System.out.println("# papers: "+app.countPapers());
        System.out.println("# authors: "+app.countAuthors());
    }
}
