package com.AddressBook.Address.dto;

import com.AddressBook.Address.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    private String email;
    private String password;

    public static LoginDTO fromEntity(User user, ModelMapper modelMapper) {
        return modelMapper.map(user, LoginDTO.class);
    }
}
