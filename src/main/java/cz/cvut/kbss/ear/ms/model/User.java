package cz.cvut.kbss.ear.ms.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@NamedQueries({
        @NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE :email = u.email"),
        @NamedQuery(name = "User.findUserByVote", query = "SELECT u FROM User u WHERE :vote MEMBER OF u.votes"),
        @NamedQuery(name = "User.findGuestEvent", query = "SELECT u FROM User u WHERE :event MEMBER OF u.guestEvents")
})
@Table(name = "MS_USER")
@DiscriminatorValue("USER")
public class User extends Account {

    @Basic(optional = false)
    @Column(nullable = false)
    @Setter
    @Getter
    private String firstName;

    @Basic(optional = false)
    @Column(nullable = false)
    @Setter @Getter
    private String lastName;

    @Basic(optional = false)
    @Column(nullable = false)
    @Setter @Getter
    private String email;

    @OneToMany(mappedBy = "owner", cascade = {CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    @Setter @Getter
    private Set<Event> ownedEvents;

    @ManyToMany(cascade = {CascadeType.MERGE})
    @Setter @Getter
    @OrderBy("name asc")
    private Set<Event> guestEvents;

    @OneToMany(mappedBy = "guest", cascade = {CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    @Setter @Getter
    @OrderBy("id")
    private List<Vote> votes;

    public User() {
        setRole(Role.USER);
    }

    public User(String username, String password, String email, String firstName, String lastName) {
        super(username, password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        setRole(Role.USER);
    }

    public String toString() {

        String owned = "";
        for(Event p: ownedEvents){
            owned += "        [id=" + p.getId() + ", name=" + p.getName() + ", open to=" + p.getOpenDueTo() + "]\n";
        }

        String guests = "";
        for(Event p: guestEvents){
            guests += "        [event id=" + p.getId() + ", name=" + p.getName() + ", open to=" + p.getOpenDueTo() + ", owner=" + p.getOwner().getUsername() + "]\n";
        }

        String vots = "";
        for(Vote p: votes){
            vots += "        [vote id=" + p.getId() + ", type=" + p.getVoteType() + ", comment=" + p.getComment() + ", event=" + p.getPollOption().getEvent().getId() + ", dateSlot=" + p.getPollOption().getDateSlot() + ", timeSlot=" + p.getPollOption().getTimeSlot() + "]\n";
        }
        return "User{" +
                "\n    id = " + getId() +
                "\n    username = " + getUsername() +
                "\n    firstName = " + firstName +
                "\n    lastName = " + lastName +
                "\n    email = " + email +
                ",\n    ownedEvents{\n" + owned +
                "    },\n    guestEvent{\n" + guests +
                "    },\n    votes{\n" + vots +
                "    }\n}";
    }
}
