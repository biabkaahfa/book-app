package com.example.demo.service;

import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Disponibilite;
import com.example.demo.model.RendezVous;
import com.example.demo.repository.DisponibiliteRepository;
import com.example.demo.repository.RendezVousRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RendezVousService {

    private final RendezVousRepository rendezVousRepository;
    private final DisponibiliteRepository disponibiliteRepository;

    public List<RendezVous> findAll() {
        return rendezVousRepository.findAll();
    }

    public RendezVous findById(Long id) {
        return rendezVousRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rendez-vous non trouvé avec l'id : " + id));
    }

    public RendezVous create(@Valid RendezVous rendezVous) {
        LocalDateTime dateRdv = rendezVous.getDateRendezVous();
        Long medecinId = rendezVous.getMedecin().getId();

        // Étape 1 - Vérification de l'espacement de 30 minutes
        LocalDate date = dateRdv.toLocalDate();
        List<RendezVous> rdvsExistants = rendezVousRepository.findByMedecinIdAndDate(medecinId, date);

        for (RendezVous existant : rdvsExistants) {
            Duration diff = Duration.between(existant.getDateRendezVous(), dateRdv).abs();
            if (diff.toMinutes() < 30) {
                throw new BusinessException("Un autre rendez-vous est déjà prévu à une heure trop proche (moins de 30 minutes).");
            }
        }

        // Étape 2 - Vérification de la disponibilité
        LocalTime heureRdv = dateRdv.toLocalTime();
        List<Disponibilite> dispos = disponibiliteRepository.findByMedecinIdAndDateDisponibilite(medecinId, date);

        boolean isInDisponibilite = false;

        if (!dispos.isEmpty()) {
            for (Disponibilite dispo : dispos) {
                LocalTime start = dispo.getHeureDebut();
                LocalTime end = dispo.getHeureFin();
                LocalTime pauseStart = dispo.getHeurePauseDebut();
                LocalTime pauseEnd = dispo.getHeurePauseFin();

                boolean isInWorkingTime = !heureRdv.isBefore(start) && heureRdv.isBefore(end);
                boolean isInPause = pauseStart != null && pauseEnd != null && !heureRdv.isBefore(pauseStart) && heureRdv.isBefore(pauseEnd);

                if (isInWorkingTime && !isInPause) {
                    isInDisponibilite = true;
                    break;
                }
            }
        } else {
            // Aucun enregistrement de disponibilité, appliquer les horaires par défaut
            boolean inMorning = !heureRdv.isBefore(LocalTime.of(8, 0)) && heureRdv.isBefore(LocalTime.of(12, 0));
            boolean inAfternoon = !heureRdv.isBefore(LocalTime.of(13, 0)) && heureRdv.isBefore(LocalTime.of(17, 0));

            if (inMorning || inAfternoon) {
                isInDisponibilite = true;
            }
        }

        if (!isInDisponibilite) {
            throw new BusinessException("L'heure du rendez-vous ne correspond à aucune plage horaire disponible.");
        }

        // Enregistrement si tout est bon
        return rendezVousRepository.save(rendezVous);
    }


    public RendezVous update(Long id, @Valid RendezVous updatedRendezVous) {
        RendezVous existing = findById(id);

        if (!existing.peutEtreAnnule()) {
            throw new BusinessException("Le rendez-vous ne peut pas être modifié à moins de 24h de l’échéance.");
        }

        existing.setDateRendezVous(updatedRendezVous.getDateRendezVous());
        existing.setStatut(updatedRendezVous.getStatut());
        existing.setMotif(updatedRendezVous.getMotif());
        existing.setNotes(updatedRendezVous.getNotes());
        existing.setMedecin(updatedRendezVous.getMedecin());
        existing.setPatient(updatedRendezVous.getPatient());

        return rendezVousRepository.save(existing);
    }

    public void delete(Long id) {
        RendezVous rendezVous = findById(id);

        if (!rendezVous.peutEtreAnnule()) {
            throw new BusinessException("Ce rendez-vous ne peut pas être annulé.");
        }

        rendezVousRepository.delete(rendezVous);
    }

    public List<RendezVous> getRendezVousByMedecin(Long medecinId) {
        return rendezVousRepository.findByMedecinId(medecinId);
    }

    public List<RendezVous> getRendezVousByPatient(Long patientId) {
        return rendezVousRepository.findByPatientId(patientId);
    }


    public List<RendezVous> getRendezVousFutursPourPatient(Long patientId) {
        return rendezVousRepository.findFutureRendezVousByPatient(patientId, LocalDateTime.now());
    }

    public List<RendezVous> getTousLesRendezVousPourPatient(Long patientId) {
        return rendezVousRepository.findAllRendezVousByPatient(patientId);
    }

}