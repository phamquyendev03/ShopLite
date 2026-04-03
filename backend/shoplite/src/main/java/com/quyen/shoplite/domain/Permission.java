package com.quyen.shoplite.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "api_path", nullable = false, length = 200)
    private String apiPath;

    @Column(nullable = false, length = 10)
    private String method;

    @Column(nullable = false, length = 100)
    private String module;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "permissions")
    @Builder.Default
    private List<Role> roles = new ArrayList<>();
}
