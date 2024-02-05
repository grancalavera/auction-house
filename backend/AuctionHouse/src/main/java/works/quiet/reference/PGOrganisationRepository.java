package works.quiet.reference;

import lombok.extern.java.Log;
import works.quiet.io.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
public class PGOrganisationRepository implements OrganisationRepository {
    private final DBConnection connection;

    public PGOrganisationRepository(final Level logLevel, final DBConnection connection) {
        log.setLevel(logLevel);
        this.connection = connection;
    }


    @Override
    public List<OrganisationModel> listOrganisations() {
        List<OrganisationModel> organistions = new ArrayList<>();
        String query = "SELECT * from ah_organisations";

        connection.getConnection().ifPresent(conn -> {
            try (
                    Statement statement = conn.createStatement();
                    ResultSet resultSet = statement.executeQuery(query);
            ) {
                while (resultSet.next()) {
                    organisationFromResultSet(resultSet).ifPresent(organistions::add);
                }
            } catch (final SQLException ex) {
                log.severe(ex.toString());
            }
        });

        return organistions;
    }

    private Optional<OrganisationModel> organisationFromResultSet(final ResultSet resultSet) {
        try {
            OrganisationModel model = OrganisationModel
                    .builder()
                    .id(resultSet.getInt("id"))
                    .name(resultSet.getString("org_name"))
                    .build();
            return Optional.of(model);
        } catch (final Exception ex) {
            log.severe(ex.toString());
            return Optional.empty();
        }
    }
}
