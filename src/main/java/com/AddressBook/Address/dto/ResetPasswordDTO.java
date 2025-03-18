package com.AddressBook.Address.dto;

import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class ResetPasswordDTO {
    private String token;
    private String newPassword;

    public static ResetPasswordDTO fromEntity(Object obj, ModelMapper modelMapper) {
        return modelMapper.map(obj, ResetPasswordDTO.class);
    }
}
