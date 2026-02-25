package pl.miken.electionhandler.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.miken.electionhandler.dto.VoteDataPayload;
import pl.miken.electionhandler.dto.VoteOptionPayload;
import pl.miken.electionhandler.dto.response.VoteResponse;
import pl.miken.electionhandler.facade.VoteFacade;

import java.util.List;

@Validated
@RestController
@Tag(name = "Votes")
@RequestMapping(value = "/api/v1/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteFacade voteFacade;

    @GetMapping("/all-votes")
    @Operation(summary = "Get all existed votes")
    public ResponseEntity<List<VoteResponse>> getAllVotes() {
        return ResponseEntity.ok(voteFacade.getAllActiveVotes());
    }

    @GetMapping("/vote-data/{voteIdentifier}")
    @Operation(summary = "Get active vote data by unique identifier")
    public ResponseEntity<VoteDataPayload> getVoteData(@NotNull @PathVariable String voteIdentifier) {
        return ResponseEntity.ok(voteFacade.findVoteByUniqueIdentifier(voteIdentifier));
    }

    @GetMapping("/active-vote/{voteIdentifier}")
    @Operation(summary = "Get active vote data by unique identifier")
    public ResponseEntity<VoteDataPayload> getActiveVote(@NotNull @PathVariable String voteIdentifier) {
        return ResponseEntity.ok(voteFacade.findActiveByUniqueIdentifier(voteIdentifier));
    }

    @GetMapping("/all-active-vote")
    @Operation(summary = "Get active vote data by unique identifier")
    public ResponseEntity<List<VoteDataPayload>> getAllActiveVote() {
        return ResponseEntity.ok(voteFacade.findAllActiveVote());
    }

    @PostMapping("/start-vote/{voteIdentifier}")
    @Operation(summary = "Start vote")
    public void startVote(@NotNull @PathVariable String voteIdentifier) {
        voteFacade.startVote(voteIdentifier);
    }

    @PostMapping("/remove-vote/{voteIdentifier}")
    @Operation(summary = "Remove vote")
    public void removeVote(@NotNull @PathVariable String voteIdentifier) {
        voteFacade.removeVote(voteIdentifier);
    }

    @PostMapping("/add-vote")
    @Operation(summary = "Add new vote")
    public void addVote(
            @Valid
            @NotNull
            @RequestBody
            VoteDataPayload voteDataPayload
    ) {
        voteFacade.addVote(voteDataPayload);
    }

    @PostMapping("/add-option/{voteIdentifier}")
    @Operation(summary = "Add new option to vote")
    public void addOptionToVote(@NotNull @PathVariable String voteIdentifier, @NotNull @RequestBody VoteOptionPayload optionPayload) {
        voteFacade.addOptionToVote(voteIdentifier, optionPayload);
    }

    @PatchMapping("/remove-option/{voteIdentifier}")
    @Operation(summary = "Remove option from vote")
    public void removeOptionFromVote(@NotNull @PathVariable String voteIdentifier, @NotNull @RequestBody VoteOptionPayload optionPayload) {
        voteFacade.removeOptionFromVote(voteIdentifier, optionPayload);
    }
}
