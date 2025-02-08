package com.cerebra.fileprocessor.data;

import com.cerebra.fileprocessor.common.PasswordGenerator;
import com.cerebra.fileprocessor.config.ConfigProperties;
import com.cerebra.fileprocessor.entity.Privilege;
import com.cerebra.fileprocessor.entity.Role;
import com.cerebra.fileprocessor.entity.User;
import com.cerebra.fileprocessor.repository.PrivilegeRepository;
import com.cerebra.fileprocessor.repository.RoleRepository;
import com.cerebra.fileprocessor.repository.UserRepository;
import com.cerebra.fileprocessor.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class SelfRegistration implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;
    private final ConfigProperties configProperties;
    private final EmailService emailService;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (privilegeRepository.count() == 0) {
            List<Privilege> privileges = List.of(
                    Privilege.builder().id(1L).name("INSERT").build(),
                    Privilege.builder().id(2L).name("UPDATE").build(),
                    Privilege.builder().id(3L).name("DELETE").build(),
                    Privilege.builder().id(4L).name("VIEW").build(),
                    Privilege.builder().id(5L).name("UPLOAD").build()
            );

            privilegeRepository.saveAll(privileges);
        }
        if (roleRepository.count() == 0) {
            List<Long> ADMIN_PRIVILEGES = List.of(1L, 2L, 3L, 4L,5L);
            List<Long> USER_PRIVILEGES = List.of(1L, 2L,4L,5L );

            Map<Long, Privilege> privilegeMap = privilegeRepository.findAllById(
                    List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L,8L,9L)
            ).stream().collect(Collectors.toMap(Privilege::getId, privilege -> privilege));

            List<Role> roles = List.of(
                    Role.builder()
                            .id(1L)
                            .name("ADMIN")
                            .privilleges(ADMIN_PRIVILEGES.stream().map(privilegeMap::get).collect(Collectors.toList()))
                            .build(),

                    Role.builder()
                            .id(2L)
                            .name("USER")
                            .privilleges(USER_PRIVILEGES.stream().map(privilegeMap::get).collect(Collectors.toList()))
                            .build()
            );

            roleRepository.saveAll(roles);

        }

        if (userRepository.count() == 0) {
            String password = PasswordGenerator.generateRandomPassword();
            User user = userRepository.save(User
                    .builder()
                    .firstName("Admin")
                    .email(configProperties.getUserEmail())
                    .password(passwordEncoder.encode(password))
                    .tempPassword(password)
                    .role(roleRepository.findById(1L).orElse(null))
                    .enabled(true)
                    .verified(true)
                    .userType("ADMIN")
                    .build());
            emailService.sendEmailLoginCredentials(user.getFirstName(), user.getEmail(), password, user.getEmail(),null);
        }
    }
}
