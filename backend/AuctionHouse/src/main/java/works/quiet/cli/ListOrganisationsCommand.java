package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.reference.OrganisationModel;
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
    public Integer call() {
        // this should be handled by the top level admin command
        try {
            adminService.assertIsAdmin();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return 1;
        }

        var organisations = adminService.listOrganistions();
        for (OrganisationModel organisation : organisations) {
            System.out.println(organisation.toString());
        }

        return 0;
    }
}
