package works.quiet.reference;

import lombok.*;

@Getter
@EqualsAndHashCode
@ToString
@Builder(toBuilder = true)
public class OrganisationModel {
    public static final int UNKNOWN_ORGANISATION = 1;
    private int id;
    private String name;
}
