package cz.cvut.kbss.ear.ms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "MS_POLLOPTION")
@NamedQueries({
        @NamedQuery(name = "Option.findByDateSlot", query = "SELECT u FROM PollOption u WHERE u.dateSlot = :dateslot"),
        @NamedQuery(name = "Option.findFinalForEvent", query = "SELECT p from PollOption p WHERE p.event = :event AND p.isFinal = true")
})
public class PollOption extends AbstractEntity{
    @Basic(optional = false)
    @Column(nullable = false)
    @Setter @Getter
    private LocalDate dateSlot;

    @Basic(optional = false)
    @Column(nullable = false)
    @Setter @Getter
    private LocalTime timeSlot;


    @Basic(optional = false)
    @Column(nullable = false)
    @Setter @Getter
    private Boolean isFinal = false;

    @JsonIgnore
    @ManyToOne
    @Setter @Getter
    private Event event;

    @OneToMany(mappedBy = "pollOption", cascade = {CascadeType.REMOVE}, orphanRemoval = true)
    @Setter
    @Getter
    @OrderBy("voteType asc")
    private List<Vote> votes;

    public PollOption() {
    }

    public PollOption(LocalDate dateSlot, LocalTime timeSlot, Event event) {
        this.dateSlot = dateSlot;
        this.timeSlot = timeSlot;
        this.event = event;
    }


    public String toString() {

        String vots = "";
        for(Vote p: votes){
            vots += "        [vote id=" + p.getId() + ", type=" + p.getVoteType() + ", comment=" + p.getComment() + ", event=" + p.getPollOption().getEvent().getId() + ", guest=" + p.getGuest().getUsername() + "]\n";
        }

        return "Date{" +
                "\n  id = " + getId() +
                "\n  dateSlot=" + dateSlot +
                "\n  timeSlot=" + timeSlot +
                "\n  isFinal=" + isFinal +
                "\n  votes{\n" + vots +
                "\n  }\n}";
    }
}
