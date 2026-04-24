package com.hongseonah.costmanager.domain.standardcost.entity;

import com.hongseonah.costmanager.domain.businessunit.entity.BusinessUnit;
import com.hongseonah.costmanager.domain.project.entity.CostProject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "standard_cost_plans")
public class StandardCostPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String standardCode;

    @Column(nullable = false, length = 7)
    private String planMonth;

    @ManyToOne
    @JoinColumn(name = "business_unit_id", nullable = false)
    private BusinessUnit businessUnit;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private CostProject project;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StandardCostBasisType basisType = StandardCostBasisType.BUSINESS_UNIT;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal standardAmount = BigDecimal.ZERO;

    @Column(length = 500)
    private String memo;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        if (standardAmount == null) {
            standardAmount = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
