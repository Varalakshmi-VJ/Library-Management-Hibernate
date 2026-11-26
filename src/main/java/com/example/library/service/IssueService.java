package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.model.Issue;
import com.example.library.model.Member;
import com.example.library.repository.BookRepository;
import com.example.library.repository.IssueRepository;
import com.example.library.repository.MemberRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IssueService {

    public static final int LOAN_DAYS = 14;
    public static final int FINE_PER_DAY = 5;
    public static final int MAX_ACTIVE_ISSUES = 3;

    private final IssueRepository issueRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    public IssueService(IssueRepository issueRepository, BookRepository bookRepository,
                        MemberRepository memberRepository) {
        this.issueRepository = issueRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public Page<Issue> list(int page, int size) {
        return issueRepository.findAllWithDetails(PageRequest.of(page, size));
    }

    /**
     * Issues a book. Runs in ONE transaction: the stock decrement and the new Issue row
     * are saved together or not at all. The @Version field on Book stops two librarians
     * from issuing the last copy at the same time (optimistic locking).
     */
    @Transactional
    public void issueBook(Long bookId, Long memberId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("No copies of \"" + book.getTitle() + "\" are available");
        }
        if (issueRepository.countByMemberIdAndReturnDateIsNull(memberId) >= MAX_ACTIVE_ISSUES) {
            throw new IllegalStateException(member.getName() + " already has " + MAX_ACTIVE_ISSUES + " books issued");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1); // dirty checking -> UPDATE ... WHERE version=?
        LocalDate today = LocalDate.now();
        issueRepository.save(new Issue(book, member, today, today.plusDays(LOAN_DAYS)));
    }

    /** Returns a book and calculates the late fine. Returns the fine amount. */
    @Transactional
    public int returnBook(Long issueId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new IllegalArgumentException("Issue record not found"));
        if (issue.getReturnDate() != null) {
            throw new IllegalStateException("This book is already returned");
        }
        LocalDate today = LocalDate.now();
        long lateDays = Math.max(0, ChronoUnit.DAYS.between(issue.getDueDate(), today));
        int fine = (int) lateDays * FINE_PER_DAY;

        issue.setReturnDate(today);
        issue.setFine(fine);
        Book book = issue.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        return fine;
    }
}
