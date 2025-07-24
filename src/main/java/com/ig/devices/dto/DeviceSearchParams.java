package com.ig.devices.dto;

import com.ig.devices.model.DeviceState;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.RequestParam;

@Schema(description = "Search parameters for querying devices")
public record DeviceSearchParams(
        @Schema(description = "Filter by brand", example = "Some brand")
        @RequestParam(required = false)
        String brand,

        @Schema(description = "Filter by state", example = "AVAILABLE")
        @RequestParam(required = false)
        DeviceState state
) {
}

