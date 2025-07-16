package com.example.demo.controller;

import com.example.demo.dto.SpecialiteDTO;
import com.example.demo.model.Specialite;
import com.example.demo.service.SpecialiteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/specialites")
public class SpecialiteController {

    @Autowired
    private SpecialiteService specialiteService;

    @GetMapping
    public ResponseEntity<List<Specialite>> getAll() {
        return ResponseEntity.ok(specialiteService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Specialite> getById(@PathVariable Long id) {
        return ResponseEntity.ok(specialiteService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Specialite> create(@Valid @RequestBody SpecialiteDTO dto) {
        return ResponseEntity.ok(specialiteService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Specialite> update(@PathVariable Long id, @Valid @RequestBody SpecialiteDTO dto) {
        return ResponseEntity.ok(specialiteService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        specialiteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Specialite>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(specialiteService.search(keyword));
    }

    @GetMapping("/ordered")
    public ResponseEntity<List<Specialite>> ordered() {
        return ResponseEntity.ok(specialiteService.listAlphabetique());
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(specialiteService.countSpecialites());
    }

//    @GetMapping("/pagination")
//    public ResponseEntity<Page<Specialite>> searchPaginated(
//            @RequestParam(defaultValue = "") String keyword,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
//    ) {
//        return ResponseEntity.ok(specialiteService.searchPaginated(keyword, page, size));
//    }
}
