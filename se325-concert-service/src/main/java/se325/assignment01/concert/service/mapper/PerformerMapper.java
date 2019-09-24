package se325.assignment01.concert.service.mapper;

import se325.assignment01.concert.common.dto.PerformerDTO;
import se325.assignment01.concert.service.domain.Performer;

public class PerformerMapper {

	private PerformerMapper() {
	}
	
	public static PerformerDTO toDTO(Performer p) {
		return new PerformerDTO(p.getId(), p.getName(), p.getImageName(), p.getGenre(), p.getBlurb());
	}
}
