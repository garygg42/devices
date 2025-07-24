package com.ig.devices.dto;

import com.ig.devices.model.DeviceState;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object representing a device")
public record DeviceResponse(
        @Schema(description = "Device ID", example = "1")
        Long id,

        @Schema(description = "Device name", example = "Some brand")
        String name,

        @Schema(description = "Device brand", example = "Some name")
        String brand,

        @Schema(description = "Device state", example = "AVAILABLE")
        DeviceState state
) {
}
