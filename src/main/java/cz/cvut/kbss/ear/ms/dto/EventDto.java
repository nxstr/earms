package cz.cvut.kbss.ear.ms.dto;

import cz.cvut.kbss.ear.ms.model.User;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventDto {

    @Getter
    @Setter
    private Integer id;

    @Setter
    @Getter
    private String name;

    @Setter @Getter
    private String detail;

    @Setter @Getter
    private LocalDate openDueTo;

    @Setter @Getter
    private String location;

    @Setter @Getter
    private Integer ownerId;

    @Setter @Getter
    private List<AccountDto> guests;

    @Setter @Getter
    private List<PollOptionDto> pollOptions;
}
