package com.example.demo.repository;

import com.example.demo.enums.Role;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);


    List<User> findByRole(Role role);

    List<User> findByActifTrue();

    List<User> findByRoleAndActifTrue(Role role);


    @Query("SELECT u FROM User u WHERE u.nom LIKE %:nom% OR u.prenom LIKE %:prenom%")
    List<User> findByNomOrPrenomContaining(String nom, String prenom);

    @Query("SELECT u FROM User u WHERE u.actif = true AND (u.nom LIKE %:search% OR u.prenom LIKE %:search% OR u.email LIKE %:search%)")
    List<User> searchActiveUsers(String search);
} 