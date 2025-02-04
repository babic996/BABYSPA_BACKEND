package com.backend.babyspa.v1.projections;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;

public interface LocalDateTimeProjection {

	@Value("#{target.start_date}")
	LocalDateTime getStartDate();
}
