package com.example.demo.service;

import com.example.demo.dto.DisponibiliteDTO;
import com.example.demo.dto.CreateDisponibiliteDTO;
import com.example.demo.dto.UpdateDisponibiliteDTO;
import com.example.demo.model.Disponibilite;
import com.example.demo.model.Medecin;
import com.example.demo.repository.DisponibiliteRepository;
import com.example.demo.repository.MedecinRepository;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.BusinessException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DisponibiliteService {

    private final DisponibiliteRepository disponibiliteRepository;
    private final MedecinRepository medecinRepository;

    @Transactional(readOnly = true)
    public Page<DisponibiliteDTO> findAll(Pageable pageable) {
        return disponibiliteRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public DisponibiliteDTO findById(Long id) {
        Disponibilite disponibilite = disponibiliteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilité non trouvée avec l'ID : " + id));
        return convertToDTO(disponibilite);
    }

    @Transactional(readOnly = true)
    public List<DisponibiliteDTO> findByMedecinId(Long medecinId) {
        return disponibiliteRepository.findByMedecinId(medecinId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DisponibiliteDTO> findByMedecinIdAndJour(Long medecinId, DayOfWeek jour) {
        return disponibiliteRepository.findByMedecinIdAndJourSemaine(medecinId, jour)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DisponibiliteDTO> findActiveByMedecinId(Long medecinId) {
        return disponibiliteRepository.findByMedecinIdAndEstActiveTrue(medecinId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DisponibiliteDTO> findActiveDisponibilitesOrdered(Long medecinId) {
        return disponibiliteRepository.findActiveDisponibilitesOrdered(medecinId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LocalTime> obtenirCreneauxDisponibles(Long disponibiliteId) {
        Disponibilite disponibilite = disponibiliteRepository.findById(disponibiliteId)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilité non trouvée avec l'ID : " + disponibiliteId));
        return disponibilite.obtenirCreneauxDisponibles();
    }

    public DisponibiliteDTO create(@Valid CreateDisponibiliteDTO createDTO) {
        // Vérifie que le médecin existe
        Medecin medecin = medecinRepository.findById(createDTO.getMedecinId())
                .orElseThrow(() -> new ResourceNotFoundException("Médecin non trouvé avec l'ID : " + createDTO.getMedecinId()));

        // Validation des horaires
        validateHoraires(createDTO.getHeureDebut(), createDTO.getHeureFin());

        Disponibilite disponibilite = new Disponibilite();
        disponibilite.setMedecin(medecin);
        disponibilite.setJourSemaine(createDTO.getJourSemaine());
        disponibilite.setDateDisponibilite(createDTO.getDateDisponibilite());
        disponibilite.setHeureDebut(createDTO.getHeureDebut());
        disponibilite.setHeureFin(createDTO.getHeureFin());
        disponibilite.setHeurePauseDebut(createDTO.getHeurePauseDebut());
        disponibilite.setHeurePauseFin(createDTO.getHeurePauseFin());
        disponibilite.setDureeCreneau(createDTO.getDureeCreneau());
        disponibilite.setEstActive(true);

        Disponibilite saved = disponibiliteRepository.save(disponibilite);
        return convertToDTO(saved);
    }

    public DisponibiliteDTO update(Long id, UpdateDisponibiliteDTO updateDTO) {
        Disponibilite disponibilite = disponibiliteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilité non trouvée avec l'ID : " + id));

        // Validation des horaires si fournis
        LocalTime heureDebut = updateDTO.getHeureDebut() != null ? updateDTO.getHeureDebut() : disponibilite.getHeureDebut();
        LocalTime heureFin = updateDTO.getHeureFin() != null ? updateDTO.getHeureFin() : disponibilite.getHeureFin();
        validateHoraires(heureDebut, heureFin);

        // Mise à jour des champs
        if (updateDTO.getJourSemaine() != null) {
            disponibilite.setJourSemaine(updateDTO.getJourSemaine());
        }
        if (updateDTO.getHeureDebut() != null) {
            disponibilite.setHeureDebut(updateDTO.getHeureDebut());
        }
        if (updateDTO.getHeureFin() != null) {
            disponibilite.setHeureFin(updateDTO.getHeureFin());
        }
        if (updateDTO.getDureeCreneau() != null) {
            disponibilite.setDureeCreneau(updateDTO.getDureeCreneau());
        }
        if (updateDTO.getEstActive() != null) {
            disponibilite.setEstActive(updateDTO.getEstActive());
        }

        Disponibilite updated = disponibiliteRepository.save(disponibilite);
        return convertToDTO(updated);
    }

    public void delete(Long id) {
        if (!disponibiliteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Disponibilité non trouvée avec l'ID : " + id);
        }
        disponibiliteRepository.deleteById(id);
    }

    public DisponibiliteDTO toggleActive(Long id) {
        Disponibilite disponibilite = disponibiliteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilité non trouvée avec l'ID : " + id));

        disponibilite.setEstActive(!disponibilite.isEstActive());
        Disponibilite updated = disponibiliteRepository.save(disponibilite);
        return convertToDTO(updated);
    }

    private void validateHoraires(LocalTime heureDebut, LocalTime heureFin) {
        if (heureDebut.isAfter(heureFin) || heureDebut.equals(heureFin)) {
            throw new BusinessException("L'heure de début doit être antérieure à l'heure de fin");
        }
    }

    private DisponibiliteDTO convertToDTO(Disponibilite disponibilite) {
        DisponibiliteDTO dto = new DisponibiliteDTO();
        dto.setId(disponibilite.getId());
        dto.setMedecinId(disponibilite.getMedecin().getId());
        dto.setMedecinNom(disponibilite.getMedecin().getNom());
        dto.setJourSemaine(disponibilite.getJourSemaine());
        dto.setHeureDebut(disponibilite.getHeureDebut());
        dto.setHeureFin(disponibilite.getHeureFin());
        dto.setDureeCreneau(disponibilite.getDureeCreneau());
        dto.setEstActive(disponibilite.isEstActive());
        dto.setCreneaux(disponibilite.obtenirCreneauxDisponibles()); // ajouter les créneaux ici
        return dto;
    }



    public List<DisponibiliteDTO> createDisponibilitesHebdomadaires(
            Long medecinId,
            LocalTime heureDebut,
            LocalTime heureFin,
            Duration dureeCreneau,
            LocalTime heurePauseDebut,
            LocalTime heurePauseFin,
            LocalDate startDate // date de début de la semaine (ex: lundi prochain)
    ) {
        Medecin medecin = medecinRepository.findById(medecinId)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin non trouvé avec l'ID : " + medecinId));

        validateHoraires(heureDebut, heureFin);

        List<DisponibiliteDTO> result = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            DayOfWeek jour = currentDate.getDayOfWeek();

            // Évite les doublons sur la même date
            boolean exists = disponibiliteRepository.existsByMedecinIdAndDateDisponibilite(medecinId, currentDate);
            if (!exists) {
                Disponibilite dispo = new Disponibilite();
                dispo.setMedecin(medecin);
                dispo.setJourSemaine(jour);
                dispo.setDateDisponibilite(currentDate);
                dispo.setHeureDebut(heureDebut);
                dispo.setHeureFin(heureFin);
                dispo.setDureeCreneau(dureeCreneau);
                dispo.setHeurePauseDebut(heurePauseDebut);
                dispo.setHeurePauseFin(heurePauseFin);
                dispo.setEstActive(true);

                Disponibilite saved = disponibiliteRepository.save(dispo);
                result.add(convertToDTO(saved));
            }
        }

        return result;
    }



    public List<DisponibiliteDTO> createDisponibilitesPourJours(
            Long medecinId,
            LocalTime heureDebut,
            LocalTime heureFin,
            Duration dureeCreneau,
            LocalTime heurePauseDebut,
            LocalTime heurePauseFin,
            List<LocalDate> dates // Ex: [2024-12-10, 2024-12-12, 2024-12-14]
    ) {
        Medecin medecin = medecinRepository.findById(medecinId)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin non trouvé avec l'ID : " + medecinId));

        validateHoraires(heureDebut, heureFin);

        List<DisponibiliteDTO> result = new ArrayList<>();

        for (LocalDate date : dates) {
            DayOfWeek jour = date.getDayOfWeek();

            if (!disponibiliteRepository.existsByMedecinIdAndDateDisponibilite(medecinId, date)) {
                Disponibilite dispo = new Disponibilite();
                dispo.setMedecin(medecin);
                dispo.setJourSemaine(jour);
                dispo.setDateDisponibilite(date);
                dispo.setHeureDebut(heureDebut);
                dispo.setHeureFin(heureFin);
                dispo.setDureeCreneau(dureeCreneau);
                dispo.setHeurePauseDebut(heurePauseDebut);
                dispo.setHeurePauseFin(heurePauseFin);
                dispo.setEstActive(true);

                Disponibilite saved = disponibiliteRepository.save(dispo);
                result.add(convertToDTO(saved));
            }
        }

        return result;
    }



    public List<DisponibiliteDTO> findDisponibilitesDeLaSemaine(Long medecinId) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(java.time.DayOfWeek.SUNDAY);

        List<Disponibilite> disponibilites = disponibiliteRepository
                .findByMedecinIdAndDateDisponibiliteBetween(medecinId, startOfWeek, endOfWeek);

        return disponibilites.stream()
                .map(dispo -> {
                    DisponibiliteDTO dto = convertToDTO(dispo);
                    dto.setCreneaux(dispo.obtenirCreneauxDisponibles());
                    return dto;
                })
                .collect(Collectors.toList());
    }




}