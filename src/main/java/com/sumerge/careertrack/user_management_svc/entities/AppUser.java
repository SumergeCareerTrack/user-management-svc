package com.sumerge.careertrack.user_management_svc.entities;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppUser implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @lombok.NonNull
    @Column(nullable = false)
    private String firstName;

    @lombok.NonNull
    @Column(nullable = false)
    private String lastName;

    @lombok.NonNull
    @Column(nullable = false)
    private String email;

    @lombok.NonNull
    @Column(nullable = false)
    private String password;

    @ManyToOne
    private AppUser manager;

    @ManyToOne
    @JoinColumn(name = "department", nullable = false)
    @JoinColumn(name = "title", nullable = false)
    private Title title;

    //TODO: might change it later to use an ENUM for different roles (To be reviewed)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("EMPLOYEE"));
    }

    @Override
    public String getUsername() {
        return email;
    }

}
