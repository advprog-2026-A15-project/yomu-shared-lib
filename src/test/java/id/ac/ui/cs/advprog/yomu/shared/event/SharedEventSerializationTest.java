package id.ac.ui.cs.advprog.yomu.shared.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SharedEventSerializationTest {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private static final Instant OCCURRED_AT = Instant.parse("2026-01-15T10:00:00Z");
    private static final UUID USER_ID = UUID.fromString("0114b813-a5ef-4c2b-a583-bf2900e4ceea");
    private static final UUID QUIZ_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID BACAAN_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID MISSION_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final UUID SEASON_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");
    private static final UUID CLAN_ID = UUID.fromString("66666666-6666-6666-6666-666666666666");

    @Test
    void quizCompletedEvent_roundTrip_preservesFields() throws Exception {
        var original = new QuizCompletedEvent(USER_ID, QUIZ_ID, "quiz-slug", 4, 5, OCCURRED_AT);

        String json = MAPPER.writeValueAsString(original);
        var restored = MAPPER.readValue(json, QuizCompletedEvent.class);

        assertThat(restored).isEqualTo(original);
        assertThat(json).contains("totalQuestions");
        assertThat(json).contains("quizSlug");
    }

    @Test
    void learningCompletedEvent_roundTrip_preservesFields() throws Exception {
        var original = new LearningCompletedEvent(USER_ID, BACAAN_ID, "bacaan-slug", OCCURRED_AT);

        String json = MAPPER.writeValueAsString(original);
        var restored = MAPPER.readValue(json, LearningCompletedEvent.class);

        assertThat(restored).isEqualTo(original);
        assertThat(json).contains("bacaanSlug");
    }

    @Test
    void achievementUnlockedEvent_roundTrip_preservesFields() throws Exception {
        var original = new AchievementUnlockedEvent(
                USER_ID, "FIRST_READ", "First Read", OCCURRED_AT);

        String json = MAPPER.writeValueAsString(original);
        var restored = MAPPER.readValue(json, AchievementUnlockedEvent.class);

        assertThat(restored).isEqualTo(original);
        assertThat(json).contains("achievementCode");
    }

    @Test
    void dailyMissionCompletedEvent_roundTrip_preservesFields() throws Exception {
        var original = new DailyMissionCompletedEvent(
                USER_ID, MISSION_ID, "Daily Quiz", OCCURRED_AT);

        String json = MAPPER.writeValueAsString(original);
        var restored = MAPPER.readValue(json, DailyMissionCompletedEvent.class);

        assertThat(restored).isEqualTo(original);
        assertThat(json).contains("missionName");
    }

    @Test
    void userRegisteredEvent_roundTrip_preservesFields() throws Exception {
        var original = new UserRegisteredEvent(
                USER_ID, "newuser", "newuser@test.com", OCCURRED_AT);

        String json = MAPPER.writeValueAsString(original);
        var restored = MAPPER.readValue(json, UserRegisteredEvent.class);

        assertThat(restored).isEqualTo(original);
        assertThat(json).contains("username");
    }

    @Test
    void clanPromotedEvent_roundTrip_preservesFields() throws Exception {
        var original = new ClanPromotedEvent(
                SEASON_ID, CLAN_ID, USER_ID, "Dragon Clan", "BRONZE", "SILVER", OCCURRED_AT);

        String json = MAPPER.writeValueAsString(original);
        var restored = MAPPER.readValue(json, ClanPromotedEvent.class);

        assertThat(restored).isEqualTo(original);
        assertThat(json).contains("previousTier");
        assertThat(json).contains("newTier");
    }

    @Test
    void clanDemotedEvent_roundTrip_preservesFields() throws Exception {
        var original = new ClanDemotedEvent(
                SEASON_ID, CLAN_ID, USER_ID, "Dragon Clan", "SILVER", "BRONZE", OCCURRED_AT);

        String json = MAPPER.writeValueAsString(original);
        var restored = MAPPER.readValue(json, ClanDemotedEvent.class);

        assertThat(restored).isEqualTo(original);
        assertThat(json).contains("clanName");
    }

    @Test
    void commentUpdatedEvent_roundTrip_preservesFields() throws Exception {
        var original = new CommentUpdatedEvent(
                USER_ID.toString(),
                BACAAN_ID.toString(),
                "root",
                "comment-1",
                "Edited",
                OCCURRED_AT);

        String json = MAPPER.writeValueAsString(original);
        var restored = MAPPER.readValue(json, CommentUpdatedEvent.class);

        assertThat(restored).isEqualTo(original);
        assertThat(json).contains("commentContent");
    }

    @Test
    void commentDeletedEvent_roundTrip_preservesFields() throws Exception {
        var original = new CommentDeletedEvent(
                USER_ID.toString(),
                BACAAN_ID.toString(),
                "root",
                "comment-1",
                "Deleted content",
                OCCURRED_AT);

        String json = MAPPER.writeValueAsString(original);
        var restored = MAPPER.readValue(json, CommentDeletedEvent.class);

        assertThat(restored).isEqualTo(original);
        assertThat(json).contains("commentId");
    }

    @Test
    void leagueActivityEvent_roundTrip_preservesFields() throws Exception {
        var original = new LeagueActivityEvent(
                USER_ID,
                UUID.fromString("77777777-7777-7777-7777-777777777777"),
                UUID.fromString("88888888-8888-8888-8888-888888888888"),
                "QUIZ_WIN",
                OCCURRED_AT);

        String json = MAPPER.writeValueAsString(original);
        var restored = MAPPER.readValue(json, LeagueActivityEvent.class);

        assertThat(restored).isEqualTo(original);
        assertThat(json).contains("activityType");
    }

    @Test
    void bacaanUpdatedEvent_roundTrip_preservesFields() throws Exception {
        var original = new BacaanUpdatedEvent(
                BACAAN_ID,
                "Judul Bacaan",
                "UPDATED",
                OCCURRED_AT);

        String json = MAPPER.writeValueAsString(original);
        var restored = MAPPER.readValue(json, BacaanUpdatedEvent.class);

        assertThat(restored).isEqualTo(original);
        assertThat(json).contains("action");
    }

    @Test
    void commentCreatedEvent_roundTrip_preservesFields() throws Exception {
        var original = new CommentCreatedEvent(
                USER_ID.toString(),
                BACAAN_ID.toString(),
                "root",
                "comment-1",
                "Hello",
                OCCURRED_AT);

        String json = MAPPER.writeValueAsString(original);
        var restored = MAPPER.readValue(json, CommentCreatedEvent.class);

        assertThat(restored).isEqualTo(original);
        assertThat(json).contains("commentContent");
    }
}
