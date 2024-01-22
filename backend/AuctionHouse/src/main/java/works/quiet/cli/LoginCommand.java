package works.quiet.cli;

import picocli.CommandLine;
import works.quiet.admin.AdminService;
import works.quiet.io.*;
import works.quiet.user.PGUserDao;
import works.quiet.user.UserDao;
import works.quiet.user.UserModel;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name ="login",
        description = "Login with username and password, and persists an user session.",
        mixinStandardHelpOptions = true
)
public class LoginCommand implements Callable<Integer> {
    @CommandLine.Option(names = {"-u", "--username"}, required = true)
    private String username;

    @CommandLine.Option( names = {"-p", "--password"}, required = true)
    private String password;

    private final AdminService adminService;

    public LoginCommand(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public Integer call() {
        try {
            adminService.login(username, password);
            System.out.printf("Logged in as '%s'.\n",username);
            return 0;
        }catch(Exception ex) {
            System.out.println(ex.getMessage());
            return 1;
        }
    }
}
