package com.sumerge.careertrack.user_management_svc.entities;

import com.sumerge.careertrack.user_management_svc.entities.compositeKeys.TitleId;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
public class Title {
    @EmbeddedId
    private TitleId id;

    private boolean isManager;

    @ManyToOne
    @MapsId("departmentId")
    private Department department;

    public Title(Department department, String title) {
        this.id = new TitleId(department.getName(), title);
        this.department = department;
    }

    public Title(Department department, String title, boolean isManager) {
        this(department, title);
        this.isManager = isManager;
    }
}
