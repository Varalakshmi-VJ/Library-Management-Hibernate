package com.example.library.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.hibernate.annotations.BatchSize;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true, length = 20)
    private String isbn;

    private String category;

    private int totalCopies;
    private int availableCopies;

    // Optimistic locking: Hibernate adds "where version = ?" to every UPDATE.
    // If two users update the same row at once, the second one fails.
    @Version
    private Long version;

    // Owning side of the many-to-many; LAZY = authors are loaded only when used.
    // @BatchSize loads authors for up to 20 books in ONE query instead of 20 queries.
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id"))
    @BatchSize(size = 20)
    private Set<Author> authors = new HashSet<>();

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }
    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }
    public Long getVersion() { return version; }
    public Set<Author> getAuthors() { return authors; }

    public String getAuthorNames() {
        return authors.stream().map(Author::getName).sorted().collect(Collectors.joining(", "));
    }
}
