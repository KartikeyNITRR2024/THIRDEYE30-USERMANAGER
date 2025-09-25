package com.thirdeye3.usermanager.repositories;

import com.thirdeye3.usermanager.entities.TelegramChatId;
import com.thirdeye3.usermanager.enums.WorkType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TelegramChatIdRepository extends JpaRepository<TelegramChatId, Long> {

    List<TelegramChatId> findByThresholdGroupId(Long thresholdGroupId);

    void deleteByThresholdGroupId(Long thresholdGroupId);

    List<TelegramChatId> findByThresholdGroupIdIn(List<Long> thresholdGroupIds);

    List<TelegramChatId> findByThresholdGroupIdAndWorkType(Long thresholdGroupId, WorkType workType);
    
    @Query("SELECT t FROM TelegramChatId t WHERE t.thresholdGroup.active = true")
    List<TelegramChatId> findAllByActiveThresholdGroup();
}
