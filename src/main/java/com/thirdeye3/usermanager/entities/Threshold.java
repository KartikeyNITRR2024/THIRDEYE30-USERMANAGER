package com.thirdeye3.usermanager.entities;

import com.thirdeye3.usermanager.dtos.ThresholdGroupDto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "THRESHOLD")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Threshold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "THRESHOLD_ID", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "THRESHOLDGROUP_ID", nullable = false)
    private ThresholdGroup thresholdGroup;

    @Column(name = "TIME_GAP_IN_SECONDS")
    private Long timeGapInSeconds;

    @Column(name = "PRICE_GAP")
    private Double priceGap;

    @Column(name = "TYPE")
    private Integer type;
}
