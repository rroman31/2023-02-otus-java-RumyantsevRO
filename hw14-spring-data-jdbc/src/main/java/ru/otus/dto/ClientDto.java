package ru.otus.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ClientDto {
    private Long id;
    private String name;
    private AddressDto address;
    private Set<PhoneDto> phone;
}
