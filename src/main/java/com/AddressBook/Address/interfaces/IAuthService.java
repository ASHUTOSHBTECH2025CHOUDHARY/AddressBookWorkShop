package com.AddressBook.Address.interfaces;

import com.AddressBook.Address.dto.LoginDTO;
import com.AddressBook.Address.dto.ResetPasswordDTO;
import com.AddressBook.Address.dto.UserDTO;

public interface IAuthService {
    String registerUser(UserDTO userDTO);
    String resetPassword(ResetPasswordDTO resetPasswordDTO);
    String forgotPassword(String email);
    String loginUser(LoginDTO loginDTO);
}
