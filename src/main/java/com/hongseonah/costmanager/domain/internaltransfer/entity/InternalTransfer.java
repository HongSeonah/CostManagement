package com.hongseonah.costmanager.domain.internaltransfer.entity;

import com.hongseonah.costmanager.domain.businessunit.entity.BusinessUnit;
import com.hongseonah.costmanager.domain.project.entity.CostProject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "internal_transfers")
public class InternalTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String transferCode;

    @Column(nullable = false)
    private LocalDate transferDate;

    @ManyToOne
    @JoinColumn(name = "source_business_unit_id", nullable = false)
    private BusinessUnit sourceBusinessUnit;

    @ManyToOne
    @JoinColumn(name = "target_business_unit_id", nullable = false)
    private BusinessUnit targetBusinessUnit;

    @ManyToOne
    @JoinColumn(name = "source_project_id")
    private CostProject sourceProject;

    @ManyToOne
    @JoinColumn(name = "target_project_id")
    private CostProject targetProject;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

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
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
