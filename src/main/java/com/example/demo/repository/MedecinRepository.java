package com.example.demo.repository;

import com.example.demo.model.Medecin;
import com.example.demo.model.RendezVous;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MedecinRepository extends JpaRepository<Medecin, Long> {
    List<Medecin> findByActifTrue();

    List<Medecin> findBySpecialiteId(Long specialiteId);

    List<Medecin> findBySpecialiteIdAndActifTrue(Long specialiteId);

    @Query("SELECT m FROM Medecin m WHERE m.actif = true AND (m.nom LIKE %:search% OR m.prenom LIKE %:search% OR m.email LIKE %:search%)")
    List<Medecin> searchActiveMedecins(String search);

    @Query("SELECT m FROM Medecin m JOIN m.disponibilites d WHERE d.estActive = true")
    List<Medecin> findMedecinsWithActiveDisponibilites();

    boolean existsByNumeroLicence(String numeroLicence);


    @Query("SELECT r FROM RendezVous r WHERE r.medecin.id = :medecinId AND r.dateRendezVous BETWEEN :now AND :endOfDay")
    List<RendezVous> findTodayRendezVousByMedecin(
            @Param("medecinId") Long medecinId,
            @Param("now") LocalDateTime now,
            @Param("endOfDay") LocalDateTime endOfDay
    );




}