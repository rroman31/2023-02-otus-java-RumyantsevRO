package ru.otus.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.otus.dto.ClientDto;
import ru.otus.model.Client;

@Mapper(componentModel = "spring", uses = {AddressConverter.class, PhoneConverter.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientConverter {
    public Client dtoToModel(ClientDto source);

    public ClientDto modelToDto(Client client);
}
