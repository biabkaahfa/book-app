package com.example.demo.service;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.enums.Role;
import com.example.demo.model.Medecin;
import com.example.demo.model.Patient;
import com.example.demo.model.Specialite;
import com.example.demo.model.User;
import com.example.demo.repository.SpecialiteRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final SpecialiteRepository specialiteRepository;

    @org.springframework.beans.factory.annotation.Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager,SpecialiteRepository specialiteRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.specialiteRepository = specialiteRepository;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }

        User user;

        switch (request.getRole()) {
            case MEDECIN -> {
                Medecin medecin = new Medecin();
                medecin.setNom(request.getNom());
                medecin.setPrenom(request.getPrenom());
                medecin.setEmail(request.getEmail());
                medecin.setTelephone(request.getTelephone());
                medecin.setPassword(passwordEncoder.encode(request.getPassword()));
                medecin.setRole(Role.MEDECIN);
                medecin.setNumeroLicence(request.getNumeroLicence());
                medecin.setDescription(request.getDescription());

                // Charger la spécialité
                Specialite specialite = specialiteRepository.findById(request.getSpecialiteId())
                        .orElseThrow(() -> new RuntimeException("Spécialité introuvable"));
                medecin.setSpecialite(specialite);

                user = medecin;
            }
            case PATIENT -> {
                Patient patient = new Patient();
                patient.setNom(request.getNom());
                patient.setPrenom(request.getPrenom());
                patient.setEmail(request.getEmail());
                patient.setTelephone(request.getTelephone());
                patient.setPassword(passwordEncoder.encode(request.getPassword()));
                patient.setRole(Role.PATIENT);
                patient.setAdresse(request.getAdresse());
                patient.setDateNaissance(request.getDateNaissance());
                patient.setContactUrgence(request.getContactUrgence());

                user = patient;
            }
            case ADMIN -> {
                // Par défaut, un User simple peut être utilisé
                user = new User();
                user.setNom(request.getNom());
                user.setPrenom(request.getPrenom());
                user.setEmail(request.getEmail());
                user.setTelephone(request.getTelephone());
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setRole(Role.ADMIN);
            }
            default -> throw new RuntimeException("Rôle non supporté");
        }

        userRepository.save(user);


        return new AuthResponse(user.getNom(), user.getPrenom(),user.getRole());
    }


    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        org.springframework.security.core.userdetails.User userDetails =
            (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé après authentification"));
        String token = jwtTokenProvider.generateToken(user);
        return new AuthResponse(token );
    }
} 