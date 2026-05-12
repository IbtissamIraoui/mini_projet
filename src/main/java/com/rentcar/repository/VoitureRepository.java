package com.rentcar.repository;

import com.rentcar.model.Voiture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoitureRepository extends JpaRepository<Voiture, Long> {
    List<Voiture> findByDisponibleTrue();
    List<Voiture> findByDisponibleTrueAndSegment(String segment);
}
