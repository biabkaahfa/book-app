package com.example.demo.service;

import com.example.demo.dto.MedecinDTO;
import com.example.demo.enums.Role;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Medecin;
import com.example.demo.model.Patient;
import com.example.demo.model.RendezVous;
import com.example.demo.model.User;
import com.example.demo.repository.MedecinRepository;
import com.example.demo.repository.PatientRepository;
import com.example.demo.repository.RendezVousRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

@Autowired
    private MedecinRepository medecinRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RendezVousRepository rendezVousRepository;


    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable avec l'ID : " + id));
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public List<User> getByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public List<User> getActifs() {
        return userRepository.findByActifTrue();
    }

    public List<User> getByRoleAndActif(Role role) {
        return userRepository.findByRoleAndActifTrue(role);
    }

    public List<User> searchByNomOrPrenom(String nom, String prenom) {
        return userRepository.findByNomOrPrenomContaining(nom, prenom);
    }

    public List<User> searchActifUsers(String keyword) {
        return userRepository.searchActiveUsers(keyword);
    }

    public User create(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Un utilisateur avec cet e-mail existe déjà.");
        }
        return userRepository.save(user);
    }

    public User update(Long id, User updatedUser) {
        User existing = getById(id);
        existing.setNom(updatedUser.getNom());
        existing.setPrenom(updatedUser.getPrenom());
        existing.setEmail(updatedUser.getEmail());
        existing.setRole(updatedUser.getRole());
        existing.setActif(updatedUser.isActif());
        existing.setTelephone(updatedUser.getTelephone());
        return userRepository.save(existing);
    }

    public void delete(Long id) {
        User existing = getById(id);
        userRepository.delete(existing);
    }

    public List<Patient> getPatientsByMedecin(Long medecinId) {
        return patientRepository.findPatientsByMedecin(medecinId);
    }


    public MedecinDTO getMedecinWithSpecialite(Long id) {
        Medecin medecin = medecinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin introuvable"));

        MedecinDTO dto = new MedecinDTO();
        dto.setId(medecin.getId());
        dto.setNom(medecin.getNom());
        dto.setPrenom(medecin.getPrenom());
        dto.setEmail(medecin.getEmail());
        dto.setTelephone(medecin.getTelephone());
        dto.setSpecialiteNom(medecin.getSpecialite() != null ? medecin.getSpecialite().getNom() : null);
        return dto;
    }


    public List<RendezVous> getTodayRendezVous(Long medecinId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endOfDay = now.toLocalDate().atTime(23, 59, 59);
        return medecinRepository.findTodayRendezVousByMedecin(medecinId, now, endOfDay);
    }

    public Long getPatientCountForMedecin(Long medecinId) {
        return patientRepository.countDistinctPatientsByMedecin(medecinId);
    }

    public List<MedecinDTO> getActifMedecinsWithSpecialite() {
        return userRepository.findByRoleAndActifTrue(Role.MEDECIN)
                .stream()
                .filter(user -> user instanceof Medecin)
                .map(user -> new MedecinDTO((Medecin) user))
                .collect(Collectors.toList());
    }



}
