package com.trinhkiendat.TrinhKienDat.controller;

import com.trinhkiendat.TrinhKienDat.model.Book;
import com.trinhkiendat.TrinhKienDat.model.CartItem;
import com.trinhkiendat.TrinhKienDat.repository.BookRepository;
import com.trinhkiendat.TrinhKienDat.model.Invoice;
import com.trinhkiendat.TrinhKienDat.model.ItemInvoice;
import com.trinhkiendat.TrinhKienDat.repository.InvoiceRepository;
import com.trinhkiendat.TrinhKienDat.repository.ItemInvoiceRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
@SessionAttributes("cart")
public class CartController {
    private final BookRepository bookRepository;
    private final InvoiceRepository invoiceRepository;
    private final ItemInvoiceRepository itemInvoiceRepository;

    public CartController(BookRepository bookRepository, InvoiceRepository invoiceRepository, ItemInvoiceRepository itemInvoiceRepository) {
        this.bookRepository = bookRepository;
        this.invoiceRepository = invoiceRepository;
        this.itemInvoiceRepository = itemInvoiceRepository;
    }
    @PostMapping("/checkout")
    public String checkout(@ModelAttribute("cart") List<CartItem> cart, SessionStatus status) {
        if (cart.isEmpty()) return "redirect:/cart";
        Invoice invoice = new Invoice();
        invoice.setInvoiceDate(LocalDateTime.now());
        double total = cart.stream().mapToDouble(item -> item.getBook().getPrice() * item.getQuantity()).sum();
        invoice.setTotal(total);
        invoiceRepository.save(invoice);

        for (CartItem item : cart) {
            ItemInvoice itemInvoice = new ItemInvoice();
            itemInvoice.setBook(item.getBook());
            itemInvoice.setQuantity(item.getQuantity());
            itemInvoice.setTotal(item.getBook().getPrice() * item.getQuantity());
            itemInvoice.setInvoice(invoice);
            itemInvoiceRepository.save(itemInvoice);
        }
        status.setComplete();
        return "redirect:/cart?success";
    }

    @ModelAttribute("cart")
    public List<CartItem> cart() {
        return new ArrayList<>();
    }

    @GetMapping
    public String showCart(Model model, @ModelAttribute("cart") List<CartItem> cart) {
        double total = cart.stream().mapToDouble(item -> item.getBook().getPrice() * item.getQuantity()).sum();
        model.addAttribute("cartItems", cart);
        model.addAttribute("total", total);
        return "cart";
    }

    @GetMapping("/add/{id}")
    public String addToCart(@PathVariable Long id, @ModelAttribute("cart") List<CartItem> cart) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book != null) {
            for (CartItem item : cart) {
                if (item.getBook().getId().equals(id)) {
                    item.setQuantity(item.getQuantity() + 1);
                    return "redirect:/cart";
                }
            }
            cart.add(new CartItem(book, 1));
        }
        return "redirect:/cart";
    }

    @GetMapping("/remove/{id}")
    public String removeFromCart(@PathVariable Long id, @ModelAttribute("cart") List<CartItem> cart) {
        cart.removeIf(item -> item.getBook().getId().equals(id));
        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clearCart(SessionStatus status) {
        status.setComplete();
        return "redirect:/cart";
    }
}
