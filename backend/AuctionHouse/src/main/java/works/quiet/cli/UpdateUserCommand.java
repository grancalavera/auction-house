package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.user.AdminService;

import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "update-user",
        description = "Creates a new user.",
        mixinStandardHelpOptions = true,
        sortOptions = false
)
public class UpdateUserCommand implements Callable<Integer> {
    private final AdminService adminService;

    @CommandLine.Option(
            names = {"-u", "--username"},
            description = "Alpha-numerical string (no spaces or special characters). Usernames must be unique."
    )
    private String username;

    @CommandLine.Option(
            names = {"-p", "--password"},
            description = "At least 16 characters long."
    )
    private String password;

    @CommandLine.Option(
            names = {"-f", "--first-name"},
            description = "The user's first name."
    )
    private String firstName;

    @CommandLine.Option(
            names = {"-l", "--last-name"},
            description = "The user's last name."
    )
    private String lastName;

    @CommandLine.Option(
            names = {"-o", "--organisation"},
            arity = "1..*",
            description = "The case sensitive user's organisation name. If the organisation name doesn't exist, a new organisation for the given name will be created."
    )
    private List<String> organisation;

    @CommandLine.Option(
            names = {"-r", "--role"},
            description = "The user's role. Possible values are: ADMIN, USER."
    )
    private String roleName;

    @CommandLine.Option(
            names = {"-s", "--account-status"},
            description = "The user's account status. Possible values are: ACTIVE, BLOCKED"
    )
    private String accountStatusName;

    @CommandLine.Parameters(paramLabel = "USER_ID", description = "The user id to update.")
    private int userId;

    public UpdateUserCommand(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public Integer call() throws Exception {
        adminService.assertIsNotBlocked();
        adminService.assertIsAdmin();

        var organisationName = organisation == null
                ? null
                : organisation.stream().reduce((acc, next) -> acc + " " + next).get();

        adminService.updateUser(
                userId,
                username,
                password,
                firstName,
                lastName,
                organisationName,
                roleName,
                accountStatusName
        );

        System.out.printf("Updated user with user.id=%d\n", userId);
        return 0;
    }
}
