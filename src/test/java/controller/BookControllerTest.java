package controller;

import model.Book;
import repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Book book;
    private Page<Book> bookPage;

    @BeforeEach
    void setUp() {
        book = new Book("Test Book", "Test Author", "Test Description", LocalDate.now());
        book.setId(1L);
        bookPage = new PageImpl<>(Arrays.asList(book));
    }

    @Test
    void getAllBooks_ShouldReturnBooks() throws Exception {
        when(bookRepository.findAll(any(PageRequest.class))).thenReturn(bookPage);

        mockMvc.perform(get("/api/books")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].titre").value("Test Book"))
                .andExpect(jsonPath("$.content[0].auteur").value("Test Author"));
    }

    @Test
    void searchBooks_ShouldReturnMatchingBooks() throws Exception {
        when(bookRepository.findByTitreContainingIgnoreCaseOrAuteurContainingIgnoreCase(
                any(), any(), any(PageRequest.class))).thenReturn(bookPage);

        mockMvc.perform(get("/api/books/search")
                .param("query", "Test")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].titre").value("Test Book"));
    }

    @Test
    void createBook_ShouldCreateNewBook() throws Exception {
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titre").value("Test Book"))
                .andExpect(jsonPath("$.auteur").value("Test Author"));
    }

    @Test
    void updateBook_ShouldUpdateExistingBook() throws Exception {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        mockMvc.perform(put("/api/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titre").value("Test Book"));
    }

    @Test
    void updateBook_ShouldReturnNotFound_WhenBookDoesNotExist() throws Exception {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBook_ShouldDeleteExistingBook() throws Exception {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteBook_ShouldReturnNotFound_WhenBookDoesNotExist() throws Exception {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNotFound());
    }
} 