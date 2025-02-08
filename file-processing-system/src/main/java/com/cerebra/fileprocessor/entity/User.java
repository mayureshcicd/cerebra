package com.cerebra.fileprocessor.entity;


import com.cerebra.fileprocessor.audit.AuditEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_email"})})
public class User extends AuditEntity implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_sequence", allocationSize = 1)
    private Long id;

    @Column(name = "first_name",length = 250,nullable = false)
    private String firstName;

    @Column( unique = true, name="user_email",nullable = false,length = 250)
    private String email;

    @Column(nullable = false,length = 500)
    private String password;

    @Column(name = "temp_password",length = 15)
    private String tempPassword;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "user_type", length = 10)
    private String userType;

    @Column(name = "registered_ip_address")
    private  String ipAddress;

    private boolean enabled;

    private boolean verified;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<SimpleGrantedAuthority> authorities = role.getPrivilleges().stream()
                .map(priv -> new SimpleGrantedAuthority(priv.getName())).collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        return authorities;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }


}