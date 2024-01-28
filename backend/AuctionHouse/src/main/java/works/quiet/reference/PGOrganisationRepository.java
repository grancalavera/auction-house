package works.quiet.reference;

import lombok.extern.java.Log;
import works.quiet.io.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.logging.Level;

@Log
public class PGOrganisationRepository implements OrganisationRepository {
    private final DBConnection connection;

    public PGOrganisationRepository(Level logLevel, DBConnection connection) {
        log.setLevel(logLevel);
        this.connection = connection;
    }

    @Override
    public Map<Integer, OrganisationModel> getAll() {
        Map<Integer, OrganisationModel> result = new HashMap<>();
        String query = "SELECT * from ah_organisations";

        connection.getConnection().ifPresent(conn -> {
            try (
                    Statement statement = conn.createStatement();
                    ResultSet resultSet = statement.executeQuery(query);
            ){
                while (resultSet.next()) {
                    deserialize(resultSet).ifPresent(model -> result.put(model.getId(), model));
                }
            } catch (SQLException ex) {
                log.severe(ex.toString());
            }
        });

        return result;
    }

    private Optional<OrganisationModel> deserialize(ResultSet resultSet) {
        try {
            OrganisationModel model = OrganisationModel
                    .builder()
                    .id(resultSet.getInt("id"))
                    .name(resultSet.getString("org_name"))
                    .build();
            return Optional.of(model);
        }catch(Exception ex){
            log.severe(ex.toString());
            return Optional.empty();
        }
    }
}
