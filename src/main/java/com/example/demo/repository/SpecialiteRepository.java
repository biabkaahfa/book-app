package com.example.demo.repository;

import com.example.demo.model.Specialite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpecialiteRepository extends JpaRepository<Specialite,Long> {
    Optional<Specialite> findByNom(String nom);

    boolean existsByNom(String nom);

    @Query("SELECT s FROM Specialite s WHERE LOWER(s.nom) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Specialite> searchByKeyword(String keyword);

    @Query("SELECT COUNT(s) FROM Specialite s")
    long countAll();

    @Query("SELECT s FROM Specialite s ORDER BY s.nom ASC")
    List<Specialite> findAllOrdered();
}
