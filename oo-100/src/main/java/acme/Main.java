package acme;

import acme.author.data.AuthorRepositoryDB;
import acme.paper.data.PaperRepositoryDB;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args) {
        //GUI gui = new GUI(new AuthorRepositoryDB(),new PaperRepositoryDB());
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        GUI gui = context.getBean(GUI.class);
        gui.run();
    }
}
