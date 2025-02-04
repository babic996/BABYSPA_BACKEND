package com.backend.babyspa.v1.models;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Embeddable
public class UserRoleKey implements Serializable {

	@Column(name = "user_id", nullable = false)
	private int userId;

	@Column(name = "role_id", nullable = false)
	private int roleId;

}
