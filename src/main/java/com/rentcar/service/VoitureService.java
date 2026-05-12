package com.rentcar.service;

import com.rentcar.model.Voiture;
import com.rentcar.repository.VoitureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoitureService {

    private final VoitureRepository voitureRepository;

    @Autowired
    public VoitureService(VoitureRepository voitureRepository) {
        this.voitureRepository = voitureRepository;
    }

    public List<Voiture> getAllVoitures() {
        return voitureRepository.findAll();
    }

    public List<Voiture> getVoituresDisponibles() {
        return voitureRepository.findByDisponibleTrue();
    }

    public List<Voiture> getVoituresDisponiblesParSegment(String segment) {
        if (segment == null || segment.isEmpty()) {
            return getVoituresDisponibles();
        }
        return voitureRepository.findByDisponibleTrueAndSegment(segment);
    }

    public Voiture getVoitureById(Long id) {
        return voitureRepository.findById(id).orElse(null);
    }

    public Voiture saveVoiture(Voiture voiture) {
        return voitureRepository.save(voiture);
    }

    public void deleteVoiture(Long id) {
        voitureRepository.deleteById(id);
    }
}
