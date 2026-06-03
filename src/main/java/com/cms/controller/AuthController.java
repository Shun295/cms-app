package com.cms.controller;

import com.cms.dto.LoginResponseDto;
import com.cms.dto.OfficerReqSignInDto;
import com.cms.dto.OfficerResLoginDto;
import com.cms.dto.TokenDto;
import com.cms.model.User;
import com.cms.service.AuthService;
import com.cms.service.UserService;
import com.cms.utility.JwtUtility;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final JwtUtility jwtUtility;

    @PostMapping("/signIn")
    public void signInOfficer(@RequestBody OfficerReqSignInDto officerReqSignInDto)
    {
        authService.signInOfficer(officerReqSignInDto);
    }

    @GetMapping("/login")
    public TokenDto login(Principal principal){
        String username = principal.getName();
        String token = jwtUtility.generateToken(username);
        return new TokenDto(username,token);
    }

    @GetMapping("/user-details")
    public LoginResponseDto getUserDetails(Principal principal){
        User user = (User)userService.loadUserByUsername(principal.getName());
        return new LoginResponseDto(
                user.getId(),
                user.getUsername(),
                user.getRole().toString()
        );
    }
}
