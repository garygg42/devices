package com.ig.devices.repository;

import com.ig.devices.dto.DeviceSearchParams;
import com.ig.devices.model.Device;
import com.ig.devices.model.Device_;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

public class DeviceSpecs {

    public static Specification<Device> fromSearchParams(DeviceSearchParams params) {
        return (root, query, builder) -> {
            var predicates = new ArrayList<Predicate>();

            if (StringUtils.hasText(params.brand())) {
                predicates.add(builder.equal(root.get(Device_.brand), params.brand()));
            }
            if (params.state() != null) {
                predicates.add(builder.equal(root.get(Device_.state), params.state()));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
