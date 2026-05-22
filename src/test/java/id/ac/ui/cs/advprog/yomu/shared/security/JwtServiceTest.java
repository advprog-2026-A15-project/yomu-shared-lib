package id.ac.ui.cs.advprog.yomu.shared.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String TEST_SECRET =
            "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final String USERNAME = "testuser";
    private static final String USER_ID = "0114b813-a5ef-4c2b-a583-bf2900e4ceea";
    private static final String ROLE = "PELAJAR";

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86_400_000L);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", 604_800_000L);
    }

    @Test
    void generateAccessToken_extractsClaims() {
        String token = jwtService.generateAccessToken(USERNAME, authClaims());

        assertThat(jwtService.extractUsername(token)).isEqualTo(USERNAME);
        assertThat(jwtService.extractUserId(token)).isEqualTo(USER_ID);
        assertThat(jwtService.extractRole(token)).isEqualTo(ROLE);
        assertThat(jwtService.extractTokenType(token)).isEqualTo("access");
    }

    @Test
    void isAccessTokenValid_validAccessToken_returnsTrue() {
        String token = jwtService.generateAccessToken(USERNAME, authClaims());

        assertThat(jwtService.isAccessTokenValid(token)).isTrue();
        assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    @Test
    void generateRefreshToken_isNotAccessToken() {
        String token = jwtService.generateRefreshToken(USERNAME, authClaims());

        assertThat(jwtService.isRefreshTokenValid(token)).isTrue();
        assertThat(jwtService.isAccessTokenValid(token)).isFalse();
        assertThat(jwtService.extractTokenType(token)).isEqualTo("refresh");
    }

    @Test
    void isRefreshTokenValid_onAccessToken_returnsFalse() {
        String accessToken = jwtService.generateAccessToken(USERNAME, authClaims());

        assertThat(jwtService.isRefreshTokenValid(accessToken)).isFalse();
    }

    @Test
    void isAccessTokenValid_onRefreshToken_returnsFalse() {
        String refreshToken = jwtService.generateRefreshToken(USERNAME, authClaims());

        assertThat(jwtService.isAccessTokenValid(refreshToken)).isFalse();
    }

    @Test
    void isTokenValid_malformedToken_returnsFalse() {
        assertThat(jwtService.isTokenValid("not.a.jwt")).isFalse();
        assertThat(jwtService.isAccessTokenValid("not.a.jwt")).isFalse();
        assertThat(jwtService.isRefreshTokenValid("not.a.jwt")).isFalse();
    }

    @Test
    void isTokenValid_tamperedToken_returnsFalse() {
        String token = jwtService.generateAccessToken(USERNAME, authClaims());
        String tampered = token.substring(0, token.length() - 1) + (token.endsWith("a") ? "b" : "a");

        assertThat(jwtService.isTokenValid(tampered)).isFalse();
        assertThat(jwtService.isAccessTokenValid(tampered)).isFalse();
    }

    @Test
    void isAccessTokenValid_expiredToken_returnsFalse() {
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1_000L);
        String expiredToken = jwtService.generateAccessToken(USERNAME, authClaims());
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86_400_000L);

        assertThat(jwtService.isAccessTokenValid(expiredToken)).isFalse();
        assertThat(jwtService.isTokenValid(expiredToken)).isFalse();
    }

    @Test
    void generateToken_defaultTypeIsAccess() {
        Map<String, Object> claims = new HashMap<>(authClaims());
        claims.remove("type");

        String token = jwtService.generateToken(USERNAME, claims);

        assertThat(jwtService.extractTokenType(token)).isEqualTo("access");
        assertThat(jwtService.isAccessTokenValid(token)).isTrue();
    }

    @Test
    void extractExpirationInstant_validToken_returnsFutureInstant() {
        String token = jwtService.generateAccessToken(USERNAME, authClaims());

        assertThat(jwtService.extractExpirationInstant(token)).isAfter(java.time.Instant.now());
    }

    private static Map<String, Object> authClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", USER_ID);
        claims.put("role", ROLE);
        return claims;
    }
}
