package com.ig.devices.controller;

import com.ig.devices.dto.DeviceCreateOrUpdateRequest;
import com.ig.devices.dto.DeviceResponse;
import com.ig.devices.dto.DeviceSearchParams;
import com.ig.devices.service.DevicesService;
import com.ig.devices.validation.DeviceValidationGroups.CreateOrUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/devices")
@Tag(name = "Devices", description = "Devices CRUD endpoints")
public class DevicesController {

    private final DevicesService service;

    public DevicesController(DevicesService service) {
        this.service = service;
    }

    @Operation(summary = "Create new device", responses = {
            @ApiResponse(responseCode = "201", description = "Device created",
                    content = @Content(
                            schema = @Schema(implementation = DeviceResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Valid device",
                                            value = """
                                                    {
                                                        name: "Device name",
                                                        brand: "Device brand",
                                                        state: "IN_USE"
                                                    }
                                                    """)
                            })),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PostMapping
    public ResponseEntity<DeviceResponse> create(
            @RequestBody @Validated(CreateOrUpdate.class) DeviceCreateOrUpdateRequest request) {
        var createdDevice = service.create(request);
        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdDevice.id())
                .toUri();
        return ResponseEntity.created(location).body(createdDevice);
    }

    @Operation(summary = "Get device by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Found the device",
                    content = @Content(schema = @Schema(implementation = DeviceResponse.class))),
            @ApiResponse(responseCode = "404", description = "Device not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @Operation(summary = "Search for devices with optional filters and pagination",
            description = "Returns all devices if called without parameters")
    @GetMapping
    public ResponseEntity<Page<DeviceResponse>> search(
            @ParameterObject @ModelAttribute DeviceSearchParams params,
            @ParameterObject @PageableDefault(size = Integer.MAX_VALUE) Pageable pageable) {
        return ResponseEntity.ok(service.search(params, pageable));
    }

    @Operation(summary = "Fully update device")
    @PutMapping("/{id}")
    public ResponseEntity<DeviceResponse> fullUpdate(
            @PathVariable Long id,
            @RequestBody @Validated(CreateOrUpdate.class) DeviceCreateOrUpdateRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @Operation(summary = "Partially update device")
    @PatchMapping("/{id}")
    public ResponseEntity<DeviceResponse> partialUpdate(
            @PathVariable Long id,
            @RequestBody DeviceCreateOrUpdateRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @Operation(summary = "Delete device by ID", responses = {
            @ApiResponse(responseCode = "204", description = "Deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Device cannot be deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Device not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
