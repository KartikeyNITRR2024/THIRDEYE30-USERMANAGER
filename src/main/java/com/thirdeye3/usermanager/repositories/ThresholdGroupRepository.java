package com.thirdeye3.usermanager.repositories;

import com.thirdeye3.usermanager.entities.ThresholdGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;



@Repository
public interface ThresholdGroupRepository extends JpaRepository<ThresholdGroup, Long> {

    List<ThresholdGroup> findByUserUserId(Long userId);

    void deleteByUserUserId(Long userId);

    List<ThresholdGroup> findByActiveTrue();

    @Query("SELECT COUNT(tg) FROM ThresholdGroup tg WHERE tg.user.userId = :userId AND tg.active = true")
    Long countActiveByUserId(@Param("userId") Long userId);
}
