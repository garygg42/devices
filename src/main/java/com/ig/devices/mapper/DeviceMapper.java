package com.ig.devices.mapper;

import com.ig.devices.dto.DeviceCreateOrUpdateRequest;
import com.ig.devices.dto.DeviceResponse;
import com.ig.devices.dto.DevicesPageResponse;
import com.ig.devices.model.Device;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface DeviceMapper {

    DeviceResponse toResponse(Device device);

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    Device toModel(DeviceCreateOrUpdateRequest deviceCreateOrUpdateRequest);

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE,
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateModelFromRequest(DeviceCreateOrUpdateRequest update, @MappingTarget Device destination);

    DevicesPageResponse toDevicesPageResponse(Page<DeviceResponse> page);
}
