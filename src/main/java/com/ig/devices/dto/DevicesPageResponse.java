package com.ig.devices.dto;

import java.util.List;

public record DevicesPageResponse(
        List<DeviceResponse> content,
        int number,
        int size,
        int totalPages,
        long totalElements
) {}