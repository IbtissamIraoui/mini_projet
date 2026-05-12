package com.rentcar.service;

import com.rentcar.model.Client;
import com.rentcar.model.Location;
import com.rentcar.repository.ClientRepository;
import com.rentcar.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final LocationRepository locationRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository, LocationRepository locationRepository) {
        this.clientRepository = clientRepository;
        this.locationRepository = locationRepository;
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Client getClientById(Long id) {
        return clientRepository.findById(id).orElse(null);
    }

    public Client saveClient(Client client) {
        return clientRepository.save(client);
    }

    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }

    public List<Location> getHistoriqueLocations(Long clientId) {
        return locationRepository.findByClientIdOrderByDateDebutDesc(clientId);
    }

    public double getTotalDepense(Long clientId) {
        Double total = locationRepository.calculerTotalDepenseParClient(clientId);
        return total != null ? total : 0.0;
    }
}
