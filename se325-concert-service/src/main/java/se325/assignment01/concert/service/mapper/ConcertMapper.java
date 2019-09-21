package se325.assignment01.concert.service.mapper;

import se325.assignment01.concert.common.dto.ConcertDTO;
import se325.assignment01.concert.common.dto.PerformerDTO;
import se325.assignment01.concert.service.domain.Concert;
import se325.assignment01.concert.service.domain.Performer;

public class ConcertMapper {

	private ConcertMapper() {
	}

	public static ConcertDTO convert(Concert c) {
		ConcertDTO dto = new ConcertDTO(c.getId(), c.getTitle(), c.getImageName(), c.getBlurb());
		for (Performer p : c.getPerformers()) {
			dto.getPerformers().add(PerformerMapper.convert(p));
		}
		dto.getDates().addAll(c.getDates());

		return dto;
	}

	public static Concert convert(ConcertDTO dto) {
		Concert c = new Concert(dto.getId(), dto.getTitle(), dto.getImageName(), dto.getBlurb());
		for (PerformerDTO p : dto.getPerformers()) {
			c.getPerformers().add(PerformerMapper.convert(p));
		}
		c.getDates().addAll(dto.getDates());

		return c;
	}

}
