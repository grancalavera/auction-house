package works.quiet.reference;

import java.util.Map;

public interface OrganisationRepository {
    Map<Integer, OrganisationModel> getAll();
}
