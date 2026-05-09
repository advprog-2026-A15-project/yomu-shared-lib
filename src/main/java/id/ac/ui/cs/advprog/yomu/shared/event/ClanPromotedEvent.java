package id.ac.ui.cs.advprog.yomu.shared.event;

import java.time.Instant;
import java.util.UUID;

public record ClanPromotedEvent(
    UUID seasonId,
    UUID clanId,
    UUID userId,
    String clanName,
    String previousTier,
    String newTier,
    Instant occurredAt
) {}
