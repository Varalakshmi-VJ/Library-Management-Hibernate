package com.example.library.service;

import com.example.library.model.Author;
import com.example.library.model.Book;
import com.example.library.repository.AuthorRepository;
import com.example.library.repository.BookRepository;
import com.example.library.repository.IssueRepository;
import java.util.Arrays;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final IssueRepository issueRepository;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository,
                       IssueRepository issueRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.issueRepository = issueRepository;
    }

    @Transactional(readOnly = true)
    public Page<Book> search(String q, int page, int size) {
        return bookRepository.search(q == null ? "" : q.trim(), PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public Book get(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Book not found"));
    }

    @Transactional(readOnly = true)
    public List<Book> availableBooks() {
        return bookRepository.findByAvailableCopiesGreaterThanOrderByTitle(0);
    }

    public boolean isbnTaken(String isbn, Long ignoreId) {
        return bookRepository.findByIsbn(isbn.trim())
                .map(b -> !b.getId().equals(ignoreId)).orElse(false);
    }

    /** Creates or updates a book. authorNames is a comma-separated list. */
    @Transactional
    public void save(Long id, String title, String isbn, String category, int totalCopies, String authorNames) {
        Book book;
        if (id == null) {
            book = new Book();
            book.setAvailableCopies(totalCopies);
        } else {
            book = get(id);
            int issued = book.getTotalCopies() - book.getAvailableCopies();
            if (totalCopies < issued) {
                throw new IllegalStateException(issued + " copies are currently issued; total cannot be less than that");
            }
            book.setAvailableCopies(totalCopies - issued);
        }
        book.setTitle(title.trim());
        book.setIsbn(isbn.trim());
        book.setCategory(category == null ? null : category.trim());
        book.setTotalCopies(totalCopies);

        book.getAuthors().clear();
        Arrays.stream(authorNames.split(","))
                .map(String::trim).filter(s -> !s.isEmpty()).distinct()
                .forEach(name -> book.getAuthors().add(
                        authorRepository.findByNameIgnoreCase(name).orElseGet(() -> authorRepository.save(new Author(name)))));
        bookRepository.save(book);
    }

    @Transactional
    public void delete(Long id) {
        if (issueRepository.existsByBookId(id)) {
            throw new IllegalStateException("This book has issue history and cannot be deleted");
        }
        bookRepository.deleteById(id);
    }
}
