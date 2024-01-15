package com.hotnslicy.finalproject.domain;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class RefreshToken {

	private Integer id;
	private User user;
	private String refreshToken;
	private Date expirationDate;

	public RefreshToken() {}
	public RefreshToken(User user, String refreshToken, Date expirationDate) {
		super();
		this.user = user;
		this.refreshToken = refreshToken;
		this.expirationDate = expirationDate;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@OneToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

}
