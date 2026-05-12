package com.rentcar.controller;

import com.rentcar.model.Client;
import com.rentcar.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public String listClients(Model model) {
        model.addAttribute("clients", clientService.getAllClients());
        return "clients/index";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("client", new Client());
        return "clients/form";
    }

    @PostMapping("/add")
    public String saveClient(@Valid @ModelAttribute("client") Client client, BindingResult result) {
        if (result.hasErrors()) {
            return "clients/form";
        }
        clientService.saveClient(client);
        return "redirect:/clients";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Client client = clientService.getClientById(id);
        if (client == null) {
            return "redirect:/clients";
        }
        model.addAttribute("client", client);
        return "clients/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteClient(@PathVariable("id") Long id) {
        clientService.deleteClient(id);
        return "redirect:/clients";
    }
    @GetMapping("/{id}/historique")
    public String historique(@PathVariable("id") Long id, Model model) {
        Client client = clientService.getClientById(id);
        if (client == null) {
            return "redirect:/clients";
        }
        model.addAttribute("client", client);
        model.addAttribute("locations", clientService.getHistoriqueLocations(id));
        model.addAttribute("totalDepense", clientService.getTotalDepense(id));
        return "clients/historique";
    }



}
