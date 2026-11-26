package com.example.library.controller;

import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.library.service.BookService;
import com.example.library.service.IssueService;
import com.example.library.service.MemberService;

@Controller
public class IssueController {

    private final IssueService issueService;
    private final BookService bookService;
    private final MemberService memberService;

    public IssueController(IssueService issueService, BookService bookService, MemberService memberService) {
        this.issueService = issueService;
        this.bookService = bookService;
        this.memberService = memberService;
    }

    @GetMapping("/issues")
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("issues", issueService.list(page, 8));
        model.addAttribute("books", bookService.availableBooks());
        model.addAttribute("members", memberService.all());
        model.addAttribute("loanDays", IssueService.LOAN_DAYS);
        model.addAttribute("finePerDay", IssueService.FINE_PER_DAY);
        model.addAttribute("active", "issues");
        return "issues";
    }

    @PostMapping("/issues/issue")
    public String issue(@RequestParam Long bookId, @RequestParam Long memberId, RedirectAttributes ra) {
        try {
            issueService.issueBook(bookId, memberId);
            ra.addFlashAttribute("success", "Book issued. Due in " + IssueService.LOAN_DAYS + " days.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        } catch (ObjectRetrievalFailureException e) {
            // Another request changed the same book row first (@Version mismatch)
            ra.addFlashAttribute("error", "Someone else just updated this book. Please try again.");
        }
        return "redirect:/issues";
    }

    @PostMapping("/issues/return/{id}")
    public String returnBook(@PathVariable Long id, RedirectAttributes ra) {
        try {
            int fine = issueService.returnBook(id);
            ra.addFlashAttribute("success", fine > 0 ? "Book returned. Late fine: Rs. " + fine : "Book returned on time. No fine.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        } catch (ObjectRetrievalFailureException e) {
            ra.addFlashAttribute("error", "Someone else just updated this book. Please try again.");
        }
        return "redirect:/issues";
    }
}
