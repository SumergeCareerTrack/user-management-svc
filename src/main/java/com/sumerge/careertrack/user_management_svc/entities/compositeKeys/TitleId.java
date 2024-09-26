package com.sumerge.careertrack.user_management_svc.entities.compositeKeys;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TitleId implements Serializable {
    private String department;
    private String name;
}
