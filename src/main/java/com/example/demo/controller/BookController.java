package com.example.demo.controller;

import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@Validated
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/search")
    public ResponseEntity<Page<Book>> searchBooks(
            @RequestParam String query,
            @RequestParam @Min(0) int page,  
            @RequestParam @Min(1) int size) {
        logger.info("Recherche de livres avec la requête: {}", query);
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> books = bookRepository.findByTitreContainingIgnoreCaseOrAuteurContainingIgnoreCase(query, query, pageable);
        return ResponseEntity.ok(books);
    }

    @GetMapping
    public ResponseEntity<Page<Book>> getAllBooks(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        logger.info("Récupération de tous les livres, page: {}, taille: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> books = bookRepository.findAll(pageable);
        return ResponseEntity.ok(books);
    }

    @PostMapping
    public ResponseEntity<?> createBook(@Valid @RequestBody Book book) {
        logger.info("Création d'un nouveau livre: {}", book.getTitre());
        // Validation caractères spéciaux
        Pattern forbidden = Pattern.compile("[\\{\\}\\.\\*\\/\\-\\+\\)\\(\\:;,]");
        if (forbidden.matcher(book.getTitre()).find() || forbidden.matcher(book.getAuteur()).find() || (book.getDescription() != null && forbidden.matcher(book.getDescription()).find())) {
            return ResponseEntity.badRequest().body("Les champs titre, auteur et description ne doivent pas contenir de caractères spéciaux: {} . * / - + ) ( : ; ,");
        }
        // Refus si un livre avec le même titre existe déjà (peu importe l'auteur)
        Book sameTitle = bookRepository.findByTitreIgnoreCase(book.getTitre());
        if (sameTitle != null) {
            return ResponseEntity.badRequest().body("Ce livre appartient déjà à un auteur.");
        }
        Book savedBook = bookRepository.save(book);
        return ResponseEntity.ok(savedBook);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody Book bookDetails) {
        logger.info("Mise à jour du livre avec l'ID: {}", id);
        return bookRepository.findById(id)
                .map(book -> {
                    book.setTitre(bookDetails.getTitre());
                    book.setAuteur(bookDetails.getAuteur());
                    book.setDescription(bookDetails.getDescription());
                    book.setDatePublication(bookDetails.getDatePublication());
                    Book updatedBook = bookRepository.save(book);
                    return ResponseEntity.ok(updatedBook);
                })
                .orElseGet(() -> {
                    logger.warn("Livre non trouvé avec l'ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        logger.info("Suppression du livre avec l'ID: {}", id);
        return bookRepository.findById(id)
                .map(book -> {
                    bookRepository.delete(book);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> {
                    logger.warn("Livre non trouvé avec l'ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Optional<Book> book = bookRepository.findById(id);
        return book.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStatsByAuteur() {
        var stats = bookRepository.countBooksByAuteur();
        return ResponseEntity.ok(stats);
    }
}
