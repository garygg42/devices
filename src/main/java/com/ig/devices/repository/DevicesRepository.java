package com.ig.devices.repository;

import com.ig.devices.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DevicesRepository extends JpaRepository<Device, Long>, JpaSpecificationExecutor<Device> {

}
