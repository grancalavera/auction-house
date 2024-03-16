package works.quiet.reports;

import works.quiet.db.Repository;
import works.quiet.user.User;

import java.util.List;

public interface ReportRepository extends Repository<Report> {

    List<Report> findReportsForUser(User user);
}
