package se325.assignment01.concert.service.mapper;

import java.util.ArrayList;
import java.util.List;

import se325.assignment01.concert.common.dto.BookingDTO;
import se325.assignment01.concert.common.dto.SeatDTO;
import se325.assignment01.concert.service.domain.Booking;

public class BookingMapper {
	private BookingMapper() {
	}

	/**
	 * Maps a domain model booking to its relative DTO class
	 */
	public static BookingDTO toDTO(Booking b) {
		List<SeatDTO> seats = new ArrayList<>();
		b.getSeats().forEach(seat -> seats.add(SeatMapper.toDTO(seat)));
		return new BookingDTO(b.getId(), b.getDate(), seats);
	}
}
