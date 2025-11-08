package com.thirdeye3.usermanager.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.thirdeye3.usermanager.entities.Mail;

@Repository
public interface MailRepository extends CrudRepository<Mail, Long> {

    List<Mail> findAllBySuccessFalseAndNoOFTriesLeftGreaterThan(Integer noOFTriesLeft);

    @Transactional
    @Modifying
    @Query("DELETE FROM Mail m WHERE m.success = true OR m.noOFTriesLeft = 0")
    void deleteAllSuccessOrZeroTries();

    @Query("SELECT m FROM Mail m WHERE m.userId = :userId AND m.otp = :otp AND m.expiryTime > :currentTime")
    Optional<Mail> verifyOtp(Long userId, String otp, LocalDateTime currentTime);

    @Query("SELECT m FROM Mail m WHERE m.userId = :userId AND m.mailType = :type AND m.expiryTime > :currentTime AND m.noOFTriesLeft > 0")
    Optional<Mail> checkOtp(Long userId, Integer type, LocalDateTime currentTime);

    @Transactional
    @Modifying
    @Query("UPDATE Mail m SET m.expiryTime = :newExpiryTime WHERE m.id = :id")
    void updateExpiryTime(Long id, LocalDateTime newExpiryTime);

    @Transactional
    @Modifying
    @Query("UPDATE Mail m SET m.success = true WHERE m.id = :id")
    void markSuccess(Long id);

    @Transactional
    @Modifying
    @Query("UPDATE Mail m SET m.noOFTriesLeft = m.noOFTriesLeft - 1 WHERE m.id = :id")
    void decreaseTries(Long id);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM Mail m WHERE m.success = true OR m.noOFTriesLeft <= 0 OR m.expiryTime < :currentTime")
    void deleteExpiredOrFailed(LocalDateTime currentTime);

}
