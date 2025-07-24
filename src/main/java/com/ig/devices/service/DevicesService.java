package com.ig.devices.service;

import com.ig.devices.dto.DeviceCreateOrUpdateRequest;
import com.ig.devices.dto.DeviceResponse;
import com.ig.devices.dto.DeviceSearchParams;
import com.ig.devices.exception.DeviceStateValidationException;
import com.ig.devices.mapper.DeviceMapper;
import com.ig.devices.model.Device;
import com.ig.devices.model.DeviceState;
import com.ig.devices.repository.DeviceSpecs;
import com.ig.devices.repository.DevicesRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class DevicesService {

    private final DevicesRepository repository;
    private final DeviceMapper mapper;

    public DevicesService(DevicesRepository repository, DeviceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public DeviceResponse create(DeviceCreateOrUpdateRequest request) {
        var device = mapper.toModel(request);
        return mapper.toResponse(repository.save(device));
    }

    public DeviceResponse get(Long id) {
        return mapper.toResponse(getById(id));
    }

    public Page<DeviceResponse> search(DeviceSearchParams params, Pageable pageable) {
        var spec = DeviceSpecs.fromSearchParams(params);
        return repository.findAll(spec, pageable).map(mapper::toResponse);
    }

    @Transactional
    public DeviceResponse update(Long id, DeviceCreateOrUpdateRequest request) {
        var device = getById(id);

        if (device.getState() == DeviceState.IN_USE) {
            if ((StringUtils.hasText(request.name()) && !device.getName().equals(request.name()))
                    || (StringUtils.hasText(request.brand()) && !device.getBrand().equals(request.brand()))) {
                throw new DeviceStateValidationException("device must not be in IN_USE state");
            }
        }
        mapper.updateModelFromRequest(request, device);
        repository.save(device);

        return mapper.toResponse(device);
    }

    public void delete(Long id) {
        var device = getById(id);
        if (device.getState() == DeviceState.IN_USE) {
            throw new DeviceStateValidationException("device must not be in IN_USE state");
        }
        repository.delete(device);
    }

    private Device getById(Long id) {
        return repository.findById(id).orElseThrow(EntityNotFoundException::new);
    }
}
