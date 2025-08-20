package com.backend.babyspa.v1.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.backend.babyspa.v1.exceptions.BuisnessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.backend.babyspa.v1.config.TenantContext;
import com.backend.babyspa.v1.config.UserDetailsServiceImpl;
import com.backend.babyspa.v1.dtos.AddNewTenantUserDto;
import com.backend.babyspa.v1.dtos.AssignRolesDto;
import com.backend.babyspa.v1.dtos.ChangePasswordDto;
import com.backend.babyspa.v1.dtos.LoginDto;
import com.backend.babyspa.v1.dtos.LoginResponseDto;
import com.backend.babyspa.v1.dtos.RegisterNewUserDto;
import com.backend.babyspa.v1.dtos.UpdateUserDto;
import com.backend.babyspa.v1.dtos.UserInfoDto;
import com.backend.babyspa.v1.exceptions.NotFoundException;
import com.backend.babyspa.v1.models.Role;
import com.backend.babyspa.v1.models.User;
import com.backend.babyspa.v1.repositories.UserRepository;
import com.backend.babyspa.v1.utils.JwtUtil;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    RoleService roleService;

    @Autowired
    UserRoleService userRoleService;

    public User findById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Nije pronađen korisnik čiji je ID: " + userId + "!"));
    }

    public UserInfoDto findUserInfoByUserId(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Nije pronađen korisnik čiji je ID: " + userId + "!"));

        return buildUserInfoFromUser(user);
    }

    public List<UserInfoDto> findAllUsers(List<String> excludedRoleNames) {
        List<UserInfoDto> users;

        if (Objects.isNull(excludedRoleNames) || excludedRoleNames.isEmpty()) {
            users = userRepository.findByTenantId(TenantContext.getTenant()).stream().map(this::buildUserInfoFromUser).toList();
        } else {
            users = userRepository.findByTenantId(TenantContext.getTenant()).stream()
                    .map(this::buildUserInfoFromUser)
                    .filter(userInfo -> userInfo.getRoles().stream()
                            .map(Role::getRoleName)
                            .noneMatch(excludedRoleNames::contains))
                    .toList();
        }

        return users;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Nije pronađen korisnik čiji je username: " + username + "!"));
    }

    public User register(RegisterNewUserDto registerNewUserDto, Authentication authentication) {
        boolean hasPermission = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")
                        || authority.getAuthority().equals("ROLE_SUPER_ADMIN"));

        if (!hasPermission) {
            throw new BuisnessException("Ovaj korisnik nema ovlaštenje da kreira naloge.");
        }

        User user = new User();

        if (userRepository.existsByUsername(registerNewUserDto.getUsername())) {
            throw new BadCredentialsException("Username već postoji.");
        }

        if (userRepository.existsByEmail(registerNewUserDto.getEmail())) {
            throw new BadCredentialsException("Email već postoji.");
        }

        user.setEmail(registerNewUserDto.getEmail());
        user.setFirstName(registerNewUserDto.getFirstName());
        user.setLastName(registerNewUserDto.getLastName());
        user.setUsername(registerNewUserDto.getUsername() + "@" + TenantContext.getTenant());
        user.setPassword(passwordEncoder.encode(registerNewUserDto.getPassword()));

        return userRepository.save(user);
    }

    @Transactional
    public User addNewTenantUser(AddNewTenantUserDto addNewTenantUserDto, Authentication authentication) {
        boolean hasPermission = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_SUPER_ADMIN"));
        if (!hasPermission) {
            throw new BuisnessException("Ovaj korisnik nema ovlaštenje da dodaje nove tenante.");
        }

        if (userRepository.existsByUsername(addNewTenantUserDto.getUsername())) {
            throw new BadCredentialsException("Username već postoji.");
        }

        if (userRepository.existsByEmail(addNewTenantUserDto.getEmail())) {
            throw new BadCredentialsException("Email već postoji.");
        }

        User user = new User();

        user.setEmail(addNewTenantUserDto.getEmail());
        user.setFirstName(addNewTenantUserDto.getFirstName());
        user.setLastName(addNewTenantUserDto.getLastName());
        user.setUsername(addNewTenantUserDto.getUsername());
        user.setPassword(passwordEncoder.encode(addNewTenantUserDto.getPassword()));

        userRepository.save(user);

        Role role = roleService.findByRoleName("ROLE_ADMIN");
        AssignRolesDto assignRolesDto = new AssignRolesDto();
        List<Integer> roleIds = new ArrayList<>();

        roleIds.add(role.getRoleId());
        assignRolesDto.setRoleIds(roleIds);
        assignRolesDto.setUserId(user.getUserId());

        userRoleService.assignRolesToUser(assignRolesDto, authentication);

        return user;
    }

    public LoginResponseDto loginUser(LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Pogrešni kredencijali!");
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDto.getUsername());
        String jwt = jwtUtil.generateToken(userDetails);
        return new LoginResponseDto(jwt);
    }

    public String changePassword(ChangePasswordDto changePasswordDto, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Korisnik nije pronađen."));

        if (!passwordEncoder.matches(changePasswordDto.getOldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Stari password nije tačan.");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        userRepository.save(user);

        return "Uspješno ste promijenili password";
    }

    public User updateUser(UpdateUserDto updateUserDto, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Korisnik nije pronađen."));

        if (Objects.nonNull(updateUserDto.getNewPassword())) {
            if (!passwordEncoder.matches(updateUserDto.getOldPassword(), user.getPassword())) {
                throw new BadCredentialsException("Stari password nije tačan.");
            }
            user.setPassword(passwordEncoder.encode(updateUserDto.getNewPassword()));
        }

        if (userRepository.existsByUsernameAndUserIdNot(updateUserDto.getUsername() + user.getTenantId(),
                user.getUserId())) {
            throw new BadCredentialsException("Postoji korisnik sa unijetim username-om.");
        }

        if (userRepository.existsByEmailAndUserIdNot(updateUserDto.getEmail(), user.getUserId())) {
            throw new BadCredentialsException("Postoji korisnik sa unijetim email-om.");
        }

        user.setEmail(updateUserDto.getEmail());
        user.setFirstName(updateUserDto.getFirstName());
        user.setLastName(updateUserDto.getLastName());
        user.setUsername(updateUserDto.getUsername() + "@" + user.getTenantId());

        return userRepository.save(user);

    }

    private UserInfoDto buildUserInfoFromUser(User user) {
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setUserId(user.getUserId());
        userInfoDto.setUsername(user.getUsername());
        userInfoDto.setFirstName(user.getFirstName());
        userInfoDto.setLastName(user.getLastName());
        userInfoDto.setEmail(user.getEmail());
        userInfoDto.setRoles(userRoleService.findByUser(user));

        return userInfoDto;
    }
}
