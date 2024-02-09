package works.quiet.reference;

import works.quiet.db.Repository;

import java.util.Optional;

public interface OrganisationRepository extends Repository<Organisation> {
    Optional<Organisation> findByName(String name);
}
