package se325.assignment01.concert.service.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * A Concert with its Dates and Performers
 */
@Entity
@Table(name = "CONCERTS")
public class Concert {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String title;

	@Column(name = "IMAGE_NAME")
	private String imageName;

	@Column(columnDefinition = "TEXT")
	private String blurb;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "CONCERT_DATES", joinColumns = @JoinColumn(name = "CONCERT_ID"))
	@Column(name = "DATE")
	private Set<LocalDateTime> dates = new HashSet<>();

	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable(name = "CONCERT_PERFORMER", joinColumns = @JoinColumn(name = "CONCERT_ID"), inverseJoinColumns = @JoinColumn(name = "PERFORMER_ID"))
	@Fetch(FetchMode.SUBSELECT)
	private Set<Performer> performers = new HashSet<>();

	public Concert() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getBlurb() {
		return blurb;
	}

	public void setBlurb(String blurb) {
		this.blurb = blurb;
	}

	public Set<LocalDateTime> getDates() {
		return dates;
	}

	public void setDates(Set<LocalDateTime> dates) {
		this.dates = dates;
	}

	public Set<Performer> getPerformers() {
		return performers;
	}

	public void setPerformers(Set<Performer> performers) {
		this.performers = performers;
	}

	@Override
	public int hashCode() {
		return Objects.hash(dates, id, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Concert)) {
			return false;
		}
		Concert other = (Concert) obj;
		return Objects.equals(dates, other.dates) && Objects.equals(id, other.id) && Objects.equals(title, other.title);
	}

}
