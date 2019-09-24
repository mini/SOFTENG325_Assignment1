package se325.assignment01.concert.service.mapper;

import se325.assignment01.concert.common.dto.SeatDTO;
import se325.assignment01.concert.service.domain.Seat;

public class SeatMapper {

	private SeatMapper() {
	}

	public static SeatDTO toDTO(Seat s) {
		return new SeatDTO(s.getLabel(), s.getPrice());
	}
}
