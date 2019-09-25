package se325.assignment01.concert.service.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * The composite primary key for a Seat
 */
public class SeatKey implements Serializable {
	private static final long serialVersionUID = -5504649016409889372L;
	private String label;
	private LocalDateTime date;

	public SeatKey() {
	}

	public SeatKey(String label, LocalDateTime date) {
		this.label = label;
		this.date = date;
	}

	@Override
	public int hashCode() {
		return Objects.hash(date, label);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof SeatKey)) {
			return false;
		}
		SeatKey other = (SeatKey) obj;
		return Objects.equals(date, other.date) && Objects.equals(label, other.label);
	}
}
