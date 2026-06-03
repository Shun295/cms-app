package com.cms.service;

import com.cms.dto.OfficerReqSignInDto;
import com.cms.enums.Role;
import com.cms.model.Officer;
import com.cms.model.User;
import com.cms.repository.OfficerRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final OfficerRepository officerRepository;

    public void signInOfficer(OfficerReqSignInDto officerReqSignInDto) {

        //Extract user info :username and password from dto and save user in db
        String username=officerReqSignInDto.username();
        String password=officerReqSignInDto.password();

        Role role=Role.OFFICER;

        //encode the password and assign role
        String encodedPassword=passwordEncoder.encode(password);

        User user=new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setRole(role);

        //save the user in Db
        user=userService.save(user);

        //prepare officer object
        Officer officer=new Officer();
        officer.setName(officerReqSignInDto.name());
        officer.setUser(user);

        //save officer having user in db
        officerRepository.save(officer);

    }
}
