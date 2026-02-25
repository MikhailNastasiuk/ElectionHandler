package pl.miken.electionhandler.dto.request;

import jakarta.validation.constraints.NotNull;
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
public class VotingRequest {

    @NotNull(message = "UserId can't be null")
    private String voteIdentifier;

    @NotNull(message = "UserId can't be null")
    private Long userId;

    @NotNull(message = "OptionId can't be null")
    private Long optionId;
}
