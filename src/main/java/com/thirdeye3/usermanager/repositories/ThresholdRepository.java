package com.thirdeye3.usermanager.repositories;

import com.thirdeye3.usermanager.entities.Threshold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThresholdRepository extends JpaRepository<Threshold, Long> {
    List<Threshold> findByThresholdGroupId(Long thresholdGroupId);
    void deleteByThresholdGroupId(Long thresholdGroupId);
    List<Threshold> findByThresholdGroupIdIn(List<Long> thresholdGroupIds);
    Long countByThresholdGroupId(Long thresholdGroupId);
}
