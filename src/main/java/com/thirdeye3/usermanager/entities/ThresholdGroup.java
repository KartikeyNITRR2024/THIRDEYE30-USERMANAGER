package com.thirdeye3.usermanager.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "THRESHOLD_GROUP")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ThresholdGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "THRESHOLDGROUP_ID", nullable = false)
    private Long id;
    
    @Column(name = "GROUP_NAME")
    private String groupName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @OneToMany(mappedBy = "thresholdGroup", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TelegramChatId> telegramChatIds;

    @OneToMany(mappedBy = "thresholdGroup", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Threshold> thresholds;

    @Column(name = "ACTIVE", nullable = false)
    private Boolean active;

    @Column(name = "ALL_STOCKS", nullable = false)
    private Boolean allStocks;

    @Column(name = "STOCK_LIST")
    private String stockList;
}
