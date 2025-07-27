package com.hospital.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Hospital implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    private String phoneNumber;

    private String email;

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Doctor> doctors = new HashSet<>();

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Department> departments = new HashSet<>();

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Block> blocks = new HashSet<>();

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hospital hospital)) return false;
        return id != null && Objects.equals(id, hospital.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
