package works.quiet.reference;

import lombok.*;

@Getter
@EqualsAndHashCode
@ToString
@Builder(toBuilder = true)
public class OrganisationModel {
    private int id;
    private String name;
}
