package se325.assignment01.concert.service.mapper;

import se325.assignment01.concert.common.dto.ConcertDTO;
import se325.assignment01.concert.common.dto.ConcertSummaryDTO;
import se325.assignment01.concert.common.dto.PerformerDTO;
import se325.assignment01.concert.service.domain.Concert;
import se325.assignment01.concert.service.domain.Performer;

public class ConcertMapper {

	private ConcertMapper() {
	}

	public static ConcertDTO toDTO(Concert c) {
		ConcertDTO dto = new ConcertDTO(c.getId(), c.getTitle(), c.getImageName(), c.getBlurb());
		for (Performer p : c.getPerformers()) {
			dto.getPerformers().add(PerformerMapper.toDTO(p));
		}
		dto.getDates().addAll(c.getDates());

		return dto;
	}

	public static ConcertSummaryDTO toSummaryDTO(Concert c) {
		return new ConcertSummaryDTO(c.getId(), c.getTitle(), c.getImageName());
	}

	public static Concert toStd(ConcertDTO dto) {
		Concert c = new Concert(dto.getId(), dto.getTitle(), dto.getImageName(), dto.getBlurb());
		for (PerformerDTO p : dto.getPerformers()) {
			c.getPerformers().add(PerformerMapper.toStd(p));
		}
		c.getDates().addAll(dto.getDates());

		return c;
	}

}
