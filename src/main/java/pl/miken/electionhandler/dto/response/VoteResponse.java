package pl.miken.electionhandler.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoteResponse {

    private String voteName;

    private String uniqueIdentifier;

    private String description;
}
