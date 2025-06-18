package model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidBook_thenNoViolations() {
        Book book = new Book("Valid Title", "Valid Author", "Valid Description", LocalDate.now());
        Set<jakarta.validation.ConstraintViolation<Book>> violations = validator.validate(book);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenEmptyTitle_thenViolation() {
        Book book = new Book("", "Valid Author", "Valid Description", LocalDate.now());
        Set<jakarta.validation.ConstraintViolation<Book>> violations = validator.validate(book);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Le titre ne peut pas être vide", violations.iterator().next().getMessage());
    }

    @Test
    void whenEmptyAuthor_thenViolation() {
        Book book = new Book("Valid Title", "", "Valid Description", LocalDate.now());
        Set<jakarta.validation.ConstraintViolation<Book>> violations = validator.validate(book);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("L'auteur ne peut pas être vide", violations.iterator().next().getMessage());
    }

    @Test
    void whenFutureDate_thenViolation() {
        Book book = new Book("Valid Title", "Valid Author", "Valid Description", LocalDate.now().plusDays(1));
        Set<jakarta.validation.ConstraintViolation<Book>> violations = validator.validate(book);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("La date de publication ne peut pas être dans le futur", violations.iterator().next().getMessage());
    }

    @Test
    void whenDescriptionTooLong_thenViolation() {
        String longDescription = "a".repeat(1001);
        Book book = new Book("Valid Title", "Valid Author", longDescription, LocalDate.now());
        Set<jakarta.validation.ConstraintViolation<Book>> violations = validator.validate(book);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("La description ne peut pas dépasser 1000 caractères", violations.iterator().next().getMessage());
    }

    @Test
    void testGettersAndSetters() {
        Book book = new Book();
        LocalDate date = LocalDate.now();

        book.setId(1L);
        book.setTitre("Test Title");
        book.setAuteur("Test Author");
        book.setDescription("Test Description");
        book.setDatePublication(date);

        assertEquals(1L, book.getId());
        assertEquals("Test Title", book.getTitre());
        assertEquals("Test Author", book.getAuteur());
        assertEquals("Test Description", book.getDescription());
        assertEquals(date, book.getDatePublication());
    }

    @Test
    void testToString() {
        Book book = new Book("Test Title", "Test Author", "Test Description", LocalDate.now());
        book.setId(1L);
        
        String expected = "Book{id=1, titre='Test Title', auteur='Test Author', datePublication=" + book.getDatePublication() + "}";
        assertEquals(expected, book.toString());
    }
} 