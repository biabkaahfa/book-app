package com.example.demo.repository;

import com.example.demo.enums.StatutRendezVous;
import com.example.demo.model.RendezVous;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous,Long> {
    List<RendezVous> findByPatientId(Long patientId);

    List<RendezVous> findByMedecinId(Long medecinId);

    @Query("SELECT r FROM RendezVous r WHERE r.medecin.id = :medecinId AND DATE(r.dateRendezVous) = :date")
    List<RendezVous> findByMedecinIdAndDate(@Param("medecinId") Long medecinId, @Param("date") LocalDate date);

    // ✅ Tous les rendez-vous à venir (à partir d'aujourd'hui) pour un patient
    @Query("SELECT r FROM RendezVous r WHERE r.patient.id = :patientId AND r.dateRendezVous >= :now ORDER BY r.dateRendezVous ASC")
    List<RendezVous> findFutureRendezVousByPatient(
            @Param("patientId") Long patientId,
            @Param("now") LocalDateTime now
    );

    // ✅ Tous les rendez-vous (passés et futurs) pour un patient
    @Query("SELECT r FROM RendezVous r WHERE r.patient.id = :patientId ORDER BY r.dateRendezVous DESC")
    List<RendezVous> findAllRendezVousByPatient(
            @Param("patientId") Long patientId
    );


}