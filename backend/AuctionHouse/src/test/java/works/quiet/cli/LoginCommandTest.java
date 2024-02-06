package works.quiet.cli;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import works.quiet.user.AdminService;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class LoginCommandTest {

    @Test
    void wrongUsernameOrPassword() throws Exception {
        AdminService adminServiceMock = mock();
        var message = "boom!";
        doThrow(new Exception(message))
                .when(adminServiceMock)
                .login(anyString(), anyString());

        StringWriter stdErr = new StringWriter();
        CommandLine program = new CommandLine(new LoginCommand(adminServiceMock));
        program.setErr(new PrintWriter(stdErr));

        var exitCode = program
                .setExecutionExceptionHandler(new PrintExceptionMessageHandler())
                .execute("--username", "foo", "--password", "bar");

        assertEquals(1, exitCode);
        assertEquals(message + "\n", stdErr.toString());
    }

    @Test
    void blockedUser() throws Exception {
        AdminService adminServiceMock = mock();
        var message = "boom!";
        doThrow(new Exception(message))
                .when(adminServiceMock)
                .assertIsNotBlocked();

        StringWriter stdErr = new StringWriter();
        CommandLine program = new CommandLine(new LoginCommand(adminServiceMock));
        program.setErr(new PrintWriter(stdErr));

        var exitCode = program
                .setExecutionExceptionHandler(new PrintExceptionMessageHandler())
                .execute("--username", "foo", "--password", "bar");

        verify(adminServiceMock).login("foo", "bar");
        verify(adminServiceMock).assertIsNotBlocked();

        assertEquals(1, exitCode);
        assertEquals(message + "\n", stdErr.toString());
    }

    @Test
    void logInSuccess() throws Exception {
        AdminService adminServiceMock = mock();
        StringWriter stdOut = new StringWriter();
        CommandLine program = new CommandLine(new LoginCommand(adminServiceMock));
        program.setOut(new PrintWriter(stdOut));

        var exitCode = program
                .execute("--username", "foo", "--password", "bar");

        verify(adminServiceMock).login("foo", "bar");
        verify(adminServiceMock).assertIsNotBlocked();

        assertEquals(0, exitCode);
        assertEquals("Logged in as 'foo'.\n", stdOut.toString());
    }

    @Test
    void logInSuccessShort() {
        AdminService adminServiceMock = mock();
        StringWriter stdOut = new StringWriter();
        CommandLine program = new CommandLine(new LoginCommand(adminServiceMock));
        program.setOut(new PrintWriter(stdOut));

        var exitCode = program
                .execute("-u", "foo", "-p", "bar");

        assertEquals(0, exitCode);
        assertEquals("Logged in as 'foo'.\n", stdOut.toString());
    }
}
