package ru.otus.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.otus.dto.PhoneDto;
import ru.otus.model.Phone;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PhoneConverter {
    Phone toModel(PhoneDto phoneDto);

    List<Phone> toModel(List<PhoneDto> phoneDtoList);

    PhoneDto toDto(Phone phone);

    List<PhoneDto> toDto(List<Phone> phone);

}
