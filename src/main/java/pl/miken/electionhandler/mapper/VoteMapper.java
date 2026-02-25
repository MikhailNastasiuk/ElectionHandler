package pl.miken.electionhandler.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.miken.electionhandler.config.MapperConfig;
import pl.miken.electionhandler.dto.VoteDataPayload;
import pl.miken.electionhandler.dto.response.VoteResponse;
import pl.miken.electionhandler.entity.Vote;

@Mapper(config = MapperConfig.class,
        uses = {VoteOptionMapper.class})
public interface VoteMapper {

    VoteDataPayload toPayload(Vote vote);

    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "version")
    @Mapping(ignore = true, target = "isArchived")
    @Mapping(ignore = true, target = "activeVotings")
    Vote toEntity(VoteDataPayload voteDataPayload);

    VoteResponse toResponse(Vote vote);
}
