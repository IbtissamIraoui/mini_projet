package com.rentcar.service;

import com.rentcar.model.Location;
import com.rentcar.repository.LocationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RappelService {

    private final LocationRepository locationRepository;

    private final Set<Long> locationsEnRetard = ConcurrentHashMap.newKeySet();
    private final Set<Long> locationsExpirentAujourdhui = ConcurrentHashMap.newKeySet();

    @Autowired
    public RappelService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void verifierLocationsExpirees() {
        LocalDate aujourd_hui = LocalDate.now();

        // Locations en retard (dateFin dépassée et toujours EN_COURS)
        List<Location> enRetard = locationRepository.findByStatutAndDateFinLessThan(
                Location.StatutLocation.EN_COURS, aujourd_hui);

        // Locations dont le retour est prévu aujourd'hui
        List<Location> retourAujourdhui = locationRepository.findByStatutAndDateFin(
                Location.StatutLocation.EN_COURS, aujourd_hui);

        locationsEnRetard.clear();
        locationsEnRetard.addAll(enRetard.stream().map(Location::getId).collect(Collectors.toSet()));

        locationsExpirentAujourdhui.clear();
        locationsExpirentAujourdhui.addAll(retourAujourdhui.stream().map(Location::getId).collect(Collectors.toSet()));

        if (!enRetard.isEmpty()) {
            log.warn("⚠ {} location(s) en retard de restitution : IDs {}",
                    enRetard.size(), locationsEnRetard);
        }
        if (!retourAujourdhui.isEmpty()) {
            log.info("📅 {} location(s) à restituer aujourd'hui : IDs {}",
                    retourAujourdhui.size(), locationsExpirentAujourdhui);
        }
        if (enRetard.isEmpty() && retourAujourdhui.isEmpty()) {
            log.info("✅ Aucune location en retard ou expirant aujourd'hui.");
        }
    }

    // Appelé au démarrage pour initialiser sans attendre 8h
    public void verifierAuDemarrage() {
        verifierLocationsExpirees();
    }

    public Set<Long> getLocationsEnRetard() {
        return Collections.unmodifiableSet(locationsEnRetard);
    }

    public Set<Long> getLocationsExpirentAujourdhui() {
        return Collections.unmodifiableSet(locationsExpirentAujourdhui);
    }

    public List<Location> getLocationsEnRetardDetails() {
        if (locationsEnRetard.isEmpty()) return Collections.emptyList();
        LocalDate aujourd_hui = LocalDate.now();
        return locationRepository.findByStatutAndDateFinLessThan(
                Location.StatutLocation.EN_COURS, aujourd_hui);
    }
}
