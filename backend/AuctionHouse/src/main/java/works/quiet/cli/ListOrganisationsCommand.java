package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.user.AdminService;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "list-organisations",
        description = "List all existing organisations.",
        mixinStandardHelpOptions = true
)
public class ListOrganisationsCommand implements Callable<Integer> {

    private final AdminService adminService;

    public ListOrganisationsCommand(AdminService adminService) {
        this.adminService = adminService;
    }


    @Override
    public Integer call() throws Exception {
        adminService.assertIsNotBlocked();
        adminService.assertIsAdmin();
        adminService.listOrganisations().forEach(System.out::println);
        return 0;
    }
}
