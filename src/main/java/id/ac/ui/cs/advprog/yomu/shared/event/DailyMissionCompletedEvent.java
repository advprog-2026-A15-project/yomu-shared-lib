package id.ac.ui.cs.advprog.yomu.shared.event;

import java.time.Instant;
import java.util.UUID;

public record DailyMissionCompletedEvent(
    UUID userId,
    UUID missionId,
    String missionName,
    Instant occurredAt
) {}
