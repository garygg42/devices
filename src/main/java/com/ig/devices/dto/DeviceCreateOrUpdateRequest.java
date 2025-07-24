package com.ig.devices.dto;

import com.ig.devices.model.DeviceState;
import com.ig.devices.validation.DeviceValidationGroups.CreateOrUpdate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request object used to create or update a device")
public record DeviceCreateOrUpdateRequest(
        @Schema(description = "Device name", example = "Some name")
        @NotBlank(groups = CreateOrUpdate.class)
        String name,

        @Schema(description = "Device brand", example = "Some brand")
        @NotBlank(groups = CreateOrUpdate.class)
        String brand,

        @Schema(description = "Device state", example = "AVAILABLE")
        @NotNull(groups = CreateOrUpdate.class)
        DeviceState state
) {
}
