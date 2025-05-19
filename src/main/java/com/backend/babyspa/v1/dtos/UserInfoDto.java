package com.backend.babyspa.v1.dtos;

import com.backend.babyspa.v1.models.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDto {

    private int userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    List<Role> roles;
}
