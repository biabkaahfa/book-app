package com.example.demo.controller;

import com.example.demo.dto.MedecinDTO;
import com.example.demo.enums.Role;
import com.example.demo.model.Patient;
import com.example.demo.model.RendezVous;
import com.example.demo.model.User;
import com.example.demo.service.RendezVousService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    private RendezVousService rendezVousService;

    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @Valid @RequestBody User user) {
        return userService.update(id, user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

    @GetMapping("/role/{role}")
    public List<User> getByRole(@PathVariable Role role) {
        return userService.getByRole(role);
    }

    @GetMapping("/actifs")
    public List<User> getActifs() {
        return userService.getActifs();
    }

    @GetMapping("/actifs/role/{role}")
    public List<User> getActifsByRole(@PathVariable Role role) {
        return userService.getByRoleAndActif(role);
    }

    @GetMapping("/search")
    public List<User> searchByNomOrPrenom(
            @RequestParam String nom,
            @RequestParam String prenom) {
        return userService.searchByNomOrPrenom(nom, prenom);
    }

    @GetMapping("/search/actifs")
    public List<User> searchActifs(@RequestParam String keyword) {
        return userService.searchActifUsers(keyword);
    }

    @GetMapping("/medecin/{id}/patients")
    public ResponseEntity<List<Patient>> getPatientsByMedecin(@PathVariable Long id) {
        List<Patient> patients = userService.getPatientsByMedecin(id);
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/medecin/{id}/specialite")
    public ResponseEntity<MedecinDTO> getMedecinAvecSpecialite(@PathVariable Long id) {
        MedecinDTO medecinDTO = userService.getMedecinWithSpecialite(id);
        return ResponseEntity.ok(medecinDTO);
    }

    @GetMapping("/medecin/{id}/rendezvous/aujourdhui")
    public ResponseEntity<List<RendezVous>> getTodayRdv(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getTodayRendezVous(id));
    }

    @GetMapping("/medecin/{id}/patients/count")
    public ResponseEntity<Long> countPatients(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getPatientCountForMedecin(id));
    }






}
