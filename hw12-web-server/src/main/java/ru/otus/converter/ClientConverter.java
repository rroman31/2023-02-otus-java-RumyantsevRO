package ru.otus.converter;

import ru.otus.data.crm.model.Address;
import ru.otus.data.crm.model.Client;
import ru.otus.data.crm.model.Phone;
import ru.otus.dto.AddressDto;
import ru.otus.dto.ClientDto;
import ru.otus.dto.PhoneDto;

import java.util.List;

public class ClientConverter {
    public List<Phone> convertToModel(List<PhoneDto> phones) {
        return phones.stream().map(this::convertToModel).toList();
    }

    public Phone convertToModel(PhoneDto phone) {
        return new Phone(phone.getNumber());
    }

    public Address convertToModel(AddressDto address) {
        return new Address(address.getStreet());
    }


    public Client convertToModel(ClientDto client) {
        return new Client(null, client.getName(), convertToModel(client.getAddress()), convertToModel(client.getPhones()));
    }

    public ClientDto convertToDto(Client client) {
        return ClientDto.builder().id(client.getId()).name(client.getName()).address(convertToDto(client.getAddress())).phones(convertToDto(client.getPhones())).build();
    }

    public AddressDto convertToDto(Address address) {
        return AddressDto.builder().street(address.getStreet()).build();
    }

    public PhoneDto convertToDto(Phone phone) {
        return PhoneDto.builder().number(phone.getNumber()).build();
    }

    public List<PhoneDto> convertToDto(List<Phone> phones) {
        return phones.stream().map(this::convertToDto).toList();
    }
}
