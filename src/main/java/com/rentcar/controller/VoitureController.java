package com.rentcar.controller;

import com.rentcar.model.Voiture;
import com.rentcar.service.VoitureService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/voitures")
public class VoitureController {

    private final VoitureService voitureService;

    @Autowired
    public VoitureController(VoitureService voitureService) {
        this.voitureService = voitureService;
    }

    @GetMapping
    public String listVoitures(@RequestParam(value = "segment", required = false) String segment, Model model) {
        if (segment != null && !segment.isEmpty()) {
            model.addAttribute("voitures", voitureService.getVoituresDisponiblesParSegment(segment));
        } else {
            model.addAttribute("voitures", voitureService.getAllVoitures());
        }
        model.addAttribute("segment", segment);
        return "voitures/index";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("voiture", new Voiture());
        return "voitures/form";
    }

    @PostMapping("/add")
    public String saveVoiture(@Valid @ModelAttribute("voiture") Voiture voiture, BindingResult result) {
        if (result.hasErrors()) {
            return "voitures/form";
        }
        voitureService.saveVoiture(voiture);
        return "redirect:/voitures";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Voiture voiture = voitureService.getVoitureById(id);
        if (voiture == null) {
            return "redirect:/voitures";
        }
        model.addAttribute("voiture", voiture);
        return "voitures/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteVoiture(@PathVariable("id") Long id) {
        voitureService.deleteVoiture(id);
        return "redirect:/voitures";
    }
}
