package com.example.demo.repository;
import com.example.demo.model.Disponibilite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DisponibiliteRepository extends JpaRepository<Disponibilite,Long> {
    List<Disponibilite> findByMedecinId(Long medecinId);

    List<Disponibilite> findByMedecinIdAndJourSemaine(Long medecinId, DayOfWeek jour);

    List<Disponibilite> findByMedecinIdAndEstActiveTrue(Long medecinId);

    boolean existsByMedecinIdAndJourSemaine(Long medecinId, DayOfWeek jour);
    boolean existsByMedecinIdAndDateDisponibilite(Long medecinId, LocalDate date);


    @Query("SELECT d FROM Disponibilite d WHERE d.estActive = true AND d.medecin.id = :medecinId ORDER BY d.jourSemaine, d.heureDebut")
    List<Disponibilite> findActiveDisponibilitesOrdered(Long medecinId);

    List<Disponibilite> findByMedecinIdAndDateDisponibiliteBetween(Long medecinId, LocalDate start, LocalDate end);
    List<Disponibilite> findByMedecinIdAndDateDisponibilite(Long medecinId, LocalDate date);

}