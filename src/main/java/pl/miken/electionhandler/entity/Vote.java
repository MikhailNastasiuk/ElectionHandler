package pl.miken.electionhandler.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "votes")
public class Vote {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 1, max = 100)
    @Column(name = "vote_name", nullable = false)
    private String voteName;

    @Size(min = 1, max = 50)
    @Column(name = "unique_identifier", nullable = false, unique = true)
    private String uniqueIdentifier;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "duration_period_in_days", nullable = false)
    private Integer durationPeriodInDays;

    @Column(name = "version", nullable = false)
    private Integer version;

    //not used for now
    //can be used to proceed only adult voting
    @Column(name = "is_adult", nullable = false)
    private Boolean isAdult;

    //not used for now
    //can be used to proceed multiple answers
    @Column(name = "is_multiple", nullable = false)
    private Boolean isMultiple;

    @Column(name = "is_archived", nullable = false)
    private Boolean isArchived;

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "vote", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoteOption> options = new ArrayList<>();

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "vote", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActiveVoting> activeVotings = new ArrayList<>();
}
