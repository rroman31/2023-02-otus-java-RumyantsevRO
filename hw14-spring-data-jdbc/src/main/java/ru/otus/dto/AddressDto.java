package ru.otus.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddressDto {
    private Long id;
    private String street;
}
