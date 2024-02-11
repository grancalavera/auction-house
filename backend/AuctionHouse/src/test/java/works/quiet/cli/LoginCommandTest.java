package works.quiet.cli;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import picocli.CommandLine;
import works.quiet.cli.etc.AdminTestHarness;

import java.text.MessageFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@DisplayName("LoginCommand black box tests.")
class LoginCommandTest {

    private AdminTestHarness harness;

    @BeforeEach
    void setup() {
        harness = new AdminTestHarness(LoginCommand.class);
    }

    @Test
    @DisplayName("Should fail with USAGE error code for bad user input")
    void badUserInput() {
        assertEquals(CommandLine.ExitCode.USAGE, harness.program.execute());
    }

    @ParameterizedTest
    @CsvSource({
            "--username,--password",
            "-u,-p",
            "--username,-p",
            "-u,--password",
    })
    @DisplayName("Should fail with SOFTWARE error code when the user enters wrong username or password.")
    void wrongUsernameOrPassword(final String usernameOption, final String passwordOption) throws Exception {
        var expectedMessage = "boom!";

        doThrow(new Exception(expectedMessage))
                .when(harness.adminService)
                .login(anyString(), anyString());

        var exitCode = harness.program.execute(usernameOption, "", passwordOption, "");

        verify(harness.adminService).login(anyString(), anyString());
        verify(harness.adminService, never()).assertIsNotBlocked();
        assertEquals(CommandLine.ExitCode.SOFTWARE, exitCode);
        assertEquals(expectedMessage, harness.sanitizedErr());
    }

    @ParameterizedTest
    @CsvSource({
            "--username,--password",
            "-u,-p",
            "--username,-p",
            "-u,--password",
    })
    @DisplayName("Should fail with SOFTWARE error code when the user is blocked.")
    void blockedUser(final String usernameOption, final String passwordOption) throws Exception {
        var expectedMessage = "boom!";
        doThrow(new Exception(expectedMessage)).when(harness.adminService).assertIsNotBlocked();

        var exitCode = harness.program.execute(usernameOption, "", passwordOption, "");

        verify(harness.adminService).login(anyString(), anyString());
        verify(harness.adminService).assertIsNotBlocked();
        assertEquals(CommandLine.ExitCode.SOFTWARE, exitCode);
        assertEquals(expectedMessage, harness.sanitizedErr());
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
                .format(harness.resources.getBundle().getString("messages.login"), expectedUsername);

        var exitCode = harness.program.execute(usernameOption, expectedUsername, passwordOption, expectedPassword);

        verify(harness.adminService).login(expectedUsername, expectedPassword);
        verify(harness.adminService).assertIsNotBlocked();
        assertEquals(CommandLine.ExitCode.OK, exitCode);
        assertEquals(expectedMessage, harness.sanitizedOut());
    }
}
