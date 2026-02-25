package pl.miken.electionhandler.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.miken.electionhandler.dto.VoteDataPayload;
import pl.miken.electionhandler.service.ActiveVotingService;
import pl.miken.electionhandler.service.ObjectMapperService;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledActiveVotingHandler {

    private final ActiveVotingService activeVotingService;
    private final ObjectMapperService objectMapperService;

    @Scheduled(cron = "${election-handler.jobs-schedule.check-finished-voting}")
    public void checkFinishDateForActiveVoting() {
        log.info("Start ScheduledActiveVotingHandler: Checking for active voting to finished");
        var activeVotings = activeVotingService.getAllActiveVotings();
        if (activeVotings.isEmpty()) {
            return;
        }

        var errorMessage = "Error reading data from active voting with id = ";
        activeVotings.forEach(activeVoting -> {
            VoteDataPayload votePayload;
            try {
                votePayload = objectMapperService.deserializeObject(activeVoting.getVoteJson(),
                        VoteDataPayload.class,
                        errorMessage + activeVoting.getId());
            } catch (Exception e) {
                log.error("ScheduledActiveVotingHandler error: {}", errorMessage);
                return;
            }
            var duration = votePayload.getDurationPeriodInDays();
            var now = LocalDate.now();

            if (now.isAfter(activeVoting.getStartedDate().plusDays(duration))) {
                activeVoting.setFinishedDate(now);
                activeVotingService.saveActiveVoting(activeVoting);
            }
        });

        log.info("Finished ScheduledActiveVotingHandler");
    }
}
