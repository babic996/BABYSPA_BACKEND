package com.backend.babyspa.v1.projections;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;

public interface LocalDateProjection {

	@Value("#{target.date}")
	LocalDate getDate();
}
