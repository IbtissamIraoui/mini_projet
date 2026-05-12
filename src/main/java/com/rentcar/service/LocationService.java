package com.rentcar.service;

import com.rentcar.model.Location;
import com.rentcar.model.Voiture;
import com.rentcar.repository.LocationRepository;
import com.rentcar.repository.VoitureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final VoitureRepository voitureRepository;

    @Autowired
    public LocationService(LocationRepository locationRepository, VoitureRepository voitureRepository) {
        this.locationRepository = locationRepository;
        this.voitureRepository = voitureRepository;
    }

    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public Location getLocationById(Long id) {
        return locationRepository.findById(id).orElse(null);
    }

    public Location saveLocation(Location location) {
        // Calcul automatique du montant total
        long days = ChronoUnit.DAYS.between(location.getDateDebut(), location.getDateFin());
        if (days <= 0) days = 1; // Minimum 1 jour
        
        Voiture voiture = voitureRepository.findById(location.getVoiture().getId()).orElseThrow();
        location.setMontantTotal(days * voiture.getPrixJour());
        
        // Mettre à jour la disponibilité si en cours
        if (location.getStatut() == Location.StatutLocation.EN_COURS) {
            voiture.setDisponible(false);
            voitureRepository.save(voiture);
        } else if (location.getStatut() == Location.StatutLocation.TERMINEE || location.getStatut() == Location.StatutLocation.ANNULEE) {
            voiture.setDisponible(true);
            voitureRepository.save(voiture);
        }
        
        return locationRepository.save(location);
    }
    
    public void updateStatut(Long id, Location.StatutLocation statut) {
        Location location = getLocationById(id);
        if (location != null) {
            location.setStatut(statut);
            saveLocation(location); // Va déclencher la maj de disponibilité
        }
    }

    public void deleteLocation(Long id) {
        Location location = getLocationById(id);
        if (location != null && location.getStatut() == Location.StatutLocation.EN_COURS) {
            Voiture voiture = location.getVoiture();
            voiture.setDisponible(true);
            voitureRepository.save(voiture);
        }
        locationRepository.deleteById(id);
    }
    
    public double calculerRevenuTotal() {
        return locationRepository.findAll().stream()
                .filter(l -> l.getStatut() != Location.StatutLocation.ANNULEE)
                .mapToDouble(Location::getMontantTotal)
                .sum();
    }
    
    public long compterLocationsActives() {
        return locationRepository.findByStatut(Location.StatutLocation.EN_COURS).size();
    }

    public List<Location> getLocationsFiltrees(Location.StatutLocation statut, java.time.LocalDate dateDebut, java.time.LocalDate dateFin) {
        if (statut != null && dateDebut != null && dateFin != null) {
            return locationRepository.findByStatutAndDateDebutBetween(statut, dateDebut, dateFin);
        } else if (dateDebut != null && dateFin != null) {
            return locationRepository.findByDateDebutBetween(dateDebut, dateFin);
        } else if (statut != null) {
            return locationRepository.findByStatut(statut);
        } else {
            return getAllLocations();
        }
    }

    public double calculerTauxOccupation() {
        long totalVoitures = voitureRepository.count();
        if (totalVoitures == 0) return 0.0;
        long voituresLouees = totalVoitures - voitureRepository.findByDisponibleTrue().size();
        return (double) voituresLouees / totalVoitures * 100;
    }

    public List<Object[]> getRevenusParMarqueEtMois() {
        return locationRepository.getRevenusParMarqueEtMois();
    }
}
