package com.backend.babyspa.v1.models;

import java.math.BigDecimal;

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
@Table(name = "discount")
public class Discount {

	@Id
	@Column(name = "discount_id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int discountId;

	@Column(name = "value", nullable = false)
	private BigDecimal value;

	@Column(name = "is_precentage", nullable = false)
	private boolean isPrecentage;

	@Column(name = "discount_name", nullable = false)
	private String discountName;

	@Column(name = "tenant_id", nullable = false)
	private String tenantId;

	public Discount(BigDecimal value, boolean isPrecentage, String discountName, String tenantId) {
		this.value = value;
		this.isPrecentage = isPrecentage;
		this.discountName = discountName;
		this.tenantId = tenantId;
	}

}
