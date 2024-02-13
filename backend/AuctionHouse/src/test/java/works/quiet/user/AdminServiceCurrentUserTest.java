package works.quiet.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminServiceCurrentUserTest {

    @Test
    @DisplayName("Should cache username from session (only open session once)")
    void getCurrentUsername() {
        var expectedUsername = "coyote-jackson";
        Session sessionMock = mock();
        when(sessionMock.getUsername()).thenReturn(Optional.of(expectedUsername));
        var adminService = new AdminService(Level.OFF, mock(), mock(), mock(), sessionMock, mock());
        assertEquals(expectedUsername, adminService.getCurrentUsername());
        assertEquals(expectedUsername, adminService.getCurrentUsername());
        verify(sessionMock, times(1)).getUsername();
    }

    @Test
    void getCurrentUser() {
    }
}