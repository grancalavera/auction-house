package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.user.AdminService;
import works.quiet.user.Role;
import works.quiet.user.UserModel;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "create-user",
        description = "Creates a new user.",
        mixinStandardHelpOptions = true
)
public class CreateUserCommand implements Callable<Integer> {

    private final AdminService adminService;
    @CommandLine.Option(
            names = {"-u", "--username"}, required = true,
            description = "alpha-numerical string (no spaces or special characters), usernames must be unique."
    )
    private String username;
    @CommandLine.Option(
            names = {"-p", "--password"}, required = true,
            description = "At least 16 characters long"
    )
    private String password;

    public CreateUserCommand(AdminService adminService) {
        this.adminService = adminService;
    }


    @CommandLine.Option(
            names = {"-r", "--roleId"},
            paramLabel = "<roleId>",
            defaultValue = "2",
            description = "The role id for the new user, valid values are 1 (ADMIN), 2 (USER). Default value: ${DEFAULT-VALUE}"
    )
    private int roleId;

    @Override
    public Integer call() throws Exception {
        adminService.assertIsAdmin();
        System.out.printf("create-user username=%s password=%s role=%s\n", username, password, roleId);
        final UserModel user = adminService.createUser(username, password, roleId);
        System.out.println("created user: " + user);
        return 0;
    }
}
