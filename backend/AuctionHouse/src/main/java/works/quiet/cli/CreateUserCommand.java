package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.user.AdminService;

import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "create-user",
        description = "Creates a new user.",
        mixinStandardHelpOptions = true,
        sortOptions = false
)
public class CreateUserCommand implements Callable<Integer> {
    private final AdminService adminService;
    @CommandLine.Option(
            names = {"-u", "--username"},
            required = true,
            description = "Alpha-numerical string (no spaces or special characters). Usernames must be unique."
    )

    private String username;
    @CommandLine.Option(
            names = {"-p", "--password"},
            required = true,
            description = "At least 16 characters long."
    )
    private String password;

    @CommandLine.Option(
            names = {"-f", "--first-name"},
            required = true,
            description = "The user's first name."
    )
    private String firstName;

    @CommandLine.Option(
            names = {"-l", "--last-name"},
            required = true,
            description = "The user's last name."
    )
    private String lastName;

    @CommandLine.Option(
            names = {"-o", "--organisation"},
            arity = "1..*",
            description = "The case sensitive user's organisation name. If the organisation name doesn't exist, a new organisation for the given name will be created.",
            required = true

    )
    private List<String> organisation;

    @CommandLine.Option(
            names = {"-r", "--role"},
            description = "The user's role. Possible values are: ADMIN, USER. Default value: USER.",
            defaultValue = "USER"
    )
    private String roleName;

    @CommandLine.Option(
            names = {"-s", "--account-status"},
            description = "The user's account status. Possible values are: ACTIVE, BLOCKED. Default value: ACTIVE.",
            defaultValue = "ACTIVE"
    )
    private String accountStatusName;

    public CreateUserCommand(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public Integer call() throws Exception {
        adminService.assertIsNotBlocked();
        adminService.assertIsAdmin();
        var organisationName = organisation.stream().reduce((acc, next) -> acc + " " + next).get();

        var userId = adminService.createUser(
                username,
                password,
                firstName,
                lastName,
                organisationName,
                roleName,
                accountStatusName
        );

        System.out.printf("User created with id=%d\n", userId);
        return 0;
    }
}
