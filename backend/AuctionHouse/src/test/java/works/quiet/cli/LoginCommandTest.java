package works.quiet.cli;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import picocli.CommandLine;
import works.quiet.resources.Resources;
import works.quiet.user.AdminService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@DisplayName("LoginCommand black box tests.")
class LoginCommandTest {

    Resources resources;
    AdminService adminServiceMock;
    StringWriter stdErr;
    StringWriter stdOut;
    CommandLine program = new CommandLine(new LoginCommand(resources, adminServiceMock));

    @BeforeEach
    void setup() {
        resources = new Resources();
        adminServiceMock = mock();
        stdErr = new StringWriter();
        stdOut = new StringWriter();
        program = new CommandLine(new LoginCommand(resources, adminServiceMock));
        program.setOut(new PrintWriter(stdOut));
        program.setErr(new PrintWriter(stdErr));
        program.setExecutionExceptionHandler(new PrintExceptionMessageHandler());
    }

    private String sanitize(final StringWriter w) {
        return w.toString().replace("\n", "");
    }

    private String sanitizedOut() {
        return sanitize(stdOut);
    }

    private String sanitizedErr() {
        return sanitize(stdErr);
    }

    @ParameterizedTest
    @CsvSource({
            "--username,--password",
            "-u,-p",
            "--username,-p",
            "-u,--password",
    })
    @DisplayName("Should fail when the user enters wrong username or password.")
    void wrongUsernameOrPassword(final String usernameOption, final String passwordOption) throws Exception {
        var expectedMessage = "boom!";
        doThrow(new Exception(expectedMessage))
                .when(adminServiceMock)
                .login(anyString(), anyString());

        var exitCode = program.execute(
                usernameOption, "",
                passwordOption, ""
        );

        verify(adminServiceMock).login(anyString(), anyString());
        verify(adminServiceMock, never()).assertIsNotBlocked();
        assertEquals(1, exitCode);
        assertEquals(expectedMessage, sanitizedErr());
    }

    @ParameterizedTest
    @CsvSource({
            "--username,--password",
            "-u,-p",
            "--username,-p",
            "-u,--password",
    })
    @DisplayName("Should fail when the user is blocked.")
    void blockedUser(final String usernameOption, final String passwordOption) throws Exception {
        var expectedMessage = "boom!";
        doThrow(new Exception(expectedMessage)).when(adminServiceMock).assertIsNotBlocked();

        var exitCode = program.execute(
                usernameOption, "",
                passwordOption, ""
        );

        verify(adminServiceMock).login(anyString(), anyString());
        verify(adminServiceMock).assertIsNotBlocked();
        assertEquals(1, exitCode);
        assertEquals(expectedMessage, sanitizedErr());
    }

    @ParameterizedTest
    @CsvSource({
            "--username,--password",
            "-u,-p",
            "--username,-p",
            "-u,--password",
    })
    @DisplayName("Should login successfully with all allowed parameter combination.")
    void logInSuccess(final String usernameOption, final String passwordOption) throws Exception {
        var expectedUsername = "foo";
        var expectedPassword = "bar";
        var expectedMessage = MessageFormat
                .format(resources.getBundle().getString("messages.login"), expectedUsername);

        var exitCode = program.execute(
                usernameOption, expectedUsername,
                passwordOption, expectedPassword
        );

        verify(adminServiceMock).login(expectedUsername, expectedPassword);
        verify(adminServiceMock).assertIsNotBlocked();
        assertEquals(0, exitCode);
        assertEquals(expectedMessage, sanitizedOut());
    }
}
