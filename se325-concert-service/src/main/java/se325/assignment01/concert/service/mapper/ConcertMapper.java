package se325.assignment01.concert.service.mapper;

import se325.assignment01.concert.common.dto.ConcertDTO;
import se325.assignment01.concert.common.dto.ConcertSummaryDTO;
import se325.assignment01.concert.service.domain.Concert;

public class ConcertMapper {

	private ConcertMapper() {
	}

	public static ConcertDTO toDTO(Concert c) {
		ConcertDTO dto = new ConcertDTO(c.getId(), c.getTitle(), c.getImageName(), c.getBlurb());
		c.getPerformers().forEach(performer -> dto.getPerformers().add(PerformerMapper.toDTO(performer)));
		dto.getDates().addAll(c.getDates());
		return dto;
	}

	public static ConcertSummaryDTO toSummaryDTO(Concert c) {
		return new ConcertSummaryDTO(c.getId(), c.getTitle(), c.getImageName());
	}
}
