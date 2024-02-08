package works.quiet.reference;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
@Builder(toBuilder = true)
public class Organisation {
    private int id;
    private String name;
}
