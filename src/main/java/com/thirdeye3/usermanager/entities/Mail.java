package com.thirdeye3.usermanager.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "MAIL")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Mail {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column(name = "USER_ID", nullable = false)
	private Long userId;
	
    @Column(name = "USER_NAME", nullable = false)
    private String userName;
    
	@Column(name = "MAIL_TYPE", nullable = false)
	private Integer mailType;
	
	@Column(name = "ONE_TIME_PASSWORD", nullable = false)
	private String otp;
	
	@Column(name = "SUCCESS", nullable = false)
	private Boolean success = false;
	
	@Column(name = "NO_OF_TRIES_LEFT", nullable = false)
	private Long noOFTriesLeft;
	
	@Column(name = "EXPIRY_TIME", nullable = false)
	private LocalDateTime expiryTime;
}
