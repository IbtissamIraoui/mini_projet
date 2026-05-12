package com.rentcar.controller;

import com.rentcar.service.ClientService;
import com.rentcar.service.LocationService;
import com.rentcar.service.RappelService;
import com.rentcar.service.VoitureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final VoitureService voitureService;
    private final ClientService clientService;
    private final LocationService locationService;
    private final RappelService rappelService;

    @Autowired
    public DashboardController(VoitureService voitureService, ClientService clientService,
                                LocationService locationService, RappelService rappelService) {
        this.voitureService = voitureService;
        this.clientService = clientService;
        this.locationService = locationService;
        this.rappelService = rappelService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("totalVoitures", voitureService.getAllVoitures().size());
        model.addAttribute("totalClients", clientService.getAllClients().size());
        model.addAttribute("totalLocations", locationService.getAllLocations().size());
        model.addAttribute("revenuTotal", locationService.calculerRevenuTotal());
        model.addAttribute("locationsActives", locationService.compterLocationsActives());
        model.addAttribute("tauxOccupation", locationService.calculerTauxOccupation());
        model.addAttribute("revenusParMarqueEtMois", locationService.getRevenusParMarqueEtMois());

        // Rappels
        rappelService.verifierAuDemarrage();
        model.addAttribute("locationsEnRetardDetails", rappelService.getLocationsEnRetardDetails());
        model.addAttribute("nbRetards", rappelService.getLocationsEnRetard().size());
        model.addAttribute("nbExpirentAujourdhui", rappelService.getLocationsExpirentAujourdhui().size());

        return "dashboard";
    }
}
