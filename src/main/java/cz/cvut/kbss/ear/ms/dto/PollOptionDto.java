package cz.cvut.kbss.ear.ms.dto;

import cz.cvut.kbss.ear.ms.model.Event;
import cz.cvut.kbss.ear.ms.model.Vote;
import lombok.*;

import javax.persistence.OrderBy;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PollOptionDto {

    @Getter
    @Setter
    private Integer id;

    @Setter
    @Getter
    private LocalDate dateSlot;

    @Setter @Getter
    private LocalTime timeSlot;

    @Setter @Getter
    private Boolean isFinal;

    @Setter @Getter
    private Integer eventId;

    @Setter
    @Getter
    private List<VoteDto> votes;
}
