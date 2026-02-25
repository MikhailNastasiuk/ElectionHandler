package pl.miken.electionhandler.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoteDataPayload {

    @NotEmpty(message = "Vote name can't be empty")
    private String voteName;

    @NotEmpty(message = "Unique identifier name can't be empty")
    private String uniqueIdentifier;

    @NotEmpty(message = "Description name can't be empty")
    private String description;

    @NotNull(message = "Duration period can't be null")
    private Integer durationPeriodInDays;

    @NotNull(message = "isAdult parameter can't be null")
    private Boolean isAdult;

    @NotNull(message = "isMultiple parameter can't be null")
    private Boolean isMultiple;

    private List<VoteOptionPayload> options;
}
