package com.AddressBook.Address.dto;

import com.AddressBook.Address.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    public static UserDTO fromEntity(User user, ModelMapper modelMapper) {
        return modelMapper.map(user, UserDTO.class);
    }

    public static User toEntity(UserDTO userDTO, ModelMapper modelMapper) {
        return modelMapper.map(userDTO, User.class);
    }
}
