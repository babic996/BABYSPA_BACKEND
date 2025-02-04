package com.backend.babyspa.v1.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "status")
public class Status {

	@Id
	@Column(name = "status_id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int statusId;

	@Column(name = "status_name", nullable = false)
	private String statusName;

	@Column(name = "status_code", nullable = false)
	private String statusCode;

	@ManyToOne
	@JoinColumn(name = "status_type_id", nullable = false)
	private StatusType statusType;

	public Status(String statusName, String statusCode, StatusType statusType) {

		this.statusName = statusName;
		this.statusCode = statusCode;
		this.statusType = statusType;
	}
}
