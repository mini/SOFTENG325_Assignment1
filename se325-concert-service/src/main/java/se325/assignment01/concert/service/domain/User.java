package se325.assignment01.concert.service.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity(name = "USERS")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Version
	private Long version;

	@Column(unique = true)
	private String username;
	private String password;

	@Column(unique = true)
	private UUID sessionId;

	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable(joinColumns = @JoinColumn(name = "USER_ID"), inverseJoinColumns = { @JoinColumn(name = "SEAT_LABEL"),
	        @JoinColumn(name = "SEAT_DATE") })
	private Set<Seat> bookings = new HashSet<>();

	public User() {
	}

	public User(Long id, Long version, String username, String password) {
		this.id = id;
		this.version = version;
		this.username = username;
		this.password = password;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UUID getSessionId() {
		return sessionId;
	}

	public void setSessionId(UUID sessionId) {
		this.sessionId = sessionId;
	}

	public Set<Seat> getBookings() {
		return bookings;
	}

	public void setBookings(Set<Seat> bookings) {
		this.bookings = bookings;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass())
			return false;

		User user = (User) o;
		return new EqualsBuilder().append(username, user.username).append(password, user.password).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(username).append(password).toHashCode();
	}
}
