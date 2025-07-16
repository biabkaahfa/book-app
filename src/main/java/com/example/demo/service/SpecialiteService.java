package com.example.demo.service;

import com.example.demo.dto.SpecialiteDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Specialite;
import com.example.demo.repository.SpecialiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecialiteService {

    @Autowired
    private SpecialiteRepository specialiteRepository;

    public List<Specialite> getAll() {
        return specialiteRepository.findAll();
    }

    public Specialite getById(Long id) {
        return specialiteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Spécialité non trouvée avec l'ID : " + id));
    }

    public Specialite create(SpecialiteDTO dto) {
        if (specialiteRepository.existsByNom(dto.getNom())) {
            throw new IllegalArgumentException("Une spécialité avec ce nom existe déjà.");
        }
        Specialite specialite = new Specialite();
        specialite.setNom(dto.getNom());
        specialite.setDescription(dto.getDescription());
        return specialiteRepository.save(specialite);
    }

    public Specialite update(Long id, SpecialiteDTO dto) {
        Specialite specialite = getById(id);
        specialite.setNom(dto.getNom());
        specialite.setDescription(dto.getDescription());
        return specialiteRepository.save(specialite);
    }

    public void delete(Long id) {
        Specialite specialite = getById(id);
        specialiteRepository.delete(specialite);
    }

    public List<Specialite> search(String keyword) {
        return specialiteRepository.searchByKeyword(keyword);
    }

    public List<Specialite> listAlphabetique() {
        return specialiteRepository.findAllOrdered();
    }

    public long countSpecialites() {
        return specialiteRepository.countAll();
    }


}
