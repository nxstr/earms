package cz.cvut.kbss.ear.ms.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "MS_VOTE")
public class Vote extends AbstractEntity{


    @Basic(optional = false)
    @Setter
    @Getter
    private String comment;

    @Enumerated(EnumType.STRING)
    @Setter @Getter
    private VoteType voteType;

    @ManyToOne
    @Setter @Getter
    private User guest;

    @ManyToOne
    @Setter @Getter
    private PollOption pollOption;

    public Vote() {
        this.voteType = VoteType.NEUTRAL;
    }

    public Vote(User guest, PollOption pollOption) {
        this.guest = guest;
        this.pollOption = pollOption;
        this.voteType = VoteType.NEUTRAL;
    }

    public String toString() {
        return "Vote{" +
                "comment=" + comment +
                ", type=" + voteType +
                "}";
    }
}
