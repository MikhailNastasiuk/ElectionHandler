package pl.miken.electionhandler.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "active_votings")
public class ActiveVoting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vote_id", nullable = false)
    private Vote vote;

    @Column(name = "vote_json", nullable = false)
    private String voteJson;

    @Column(name = "started_date", nullable = false)
    private LocalDate startedDate;

    @Column(name = "finished_date")
    private LocalDate finishedDate;

    @Column(name = "vote_version", nullable = false)
    private Integer voteVersion;

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "activeVoting", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoteAnswers> voteAnswers = new ArrayList<>();
}
