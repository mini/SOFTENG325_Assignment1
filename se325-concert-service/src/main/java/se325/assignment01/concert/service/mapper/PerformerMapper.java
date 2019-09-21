package se325.assignment01.concert.service.mapper;

import se325.assignment01.concert.common.dto.PerformerDTO;
import se325.assignment01.concert.service.domain.Performer;

public class PerformerMapper {

	private PerformerMapper() {
	}
	
	public static PerformerDTO toDTO(Performer p) {
		return new PerformerDTO(p.getId(), p.getName(), p.getImageName(), p.getGenre(), p.getBlurb());
	}
	
	public static Performer toStd(PerformerDTO dto) {
		return new Performer(dto.getId(), dto.getName(), dto.getImageName(), dto.getGenre(), dto.getBlurb());
	}
	
}
