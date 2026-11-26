package com.example.library.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "issues")
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private int fine;

    public Issue() {}

    public Issue(Book book, Member member, LocalDate issueDate, LocalDate dueDate) {
        this.book = book;
        this.member = member;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
    }

    public Long getId() { return id; }
    public Book getBook() { return book; }
    public Member getMember() { return member; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public int getFine() { return fine; }
    public void setFine(int fine) { this.fine = fine; }

    public boolean isOverdue() {
        return returnDate == null && LocalDate.now().isAfter(dueDate);
    }
}
