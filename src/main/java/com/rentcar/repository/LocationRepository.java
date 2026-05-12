package com.rentcar.repository;

import com.rentcar.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByStatut(Location.StatutLocation statut);

    List<Location> findByDateDebutBetween(LocalDate dateDebut, LocalDate dateFin);

    List<Location> findByStatutAndDateDebutBetween(Location.StatutLocation statut, LocalDate dateDebut, LocalDate dateFin);

    @Query("SELECT l.voiture.marque, FUNCTION('MONTH', l.dateDebut), SUM(l.montantTotal) FROM Location l WHERE l.statut != 'ANNULEE' GROUP BY l.voiture.marque, FUNCTION('MONTH', l.dateDebut)")
    List<Object[]> getRevenusParMarqueEtMois();

    // Historique par client
    List<Location> findByClientIdOrderByDateDebutDesc(Long clientId);

    @Query("SELECT SUM(l.montantTotal) FROM Location l WHERE l.client.id = :id AND l.statut != com.rentcar.model.Location.StatutLocation.ANNULEE")
    Double calculerTotalDepenseParClient(@Param("id") Long clientId);

    // Rappels automatiques
    List<Location> findByStatutAndDateFinLessThan(Location.StatutLocation statut, LocalDate date);

    List<Location> findByStatutAndDateFin(Location.StatutLocation statut, LocalDate date);

    long countByClientIdAndStatut(Long clientId, Location.StatutLocation statut);

    long countByVoitureIdAndStatut(Long voitureId, Location.StatutLocation statut);
}
