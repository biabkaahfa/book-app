package controller;

import model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import repository.BookRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();

        book1 = new Book("Test Book 1", "Author 1", "Description 1", LocalDate.now());
        book2 = new Book("Test Book 2", "Author 2", "Description 2", LocalDate.now());
        
        bookRepository.saveAll(List.of(book1, book2));
    }

    @Test
    void testCreateBook() throws Exception {
        Book newBook = new Book("New Book", "New Author", "New Description", LocalDate.now());

        MvcResult result = mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titre").value("New Book"))
                .andExpect(jsonPath("$.auteur").value("New Author"))
                .andReturn();

        Book createdBook = objectMapper.readValue(result.getResponse().getContentAsString(), Book.class);
        assertNotNull(createdBook.getId());
        assertEquals("New Book", createdBook.getTitre());
    }

    @Test
    void testGetAllBooks() throws Exception {
        mockMvc.perform(get("/api/books")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].titre").value("Test Book 1"))
                .andExpect(jsonPath("$.content[1].titre").value("Test Book 2"));
    }

    @Test
    void testSearchBooks() throws Exception {
        mockMvc.perform(get("/api/books/search")
                .param("query", "Test")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].titre").value("Test Book 1"))
                .andExpect(jsonPath("$.content[1].titre").value("Test Book 2"));
    }

    @Test
    void testUpdateBook() throws Exception {
        Book updatedBook = new Book("Updated Book", "Updated Author", "Updated Description", LocalDate.now());

        mockMvc.perform(put("/api/books/" + book1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titre").value("Updated Book"))
                .andExpect(jsonPath("$.auteur").value("Updated Author"));

        Book book = bookRepository.findById(book1.getId()).orElse(null);
        assertNotNull(book);
        assertEquals("Updated Book", book.getTitre());
    }

    @Test
    void testDeleteBook() throws Exception {
        mockMvc.perform(delete("/api/books/" + book1.getId()))
                .andExpect(status().isNoContent());

        assertFalse(bookRepository.existsById(book1.getId()));
        assertTrue(bookRepository.existsById(book2.getId()));
    }

    @Test
    void testInvalidBookCreation() throws Exception {
        Book invalidBook = new Book("", "", "Description", LocalDate.now().plusDays(1));

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidBook)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testNonExistentBookUpdate() throws Exception {
        Book updatedBook = new Book("Updated Book", "Updated Author", "Updated Description", LocalDate.now());

        mockMvc.perform(put("/api/books/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBook)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testNonExistentBookDelete() throws Exception {
        mockMvc.perform(delete("/api/books/999"))
                .andExpect(status().isNotFound());
    }
} 