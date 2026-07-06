package com.backmind.auth.dto;

import java.util.UUID;

public record SignupResponse(UUID id, String email) {
}
