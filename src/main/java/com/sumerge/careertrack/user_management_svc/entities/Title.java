package com.sumerge.careertrack.user_management_svc.entities;

import com.sumerge.careertrack.user_management_svc.entities.compositeKeys.TitleId;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Title {
    @EmbeddedId
    private TitleId id;

    private boolean isManager;

    public Title(String department, String title) {
        this.id = new TitleId(department, title);
        this.isManager = false;
    }

    public Title(String department, String title, boolean isManager) {
        this(department, title);
        this.isManager = isManager;
    }
}
