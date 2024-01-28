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



    @Override
    public Integer call() throws Exception {
        var createdId = adminService.createUser(username, password);
        System.out.printf("User created with id=%d\n", createdId);
        return 0;
    }
}
