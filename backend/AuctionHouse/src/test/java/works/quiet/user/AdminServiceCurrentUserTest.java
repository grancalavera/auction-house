package works.quiet.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminServiceCurrentUserTest {
    @Test
    @DisplayName("Should cache current username from session (only open session once).")
    void ensureCurrentUsernameIsCached() {
        var expectedUsername = "coyote-jackson";
        Session sessionMock = mock();
        when(sessionMock.getUsername()).thenReturn(Optional.of(expectedUsername));

        var adminService = new AdminService(Level.OFF, mock(), mock(), mock(), sessionMock, mock());

        assertEquals(expectedUsername, adminService.getCurrentUsername());
        assertEquals(expectedUsername, adminService.getCurrentUsername());
        verify(sessionMock, times(1)).getUsername();
    }

    @Test
    @DisplayName("Should cache current user from repo (only go to DB once).")
    void ensureCurrentUserIsCached() {
        var expectedUsername = "coyote-jackson";
        var expectedUserId = Integer.MAX_VALUE;
        var user = User.builder().id(expectedUserId).build();

        UserRepository userRepoMock = mock();
        when(userRepoMock.findByUsername(anyString())).thenReturn(Optional.of(user));

        Session sessionMock = mock();
        when(sessionMock.getUsername()).thenReturn(Optional.of(expectedUsername));

        var adminService = new AdminService(Level.OFF, mock(), userRepoMock, mock(), sessionMock, mock());

        assertEquals(expectedUserId, adminService.getCurrentUser().getId());
        assertEquals(expectedUserId, adminService.getCurrentUser().getId());
        verify(userRepoMock, times(1)).findByUsername(expectedUsername);
    }
}
