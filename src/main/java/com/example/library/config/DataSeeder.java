package com.example.library.config;

import com.example.library.model.Author;
import com.example.library.model.Book;
import com.example.library.model.Member;
import com.example.library.repository.AuthorRepository;
import com.example.library.repository.BookRepository;
import com.example.library.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Inserts sample data the first time the app runs (when the tables are empty). */
@Component
public class DataSeeder implements CommandLineRunner {

    private final BookRepository books;
    private final AuthorRepository authors;
    private final MemberRepository members;

    public DataSeeder(BookRepository books, AuthorRepository authors, MemberRepository members) {
        this.books = books;
        this.authors = authors;
        this.members = members;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (books.count() > 0) return;

        Author bloch = authors.save(new Author("Joshua Bloch"));
        Author martin = authors.save(new Author("Robert C. Martin"));
        Author sierra = authors.save(new Author("Kathy Sierra"));
        Author bates = authors.save(new Author("Bert Bates"));
        Author walls = authors.save(new Author("Craig Walls"));
        Author bauer = authors.save(new Author("Christian Bauer"));
        Author king = authors.save(new Author("Gavin King"));
        Author fowler = authors.save(new Author("Martin Fowler"));

        add("Effective Java", "9780134685991", "Java", 3, bloch);
        add("Clean Code", "9780132350884", "Software Engineering", 2, martin);
        add("Head First Java", "9781491910771", "Java", 4, sierra, bates);
        add("Spring in Action", "9781617297571", "Spring", 2, walls);
        add("Java Persistence with Hibernate", "9781932394887", "Hibernate", 2, bauer, king);
        add("Refactoring", "9780134757599", "Software Engineering", 1, fowler);

        for (String[] m : new String[][]{
                {"Ravi Kumar", "ravi@example.com"},
                {"Sneha Reddy", "sneha@example.com"},
                {"Anil Varma", "anil@example.com"}}) {
            Member member = new Member();
            member.setName(m[0]);
            member.setEmail(m[1]);
            members.save(member);
        }
    }

    private void add(String title, String isbn, String category, int copies, Author... as) {
        Book b = new Book();
        b.setTitle(title);
        b.setIsbn(isbn);
        b.setCategory(category);
        b.setTotalCopies(copies);
        b.setAvailableCopies(copies);
        b.getAuthors().addAll(java.util.List.of(as));
        books.save(b);
    }
}
