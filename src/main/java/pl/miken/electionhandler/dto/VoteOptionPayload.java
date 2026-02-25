package pl.miken.electionhandler.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoteOptionPayload {

    private Long id;

    @NotEmpty(message = "Option name can't be empty")
    private String optionName;
}
