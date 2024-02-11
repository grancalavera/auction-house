package works.quiet.cli;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import works.quiet.cli.etc.AdminTestHarness;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@DisplayName("LogoutCommand black box tests.")
class LogoutCommandTest {
    private AdminTestHarness harness;

    @BeforeEach
    void setup() {
        harness = new AdminTestHarness(LogoutCommand.class);
    }

    @Test
    @DisplayName("Should fail when call to logout fails")
    void logoutFails() throws Exception {
        var expectedMessage = "boom!";
        doThrow(new Exception(expectedMessage))
                .when(harness.adminService)
                .logout();

        var exitCode = harness.program.execute();

        verify(harness.adminService).logout();
        assertEquals(expectedMessage, harness.sanitizedErr());
        assertEquals(CommandLine.ExitCode.SOFTWARE, exitCode);
    }

    @Test
    @DisplayName("Should logout successfully")
    void logoutSuccess() throws Exception {
        var exitCode = harness.program.execute();
        verify(harness.adminService).logout();
        assertEquals(
                harness.resources.getBundle().getString("messages.logout"),
                harness.sanitizedOut()
        );
        assertEquals(CommandLine.ExitCode.OK, exitCode);
    }

}
