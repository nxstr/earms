package cz.cvut.kbss.ear.ms.dto;

import lombok.*;

import javax.persistence.Basic;
import javax.persistence.Column;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {

    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private String password;
    @Setter
    @Getter
    private String firstName;

    @Setter @Getter
    private String lastName;

    @Setter @Getter
    private String email;

    @Setter @Getter
    private List<EventDto> ownedEvents;

    @Setter @Getter
    private List<EventDto> guestEvents;

    @Setter @Getter
    private List<VoteDto> votes;
}
