package ru.otus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ClientDto {
    private long id;
    private String name;
    private AddressDto address;
    private List<PhoneDto> phones;
}
