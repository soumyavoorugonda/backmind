package com.backmind.auth;

import java.util.UUID;

public record AuthenticatedUser(UUID id, String email) {
}
