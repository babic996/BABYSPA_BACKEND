package com.backend.babyspa.v1.models;

import java.time.LocalDate;

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
@Table(name = "reservation_daily_report")
public class ReservationDailyReport {

	@Id
	@Column(name = "reservation_daily_report_id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int reservationDailyReportId;

	@Column(name = "number_of_reservation", nullable = false)
	private int numberOfReservation;

	@Column(name = "date", nullable = false)
	private LocalDate date;

	@ManyToOne
	@JoinColumn(name = "baby_id", nullable = false)
	private Baby baby;

	@Column(name = "tenant_id", nullable = false)
	private String tenantId;

	@ManyToOne
	@JoinColumn(name = "status_id", nullable = true)
	private Status status;

}
