package se325.assignment01.concert.service.mapper;

import java.time.LocalDateTime;

import se325.assignment01.concert.common.dto.SeatDTO;
import se325.assignment01.concert.service.domain.Seat;

public class SeatMapper {
	public static SeatDTO toDTO(Seat s) {
		return new SeatDTO(s.getLabel(), s.getPrice());
	}

	public static Seat toStd(SeatDTO dto, boolean isBooked, LocalDateTime date) {
		return new Seat(dto.getLabel(), isBooked, date, dto.getPrice());
	}
}
