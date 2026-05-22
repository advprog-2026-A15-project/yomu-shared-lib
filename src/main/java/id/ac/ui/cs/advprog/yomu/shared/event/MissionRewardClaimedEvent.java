package id.ac.ui.cs.advprog.yomu.shared.event;

import java.time.Instant;
import java.util.UUID;

public record MissionRewardClaimedEvent(
    UUID userId,
    UUID missionId,
    String missionName,
    int rewardPoints,
    Instant occurredAt
) {}
