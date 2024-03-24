package works.quiet.reports;

import works.quiet.db.Repository;

import java.util.List;

public interface ExecutionRepository extends Repository<Execution> {
      List<Execution> findAllByBidderId(int bidderId);
}
