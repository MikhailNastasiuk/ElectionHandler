package pl.miken.electionhandler.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VotingDataResponse {

    private String voteIdentifier;

    private Map<String, Integer> votingData;
}
