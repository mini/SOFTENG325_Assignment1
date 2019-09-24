package se325.assignment01.concert.service.mapper;

import java.util.ArrayList;
import java.util.List;

import se325.assignment01.concert.common.dto.BookingDTO;
import se325.assignment01.concert.common.dto.SeatDTO;
import se325.assignment01.concert.service.domain.Booking;
import se325.assignment01.concert.service.domain.Seat;

public class BookingMapper {
	private BookingMapper() {
	}

	public static BookingDTO toDTO(Booking b) {
		List<SeatDTO> seats = new ArrayList<>();
		for (Seat s : b.getSeats()) {
			seats.add(SeatMapper.toDTO(s));
		}
		return new BookingDTO(b.getId(), b.getDate(), seats);
	}
}
