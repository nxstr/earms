package cz.cvut.kbss.ear.ms.dto;

import cz.cvut.kbss.ear.ms.model.User;
import cz.cvut.kbss.ear.ms.model.VoteType;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VoteDto {

    @Getter
    @Setter
    private Integer id;

    @Setter
    @Getter
    private String comment;

    @Setter @Getter
    private String voteType;

    @Setter @Getter
    private Integer guestId;

    @Setter @Getter
    private Integer pollOptionId;
}
