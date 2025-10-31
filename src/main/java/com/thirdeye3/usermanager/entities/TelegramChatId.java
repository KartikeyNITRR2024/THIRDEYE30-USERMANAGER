package com.thirdeye3.usermanager.entities;

import com.thirdeye3.usermanager.enums.WorkType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TELEGRAM_CHAT_ID")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TelegramChatId {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHAT_ID_PK", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "WORK_TYPE", nullable = false)
    private WorkType workType;

    @Column(name = "CHAT_ID", nullable = false)
    private String chatId;
    
    @Column(name = "CHAT_NAME", nullable = false)
    private String chatName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "THRESHOLDGROUP_ID", nullable = false)
    private ThresholdGroup thresholdGroup;
}
