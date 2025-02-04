package com.backend.babyspa.v1.models;

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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "status_type")
public class StatusType {

	@Id
	@Column(name = "status_type_id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int statusTypeId;

	@Column(name = "status_type_code", nullable = false)
	private String statusTypeCode;

	public StatusType(String statusTypeCode) {
		this.statusTypeCode = statusTypeCode;
	}
}
