package ru.otus.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.model.Client;
import ru.otus.repository.ClientRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;

    @Override
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @Override
    public Client findById(long id) {
        return clientRepository.findById(id).orElseThrow(() -> new RuntimeException(String.format("Element with id:{} not found", id)));
    }

    @Override
    @Transactional
    public Client save(Client client) {
        return clientRepository.save(client);
    }

    @Override
    public Client update(Client client, long id) {
        var newClient = new Client(id, client.getName(), client.getAddress(), client.getPhone());
        return clientRepository.save(newClient);
    }

    @Override
    public void removeById(long id) {
        clientRepository.deleteById(id);
    }
}
