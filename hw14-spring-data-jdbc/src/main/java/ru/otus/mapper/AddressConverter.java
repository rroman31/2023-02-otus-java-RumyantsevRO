package ru.otus.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.otus.dto.AddressDto;
import ru.otus.model.Address;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AddressConverter {
    AddressDto toDto(Address address);

    Address toModel(AddressDto addressDto);
}
