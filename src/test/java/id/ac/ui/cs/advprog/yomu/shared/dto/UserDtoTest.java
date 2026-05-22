package id.ac.ui.cs.advprog.yomu.shared.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserDtoTest {

    @Test
    void testUserDto() {
        UUID id = UUID.randomUUID();
        UserDto dto = UserDto.builder()
                .id(id)
                .username("testuser")
                .email("test@yomu.local")
                .displayName("Test User")
                .role("USER")
                .build();

        assertEquals(id, dto.getId());
        assertEquals("testuser", dto.getUsername());
        assertEquals("test@yomu.local", dto.getEmail());
        assertEquals("Test User", dto.getDisplayName());
        assertEquals("USER", dto.getRole());

        // Test setters and empty constructor if needed
        UserDto dto2 = new UserDto();
        dto2.setId(id);
        dto2.setUsername("user2");
        assertEquals("user2", dto2.getUsername());
    }
}
