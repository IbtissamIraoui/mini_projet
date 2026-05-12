package com.rentcar.controller;

import com.rentcar.model.Location;
import com.rentcar.service.ClientService;
import com.rentcar.service.LocationService;
import com.rentcar.service.VoitureService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;
    private final VoitureService voitureService;
    private final ClientService clientService;

    @Autowired
    public LocationController(LocationService locationService, VoitureService voitureService, ClientService clientService) {
        this.locationService = locationService;
        this.voitureService = voitureService;
        this.clientService = clientService;
    }

    @GetMapping
    public String listLocations(
            @RequestParam(value = "statut", required = false) Location.StatutLocation statut,
            @RequestParam(value = "dateDebut", required = false) @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd") java.time.LocalDate dateDebut,
            @RequestParam(value = "dateFin", required = false) @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd") java.time.LocalDate dateFin,
            Model model) {
        model.addAttribute("locations", locationService.getLocationsFiltrees(statut, dateDebut, dateFin));
        model.addAttribute("statut", statut);
        model.addAttribute("dateDebut", dateDebut);
        model.addAttribute("dateFin", dateFin);
        return "locations/index";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("location", new Location());
        model.addAttribute("voitures", voitureService.getVoituresDisponibles());
        model.addAttribute("clients", clientService.getAllClients());
        return "locations/form";
    }

    @PostMapping("/add")
    public String saveLocation(@Valid @ModelAttribute("location") Location location, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("voitures", voitureService.getVoituresDisponibles());
            model.addAttribute("clients", clientService.getAllClients());
            return "locations/form";
        }
        locationService.saveLocation(location);
        return "redirect:/locations";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Location location = locationService.getLocationById(id);
        if (location == null) {
            return "redirect:/locations";
        }
        model.addAttribute("location", location);
        model.addAttribute("voitures", voitureService.getAllVoitures());
        model.addAttribute("clients", clientService.getAllClients());
        return "locations/form";
    }

    @PostMapping("/updateStatut/{id}")
    public String updateStatut(@PathVariable("id") Long id, @RequestParam("statut") Location.StatutLocation statut) {
        locationService.updateStatut(id, statut);
        return "redirect:/locations";
    }

    @GetMapping("/delete/{id}")
    public String deleteLocation(@PathVariable("id") Long id) {
        locationService.deleteLocation(id);
        return "redirect:/locations";
    }
}
