package com.ig.devices.dto;

import java.util.List;

public record ErrorResponse(
        List<String> errors
) {
}
