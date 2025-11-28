package com.backend.babyspa.v1.controllers;

import com.backend.babyspa.v1.services.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.babyspa.v1.dtos.AddNewTenantUserDto;
import com.backend.babyspa.v1.dtos.AssignRolesDto;
import com.backend.babyspa.v1.dtos.ChangePasswordDto;
import com.backend.babyspa.v1.dtos.LoginDto;
import com.backend.babyspa.v1.dtos.LoginResponseDto;
import com.backend.babyspa.v1.dtos.RegisterNewUserDto;
import com.backend.babyspa.v1.dtos.UpdateUserDto;
import com.backend.babyspa.v1.dtos.UserInfoDto;
import com.backend.babyspa.v1.models.User;
import com.backend.babyspa.v1.services.UserService;
import com.backend.babyspa.v1.utils.ApiResponse;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UserRoleService userRoleService;

    @GetMapping("/find-by-id")
    public ResponseEntity<ApiResponse<User>> findById(@RequestParam int userId) {

        return ResponseEntity.ok(ApiResponse.success(userService.findById(userId)));
    }

    @GetMapping("/find-user-info-by-id")
    public ResponseEntity<ApiResponse<UserInfoDto>> findUserInfoByUserId(@RequestParam int userId) {

        return ResponseEntity.ok(ApiResponse.success(userService.findUserInfoByUserId(userId)));
    }

    @GetMapping("/find-all-user-info")
    public ResponseEntity<ApiResponse<List<UserInfoDto>>> findUserInfoByUserId(@RequestParam(required = false) List<String> excludedRoleNames) {

        return ResponseEntity.ok(ApiResponse.success(userService.findAllUsers(excludedRoleNames)));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@RequestBody @Valid RegisterNewUserDto registerNewUserDto,
                                                      Authentication authentication) {

    
        return ResponseEntity.ok(ApiResponse.success(userService.register(registerNewUserDto, authentication)));
    }

    @PostMapping("/add-new-tenant")
    public ResponseEntity<ApiResponse<User>> addNewTenant(@RequestBody AddNewTenantUserDto addNewTenantUserDto,
                                                          Authentication authentication) {

        
        return ResponseEntity.ok(ApiResponse.success(userService.addNewTenantUser(addNewTenantUserDto, authentication)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@RequestBody @Valid LoginDto loginDto) {

 
        return ResponseEntity.ok(ApiResponse.success(userService.loginUser(loginDto)));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(@RequestBody ChangePasswordDto changePasswordDto,
                                                              Authentication authentication) {

        return ResponseEntity.ok(ApiResponse.success(userService.changePassword(changePasswordDto, authentication)));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<User>> update(@RequestBody @Valid UpdateUserDto updateUserDto,
                                                    Authentication authentication) {

        return ResponseEntity.ok(ApiResponse.success(userService.updateUser(updateUserDto, authentication)));
    }

    @PutMapping("/assign-roles")
    public ResponseEntity<ApiResponse<String>> assignRoles(@RequestBody AssignRolesDto assignRolesDto,
                                                           Authentication authentication) {

        return ResponseEntity.ok(ApiResponse.success(userRoleService.assignRolesToUser(assignRolesDto, authentication)));
    }
}
