package pl.miken.electionhandler.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.miken.electionhandler.config.MapperConfig;
import pl.miken.electionhandler.dto.VoteOptionPayload;
import pl.miken.electionhandler.entity.VoteOption;

@Mapper(config = MapperConfig.class)
public interface VoteOptionMapper {

    VoteOptionPayload toPayload(VoteOption voteOption);

    @Mapping(ignore = true, target = "vote")
    @Mapping(ignore = true, target = "isArchived")
    @Mapping(ignore = true, target = "voteAnswers")
    VoteOption toEntity(VoteOptionPayload voteOptionPayload);
}
