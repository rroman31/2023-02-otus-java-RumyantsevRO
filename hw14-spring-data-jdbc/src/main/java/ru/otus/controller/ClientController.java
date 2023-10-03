package ru.otus.controller;

import com.electronwill.nightconfig.core.conversion.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.otus.dto.ClientDto;
import ru.otus.mapper.ClientConverter;
import ru.otus.model.Client;
import ru.otus.service.ClientService;

import java.util.List;

@Path("/")
@RestController
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final ClientConverter converter;

    @GetMapping("/clients")
    public List<Client> getClients() {
        return clientService.findAll();
    }

    @GetMapping("/clients/{id}")
    public Client getClientById(@PathVariable Long id) {
        return clientService.findById(id);
    }

    @PostMapping("/clients")
    public ClientDto saveClient(@RequestBody ClientDto client) {
        return converter.modelToDto(clientService.save(converter.dtoToModel(client)));
    }

    @PutMapping("/clients/{id}")
    public ClientDto updateClient(@RequestBody ClientDto client, @PathVariable Long id) {
        return converter.modelToDto(clientService.update(converter.dtoToModel(client), id));
    }

    @DeleteMapping("/clients/{id}")
    public void removeClientById(@PathVariable Long id) {
        clientService.removeById(id);
    }

}
