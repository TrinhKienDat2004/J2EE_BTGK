package com.trinhkiendat.TrinhKienDat.controller.api;

import com.trinhkiendat.TrinhKienDat.model.Book;
import com.trinhkiendat.TrinhKienDat.model.Category;
import com.trinhkiendat.TrinhKienDat.repository.BookRepository;
import com.trinhkiendat.TrinhKienDat.repository.CategoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
public class BookApiController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<Book> list() {
        return bookRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Book> get(@PathVariable Long id) {
        Optional<Book> b = bookRepository.findById(id);
        return b.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@Valid @RequestBody Book payload, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        if (payload.getCategory() != null && payload.getCategory().getId() != null) {
            Category c = categoryRepository.findById(payload.getCategory().getId()).orElse(null);
            payload.setCategory(c);
        }
        Book saved = bookRepository.save(payload);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody Book payload, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        Optional<Book> ob = bookRepository.findById(id);
        if (ob.isEmpty()) return ResponseEntity.notFound().build();
        Book book = ob.get();
        book.setTitle(payload.getTitle());
        book.setAuthor(payload.getAuthor());
        book.setPrice(payload.getPrice());
        if (payload.getCategory() != null && payload.getCategory().getId() != null) {
            Category c = categoryRepository.findById(payload.getCategory().getId()).orElse(null);
            book.setCategory(c);
        }
        Book saved = bookRepository.save(book);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!bookRepository.existsById(id)) return ResponseEntity.notFound().build();
        bookRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
