package pl.miken.electionhandler.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.miken.electionhandler.dto.request.VotingRequest;
import pl.miken.electionhandler.dto.response.VotingDataResponse;
import pl.miken.electionhandler.facade.ActiveVotingFacade;

@Validated
@RestController
@Tag(name = "Voting")
@RequestMapping(value = "/api/v1/voting")
@RequiredArgsConstructor
public class ActiveVotingController {

    private final ActiveVotingFacade activeVotingFacade;

    @PostMapping("/add-vote")
    @Operation(summary = "Add user voting")
    public void addVoting(
            @Valid
            @NotNull
            @RequestBody
            VotingRequest votingRequest
    ) {
        activeVotingFacade.addVoting(votingRequest);
    }

    @GetMapping("/data/{voteIdentifier}")
    @Operation(summary = "Get voting data")
    public ResponseEntity<VotingDataResponse> getActiveVotingData(@NotNull @PathVariable String voteIdentifier) {
        return ResponseEntity.ok(activeVotingFacade.getActiveVotingData(voteIdentifier));
    }
}
