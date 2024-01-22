package works.quiet.user;

import java.util.Map;

public interface OrganisationDao {
    Map<Integer, OrganisationModel> getAll();
}
