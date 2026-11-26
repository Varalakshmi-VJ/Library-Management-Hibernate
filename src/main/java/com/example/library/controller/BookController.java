package com.example.library.controller;

import com.example.library.model.Book;
import com.example.library.service.BookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class BookController {

    private static final int PAGE_SIZE = 5;

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /** Form-backing object (kept separate from the entity because authors are typed as text). */
    public static class BookForm {
        private Long id;
        @NotBlank(message = "Title is required") private String title;
        @NotBlank(message = "ISBN is required") private String isbn;
        private String category;
        @Min(value = 1, message = "At least 1 copy") private int totalCopies = 1;
        @NotBlank(message = "Enter at least one author (comma separated)") private String authorNames;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getIsbn() { return isbn; }
        public void setIsbn(String isbn) { this.isbn = isbn; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public int getTotalCopies() { return totalCopies; }
        public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }
        public String getAuthorNames() { return authorNames; }
        public void setAuthorNames(String authorNames) { this.authorNames = authorNames; }
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/books";
    }

    @GetMapping("/books")
    public String list(@RequestParam(defaultValue = "") String q,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        Page<Book> books = bookService.search(q, page, PAGE_SIZE);
        model.addAttribute("books", books);
        model.addAttribute("q", q);
        model.addAttribute("active", "books");
        return "books";
    }

    @GetMapping("/books/new")
    public String newForm(Model model) {
        model.addAttribute("form", new BookForm());
        model.addAttribute("active", "books");
        return "book-form";
    }

    @GetMapping("/books/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Book b = bookService.get(id);
        BookForm f = new BookForm();
        f.setId(b.getId());
        f.setTitle(b.getTitle());
        f.setIsbn(b.getIsbn());
        f.setCategory(b.getCategory());
        f.setTotalCopies(b.getTotalCopies());
        f.setAuthorNames(b.getAuthorNames());
        model.addAttribute("form", f);
        model.addAttribute("active", "books");
        return "book-form";
    }

    @PostMapping("/books/save")
    public String save(@Valid @ModelAttribute("form") BookForm form, BindingResult result,
                       Model model, RedirectAttributes ra) {
        if (!result.hasFieldErrors("isbn") && bookService.isbnTaken(form.getIsbn(), form.getId())) {
            result.rejectValue("isbn", "duplicate", "A book with this ISBN already exists");
        }
        if (result.hasErrors()) {
            model.addAttribute("active", "books");
            return "book-form";
        }
        try {
            bookService.save(form.getId(), form.getTitle(), form.getIsbn(), form.getCategory(),
                    form.getTotalCopies(), form.getAuthorNames());
        } catch (IllegalStateException e) {
            result.rejectValue("totalCopies", "invalid", e.getMessage());
            model.addAttribute("active", "books");
            return "book-form";
        }
        ra.addFlashAttribute("success", "Book saved");
        return "redirect:/books";
    }

    @PostMapping("/books/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            bookService.delete(id);
            ra.addFlashAttribute("success", "Book deleted");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/books";
    }
}
