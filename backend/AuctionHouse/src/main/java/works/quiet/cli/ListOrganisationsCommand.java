package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.resources.Resources;
import works.quiet.user.AdminService;

import java.util.logging.Level;

@CommandLine.Command(
        name = "list-organisations",
        description = "List all existing organisations.",
        mixinStandardHelpOptions = true
)
public class ListOrganisationsCommand extends CommandWithAdmin {

    public ListOrganisationsCommand(final Level logLevel, final Resources resources, final AdminService adminService) {
        super(logLevel, resources, adminService);
    }

    @Override
    public Integer call() throws Exception {
        adminService.assertIsNotBlocked();
        adminService.assertIsAdmin();
        adminService.listOrganisations().forEach(System.out::println);
        return 0;
    }
}
