package com.example.demo.repository;

import com.example.demo.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByActifTrue();

    List<Patient> findByDateNaissance(LocalDate dateNaissance);

    List<Patient> findByDateNaissanceBetween(LocalDate dateDebut, LocalDate dateFin);

    @Query("SELECT p FROM Patient p WHERE p.actif = true AND (p.nom LIKE %:search% OR p.prenom LIKE %:search% OR p.email LIKE %:search%)")
    List<Patient> searchActivePatients(String search);

    @Query("SELECT p FROM Patient p JOIN p.rendezVous rv WHERE rv.medecin.id = :medecinId")
    List<Patient> findPatientsByMedecin(Long medecinId);

    @Query("SELECT COUNT(DISTINCT p) FROM Patient p JOIN p.rendezVous r WHERE r.medecin.id = :medecinId")
    Long countDistinctPatientsByMedecin(@Param("medecinId") Long medecinId);

}