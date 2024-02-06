package works.quiet.cli;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import works.quiet.user.AdminService;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

class LoginCommandTest {

    @Test
    void wrongUsernameOrPassword() throws Exception {
        AdminService adminServiceMock = mock();
        doThrow(Exception.class)
                .when(adminServiceMock)
                .login(anyString(), anyString());

        StringWriter stdErr = new StringWriter();
        CommandLine program = new CommandLine(new LoginCommand(adminServiceMock));
        program.setErr(new PrintWriter(stdErr));

        var exitCode = program
                .setExecutionExceptionHandler(new PrintExceptionMessageHandler())
                .execute("--username", "foo", "--password", "bar");

        assertEquals(1, exitCode);
        assertEquals("Incorrect username or password.\n", stdErr.toString());
    }

    @Test
    void blockedUser() throws Exception {
        AdminService adminServiceMock = mock();

        doThrow(Exception.class)
                .when(adminServiceMock)
                .assertIsNotBlocked();

        StringWriter stdErr = new StringWriter();
        CommandLine program = new CommandLine(new LoginCommand(adminServiceMock));
        program.setErr(new PrintWriter(stdErr));

        var exitCode = program
                .setExecutionExceptionHandler(new PrintExceptionMessageHandler())
                .execute("--username", "foo", "--password", "bar");

        assertEquals(1, exitCode);
        assertEquals("Not authorised.\n", stdErr.toString());

    }
}
