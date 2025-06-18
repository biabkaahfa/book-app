package com.example.demo.repository;
import com.example.demo.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
	Page<Book> findByTitreContainingIgnoreCaseOrAuteurContainingIgnoreCase(String query, String query2,Pageable pageable);
	Book findByTitreIgnoreCaseAndDatePublication(String titre, java.time.LocalDate datePublication);
	Book findByTitreIgnoreCaseAndAuteurIgnoreCase(String titre, String auteur);
	Book findByTitreIgnoreCase(String titre);
	@Query("SELECT b.auteur as auteur, COUNT(b) as nombreLivres FROM Book b GROUP BY b.auteur")
	List<Map<String, Object>> countBooksByAuteur();
}
