package com.example.demo.controller;


import com.example.demo.dto.RendezVousRequest;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Medecin;
import com.example.demo.model.Patient;
import com.example.demo.model.RendezVous;
import com.example.demo.repository.MedecinRepository;
import com.example.demo.repository.PatientRepository;
import com.example.demo.repository.RendezVousRepository;
import com.example.demo.service.RendezVousService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rendezvous")
@RequiredArgsConstructor
public class RendezVousController {

    private final RendezVousService rendezVousService;
    private final MedecinRepository medecinRepository;
    private final PatientRepository patientRepository;
    private final RendezVousRepository rendezVousRepository;


    @GetMapping
    public List<RendezVous> getAll() {
        return rendezVousService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RendezVous> getById(@PathVariable Long id) {
        return ResponseEntity.ok(rendezVousService.findById(id));
    }

    @PostMapping
    public ResponseEntity<RendezVous> create(@Valid @RequestBody RendezVousRequest dto) {
        // Conversion manuelle du DTO vers l'entité RendezVous
        RendezVous rendezVous = new RendezVous();
        rendezVous.setDateRendezVous(dto.getDateRendezVous());
        rendezVous.setMotif(dto.getMotif());
        rendezVous.setNotes(dto.getNotes());

        // Chargement des entités Patient et Médecin depuis leurs IDs (assumés dans le DTO)
        Medecin medecin = new Medecin();
        medecin.setId(dto.getMedecinId());
        rendezVous.setMedecin(medecin);

        Patient patient = new Patient();
        patient.setId(dto.getPatientId());
        rendezVous.setPatient(patient);

        RendezVous rv = rendezVousService.create(rendezVous);
        return ResponseEntity.ok(rv);
    }



    @PutMapping("/{id}")
    public ResponseEntity<RendezVous> update(@PathVariable Long id, @Valid @RequestBody RendezVous rendezVous) {
        return ResponseEntity.ok(rendezVousService.update(id, rendezVous));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rendezVousService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/medecin/{medecinId}/rendezvous")
    public List<RendezVous> getRendezVousByMedecin(@PathVariable Long medecinId) {
        return rendezVousService.getRendezVousByMedecin(medecinId);
    }

    @GetMapping("/patient/{id}/futurs")
    public ResponseEntity<List<RendezVous>> getRendezVousFutursPourPatient(@PathVariable Long id) {
        List<RendezVous> rdvs = rendezVousService.getRendezVousFutursPourPatient(id);
        return ResponseEntity.ok(rdvs);
    }

    @GetMapping("/patient/{id}/tous")
    public ResponseEntity<List<RendezVous>> getTousLesRendezVousPourPatient(@PathVariable Long id) {
        List<RendezVous> rdvs = rendezVousService.getTousLesRendezVousPourPatient(id);
        return ResponseEntity.ok(rdvs);
    }



}
