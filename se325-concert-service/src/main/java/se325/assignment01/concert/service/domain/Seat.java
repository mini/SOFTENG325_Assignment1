package se325.assignment01.concert.service.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity(name = "SEATS")
@IdClass(SeatKey.class)
public class Seat {

	@Id
	private String label;
	private boolean isBooked;
	@Id
	private LocalDateTime date;
	@Id
	private BigDecimal price;

	public Seat() {}

	public Seat(String label, boolean isBooked, LocalDateTime date, BigDecimal price) {
		this.label = label;
		this.isBooked = isBooked;
		this.date = date;
		this.price = price;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Boolean getIsBooked() {
		return isBooked;
	}

	public void setIsBooked(Boolean isBooked) {
		this.isBooked = isBooked;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	@Override
	public int hashCode() {
		return Objects.hash(date, label, price);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Seat)) {
			return false;
		}
		Seat other = (Seat) obj;
		return Objects.equals(date, other.date) && Objects.equals(label, other.label) && Objects.equals(price, other.price);
	}
	
}


class SeatKey implements Serializable{
    private String label;
    private LocalDateTime date;
    private BigDecimal price;
    
	@Override
	public int hashCode() {
		return Objects.hash(date, label, price);
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
		return Objects.equals(date, other.date) && Objects.equals(label, other.label) && Objects.equals(price, other.price);
	}
   
}