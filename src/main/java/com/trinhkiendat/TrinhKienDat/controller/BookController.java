package com.trinhkiendat.TrinhKienDat.controller;

import java.util.List;
import com.trinhkiendat.TrinhKienDat.model.Book;
import com.trinhkiendat.TrinhKienDat.model.Category;
import com.trinhkiendat.TrinhKienDat.repository.BookRepository;
import com.trinhkiendat.TrinhKienDat.repository.CategoryRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/books")
public class BookController {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    public BookController(BookRepository bookRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
        public String listBooks(
            Model model,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
        ) {
            Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort); // Biến pageable được tạo ở đây
            
            Page<Book> bookPage;
            if (keyword != null && !keyword.isEmpty()) {
                // PHẢI CÓ 'pageable' Ở ĐÂY
                bookPage = bookRepository.searchBooks(keyword, pageable); 
            } else {
                bookPage = bookRepository.findAll(pageable);
            }

            model.addAttribute("books", bookPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", bookPage.getTotalPages());
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("keyword", keyword);
            return "books";
        }


    // Hiển thị form thêm sách
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("categories", categoryRepository.findAll());
        return "add";
    }

    // Thêm sách
    @PostMapping("/add")
    public String addBook(@Valid @ModelAttribute Book book, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            return "add";
        }
        if (book.getCategory() != null && book.getCategory().getId() != null) {
            Category category = categoryRepository.findById(book.getCategory().getId()).orElse(null);
            book.setCategory(category);
        }
        bookRepository.save(book);
        return "redirect:/books";
    }

    // Sửa sách - hiển thị form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book == null) {
            return "redirect:/books";
        }
        // Đảm bảo đối tượng category tồn tại để form có thể bind dữ liệu vào id
        if (book.getCategory() == null) {
            book.setCategory(new Category());
        }
        model.addAttribute("book", book);
        model.addAttribute("categories", categoryRepository.findAll());
        return "edit";
    }

    // Sửa sách - xử lý cập nhật
    @PostMapping("/edit")
    public String editBook(@Valid @ModelAttribute("book") Book book, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            return "edit";
        }

        System.out.println("[DEBUG] editBook POST received. incoming book id=" + book.getId());
        System.out.println("[DEBUG] incoming fields: title='" + book.getTitle() + "', author='" + book.getAuthor() + "', price=" + book.getPrice() + ", category=" + (book.getCategory() != null ? book.getCategory().getId() : "null"));

        if (book.getId() == null) {
            System.out.println("[DEBUG] No id provided in form.");
            return "redirect:/books";
        }

        Book oldBook = bookRepository.findById(book.getId()).orElse(null);
        if (oldBook == null) {
            System.out.println("[DEBUG] No existing book found for id=" + book.getId());
            return "redirect:/books";
        }

        System.out.println("[DEBUG] oldBook before update: id=" + oldBook.getId() + ", title='" + oldBook.getTitle() + "', category=" + (oldBook.getCategory() != null ? oldBook.getCategory().getId() : "null"));

        oldBook.setTitle(book.getTitle());
        oldBook.setAuthor(book.getAuthor());
        oldBook.setPrice(book.getPrice());

        Long categoryId = (book.getCategory() != null) ? book.getCategory().getId() : null;
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId).orElse(null);
            oldBook.setCategory(category);
        } else {
            oldBook.setCategory(null);
        }

        Book saved = bookRepository.saveAndFlush(oldBook);
        System.out.println("[DEBUG] saved book id=" + (saved != null ? saved.getId() : "null") + ", category=" + (saved != null && saved.getCategory() != null ? saved.getCategory().getId() : "null"));
        return "redirect:/books";
    }

    // Xóa sách
    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookRepository.deleteById(id);
        return "redirect:/books";
    }
}
