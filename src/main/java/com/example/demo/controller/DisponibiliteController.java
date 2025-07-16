package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.DisponibiliteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/disponibilites")
@RequiredArgsConstructor
public class DisponibiliteController {

    private final DisponibiliteService disponibiliteService;

    @GetMapping
    public ResponseEntity<Page<DisponibiliteDTO>> getAllDisponibilites(Pageable pageable) {
        Page<DisponibiliteDTO> disponibilites = disponibiliteService.findAll(pageable);
        return ResponseEntity.ok(disponibilites);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DisponibiliteDTO> getDisponibiliteById(@PathVariable Long id) {
        DisponibiliteDTO disponibilite = disponibiliteService.findById(id);
        return ResponseEntity.ok(disponibilite);
    }

    @GetMapping("/medecin/{medecinId}/disponibilites")
    public ResponseEntity<List<DisponibiliteDTO>> getDisponibilitesByMedecin(@PathVariable Long medecinId) {
        List<DisponibiliteDTO> disponibilites = disponibiliteService.findByMedecinId(medecinId);
        return ResponseEntity.ok(disponibilites);
    }

    @GetMapping("/medecin/{medecinId}/jour/{jour}")
    public ResponseEntity<List<DisponibiliteDTO>> getDisponibilitesByMedecinAndJour(
            @PathVariable Long medecinId,
            @PathVariable DayOfWeek jour) {
        List<DisponibiliteDTO> disponibilites = disponibiliteService.findByMedecinIdAndJour(medecinId, jour);
        return ResponseEntity.ok(disponibilites);
    }

    @GetMapping("/medecin/{medecinId}/actives")
    public ResponseEntity<List<DisponibiliteDTO>> getActiveDisponibilitesByMedecin(@PathVariable Long medecinId) {
        List<DisponibiliteDTO> disponibilites = disponibiliteService.findActiveByMedecinId(medecinId);
        return ResponseEntity.ok(disponibilites);
    }

    @GetMapping("/medecin/{medecinId}/actives/ordonnees")
    public ResponseEntity<List<DisponibiliteDTO>> getActiveDisponibilitesOrdered(@PathVariable Long medecinId) {
        List<DisponibiliteDTO> disponibilites = disponibiliteService.findActiveDisponibilitesOrdered(medecinId);
        return ResponseEntity.ok(disponibilites);
    }

    @GetMapping("/{id}/creneaux")
    public ResponseEntity<List<LocalTime>> getCreneauxDisponibles(@PathVariable Long id) {
        List<LocalTime> creneaux = disponibiliteService.obtenirCreneauxDisponibles(id);
        return ResponseEntity.ok(creneaux);
    }

    @PostMapping
    public ResponseEntity<DisponibiliteDTO> create(@RequestBody CreateDisponibiliteDTO createDTO) {
        DisponibiliteDTO saved = disponibiliteService.create(createDTO);
        return ResponseEntity.ok(saved);
    }


    @PutMapping("/{id}")
    public ResponseEntity<DisponibiliteDTO> updateDisponibilite(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDisponibiliteDTO updateDTO) {
        DisponibiliteDTO updated = disponibiliteService.update(id, updateDTO);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<DisponibiliteDTO> toggleActiveDisponibilite(@PathVariable Long id) {
        DisponibiliteDTO updated = disponibiliteService.toggleActive(id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDisponibilite(@PathVariable Long id) {
        disponibiliteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/hebdomadaire")
    public ResponseEntity<List<DisponibiliteDTO>> createDisponibilitesHebdomadaires(
            @RequestBody DisponibiliteHebdoRequest request) {

        List<DisponibiliteDTO> disponibilites = disponibiliteService.createDisponibilitesHebdomadaires(
                request.getMedecinId(),
                request.getHeureDebut(),
                request.getHeureFin(),
                request.getDureeCreneau(),
                request.getHeurePauseDebut(),
                request.getHeurePauseFin(),
                request.getStartDate()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(disponibilites);
    }




    @PostMapping("/jours")
    public ResponseEntity<List<DisponibiliteDTO>> createDisponibilitesPourPlusieursJours(
            @RequestBody DisponibiliteMultipleRequest request) {

        List<DisponibiliteDTO> disponibilites = disponibiliteService.createDisponibilitesPourJours(
                request.getMedecinId(),
                request.getHeureDebut(),
                request.getHeureFin(),
                request.getDureeCreneau(),
                request.getHeurePauseDebut(),
                request.getHeurePauseFin(),
                request.getDates()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(disponibilites);
    }



    @GetMapping("/semaine/{medecinId}")
    public ResponseEntity<List<DisponibiliteDTO>> getDisponibilitesDeLaSemaine(@PathVariable Long medecinId) {
        List<DisponibiliteDTO> disponibilites = disponibiliteService.findDisponibilitesDeLaSemaine(medecinId);
        return ResponseEntity.ok(disponibilites);
    }

}