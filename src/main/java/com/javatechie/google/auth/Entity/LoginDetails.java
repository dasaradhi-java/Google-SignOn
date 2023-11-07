package com.javatechie.google.auth.Entity;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "login_details")
public class LoginDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "complete_gmail")
    private String completeGmail;

    @Column(name = "login_time")
    private LocalDateTime loginTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCompleteGmail() {
		return completeGmail;
	}

	public void setCompleteGmail(String completeGmail) {
		this.completeGmail = completeGmail;
	}

	public LocalDateTime getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(LocalDateTime loginTime) {
		this.loginTime = loginTime;
	}

    
}
