package acme;

import acme.author.data.AuthorRepositoryDB;
import acme.paper.data.PaperRepositoryDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
@SpringBootApplication
public class Main  {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}

