package com.sumerge.careertrack.user_management_svc.entities;

import java.util.UUID;

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
public class AppUser {
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

}
