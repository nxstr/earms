package cz.cvut.kbss.ear.ms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "MS_EVENT")
@NamedQueries({
        @NamedQuery(name = "Event.findByOpenDueTo", query = "SELECT u FROM Event u WHERE u.openDueTo >= :openDueTo")
})
public class Event extends AbstractEntity{
    @Basic(optional = false)
    @Column(nullable = false)
    @Setter
    @Getter
    private String name;

    @Basic(optional = false)
    @Column(nullable = false)
    @Setter @Getter
    private String detail;

    @Basic(optional = false)
    @Column(nullable = false)
    @Setter @Getter
    private LocalDate openDueTo;

    @Basic(optional = false)
    @Column(nullable = false)
    @Setter @Getter
    private String location;

    @OneToMany(mappedBy = "event", cascade = {CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    @Setter @Getter
    @OrderBy("dateSlot asc")
    private List<PollOption> options;


    @ManyToMany(mappedBy = "guestEvents", cascade = {CascadeType.MERGE})
    @Setter @Getter
    private Set<User> guests;

    @JsonIgnore
    @ManyToOne(cascade = {CascadeType.MERGE})
    @Setter @Getter
    private User owner;


    public Event(){

    }

    public Event(User owner, String name, String detail, LocalDate openDueTo) {
        this.name = name;
        this.detail = detail;
        this.openDueTo = openDueTo;
        this.owner = owner;
    }

    public String toString() {

        String opt = "";

        for(PollOption p: options){
            opt += "        [id=" + p.getId() + ", dateSlot=" + p.getDateSlot() + ", timeSlot=" + p.getTimeSlot() + ", isFinal=" + p.getIsFinal() + "]\n";
            String votes = "          votes{\n";
            for(Vote v:p.getVotes()){
                votes+= "            [id=" + v.getId() + ", voteType=" + v.getVoteType() + ", comment=" + v.getComment() + ", guest=" + v.getGuest().getUsername() + "]\n";
            }
            votes+="          }\n";
            opt+=votes;
        }
        String g = "";
        for(User p: guests){
            g += "        [id=" + p.getId() + ", username=" + p.getUsername() + ", email=" + p.getEmail() + "]\n";
        }
        return  "Event{" +
                "\n    id = " + getId() +
                "\n    name = " + name +
                ",\n    detail = " + detail +
                ",\n    openDueTo = " + openDueTo +
                ",\n    location = " + location +
                ",\n    owner = " + owner.getUsername() +
                ",\n    options{\n" + opt +
                "    },\n    guests{\n" + g +
                "    }\n}";
    }
}
