package com.ig.devices;

import com.ig.devices.dto.DeviceCreateOrUpdateRequest;
import com.ig.devices.dto.DeviceResponse;
import com.ig.devices.dto.DevicesPageResponse;
import com.ig.devices.dto.ErrorResponse;
import com.ig.devices.model.DeviceState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DevicesApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void cleanDb(@Autowired DataSource ds) throws Exception {
        try (var conn = ds.getConnection(); var stmt = conn.createStatement()) {
            stmt.execute("TRUNCATE TABLE devices RESTART IDENTITY CASCADE");
        }
    }

    @Test
    void shouldSuccessfullyCreateAndGetDevice() {
        var createRequest = new DeviceCreateOrUpdateRequest("name1", "brand1", DeviceState.AVAILABLE);
        var createResponse = restTemplate.postForEntity("/devices", createRequest, DeviceResponse.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        var created = createResponse.getBody();
        assertThat(created).isNotNull();
        assertThat(created.name()).isEqualTo("name1");
        assertThat(created.brand()).isEqualTo("brand1");
        assertThat(created.state()).isEqualTo(DeviceState.AVAILABLE);

        var getResponse = restTemplate.getForEntity("/devices/" + created.id(), DeviceResponse.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        var fetched = getResponse.getBody();
        assertThat(fetched).isNotNull();
        assertThat(fetched.id()).isEqualTo(created.id());
        assertThat(fetched.name()).isEqualTo(created.name());
        assertThat(fetched.brand()).isEqualTo(created.brand());
        assertThat(fetched.state()).isEqualTo(created.state());
    }

    @Test
    void shouldValidateCreateDeviceFields() {
        var createRequest = new DeviceCreateOrUpdateRequest("", null, null);
        var createErrorResponse = restTemplate.postForEntity("/devices", createRequest, ErrorResponse.class);

        assertThat(createErrorResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        var createErrors = createErrorResponse.getBody();
        assertThat(createErrors.errors())
                .hasSameElementsAs(List.of(
                        "name must not be blank",
                        "brand must not be blank",
                        "state must not be null"));
    }

    @Test
    void shouldSuccessfullyFullUpdateDevice() {
        var createRequest = new DeviceCreateOrUpdateRequest("name1", "brand1", DeviceState.AVAILABLE);
        var createResponse = restTemplate.postForEntity("/devices", createRequest, DeviceResponse.class);

        var updateRequest = new DeviceCreateOrUpdateRequest("name2", "brand2", DeviceState.IN_USE);
        var updateResponse = restTemplate.exchange(
                "/devices/" + createResponse.getBody().id(),
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                DeviceResponse.class);

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        var updated = updateResponse.getBody();
        assertThat(updated).isNotNull();
        assertThat(updated.name()).isEqualTo("name2");
        assertThat(updated.brand()).isEqualTo("brand2");
        assertThat(updated.state()).isEqualTo(DeviceState.IN_USE);
    }

    @Test
    void shouldValidateFullUpdateDeviceFields() {
        var createRequest = new DeviceCreateOrUpdateRequest("name1", "brand1", DeviceState.AVAILABLE);
        var createResponse = restTemplate.postForEntity("/devices", createRequest, DeviceResponse.class);

        var updateRequest = new DeviceCreateOrUpdateRequest(null, "", null);
        var updateErrorResponse = restTemplate.exchange(
                "/devices/" + createResponse.getBody().id(),
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                ErrorResponse.class);

        assertThat(updateErrorResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        var updateErrors = updateErrorResponse.getBody();
        assertThat(updateErrors.errors())
                .hasSameElementsAs(List.of(
                        "name must not be blank",
                        "brand must not be blank",
                        "state must not be null"));
    }

    @Test
    void shouldSuccessfullyPartialUpdateDevice() {
        var createRequest = new DeviceCreateOrUpdateRequest("name1", "brand1", DeviceState.AVAILABLE);
        var createResponse = restTemplate.postForEntity("/devices", createRequest, DeviceResponse.class);

        var updateRequest = new DeviceCreateOrUpdateRequest("name2", null, null);
        var updateResponse = restTemplate.exchange(
                "/devices/" + createResponse.getBody().id(),
                HttpMethod.PATCH,
                new HttpEntity<>(updateRequest),
                DeviceResponse.class);

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        var updated = updateResponse.getBody();
        assertThat(updated).isNotNull();
        assertThat(updated.name()).isEqualTo("name2");
        assertThat(updated.brand()).isEqualTo("brand1");
        assertThat(updated.state()).isEqualTo(DeviceState.AVAILABLE);
    }

    @Test
    void shouldNotUpdateDeviceNameOrBrandWhenInUse() {
        var createRequest = new DeviceCreateOrUpdateRequest("name1", "brand1", DeviceState.IN_USE);
        var createResponse = restTemplate.postForEntity("/devices", createRequest, DeviceResponse.class);

        var nameUpdateRequest = new DeviceCreateOrUpdateRequest("name2", "brand2", null);
        var nameUpdateResponse = restTemplate.exchange(
                "/devices/" + createResponse.getBody().id(),
                HttpMethod.PATCH,
                new HttpEntity<>(nameUpdateRequest),
                ErrorResponse.class);

        assertThat(nameUpdateResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        var updateErrors = nameUpdateResponse.getBody();
        assertThat(updateErrors.errors()).hasSameElementsAs(List.of(
                "cannot update name and/or brand while device is in IN_USE state"
        ));
    }

    @Test
    void shouldSuccessfullyDeleteDevice() {
        var createRequest = new DeviceCreateOrUpdateRequest("name1", "brand1", DeviceState.AVAILABLE);
        var createResponse = restTemplate.postForEntity("/devices", createRequest, DeviceResponse.class);

        var deleteResponse = restTemplate.exchange(
                "/devices/" + createResponse.getBody().id(),
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void shouldNotDeleteDeviceInUse() {
        var createRequest = new DeviceCreateOrUpdateRequest("name1", "brand1", DeviceState.IN_USE);
        var createResponse = restTemplate.postForEntity("/devices", createRequest, DeviceResponse.class);

        var deleteResponse = restTemplate.exchange(
                "/devices/" + createResponse.getBody().id(),
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                ErrorResponse.class);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        var deleteErrors = deleteResponse.getBody();
        assertThat(deleteErrors.errors()).hasSameElementsAs(List.of(
                "cannot delete device in IN_USE state"
        ));
    }

    @Test
    void shouldSearchDevices() {
        var device1 = restTemplate.postForEntity("/devices",
                new DeviceCreateOrUpdateRequest("name11", "brand1", DeviceState.AVAILABLE),
                DeviceResponse.class).getBody();
        var device2 = restTemplate.postForEntity("/devices",
                new DeviceCreateOrUpdateRequest("name12", "brand1", DeviceState.IN_USE),
                DeviceResponse.class).getBody();
        var device3 = restTemplate.postForEntity("/devices",
                new DeviceCreateOrUpdateRequest("namer21", "brand2", DeviceState.AVAILABLE),
                DeviceResponse.class).getBody();
        var device4 = restTemplate.postForEntity("/devices",
                new DeviceCreateOrUpdateRequest("name22", "brand2", DeviceState.IN_USE),
                DeviceResponse.class).getBody();

        var getAllResponse = restTemplate.getForEntity("/devices", DevicesPageResponse.class);
        assertThat(getAllResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        var allDevicesPageResponse = getAllResponse.getBody();
        assertThat(allDevicesPageResponse.totalElements()).isEqualTo(4);
        var allDevices = allDevicesPageResponse.content();
        assertThat(allDevices).hasSameElementsAs(List.of(device1, device2, device3, device4));

        var getAllPagedResponse = restTemplate.getForEntity("/devices?size=2&sort=id,asc", DevicesPageResponse.class);
        assertThat(getAllPagedResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        var pagedDevicesPageResponse = getAllPagedResponse.getBody();
        assertThat(pagedDevicesPageResponse.totalElements()).isEqualTo(4);
        assertThat(pagedDevicesPageResponse.totalPages()).isEqualTo(2);
        assertThat(pagedDevicesPageResponse.size()).isEqualTo(2);
        assertThat(pagedDevicesPageResponse.number()).isEqualTo(0);
        var pagedDevices = pagedDevicesPageResponse.content();
        assertThat(pagedDevices).hasSameElementsAs(List.of(device1, device2));

        var getAllByBrandResponse = restTemplate.getForEntity("/devices?brand=brand2", DevicesPageResponse.class);
        assertThat(getAllByBrandResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        var brandPageResponse = getAllByBrandResponse.getBody();
        var devicesByBrand = brandPageResponse.content();
        assertThat(devicesByBrand).hasSameElementsAs(List.of(device3, device4));

        var getAllByStateResponse = restTemplate.getForEntity("/devices?state=IN_USE", DevicesPageResponse.class);
        assertThat(getAllByStateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        var statePageResponse = getAllByStateResponse.getBody();
        var devicesByState = statePageResponse.content();
        assertThat(devicesByState).hasSameElementsAs(List.of(device2, device4));

        var getAllByBrandAndState = restTemplate.getForEntity("/devices?brand=brand1&state=AVAILABLE", DevicesPageResponse.class);
        assertThat(getAllByBrandAndState.getStatusCode()).isEqualTo(HttpStatus.OK);
        var brandAndStatePageResponse = getAllByBrandAndState.getBody();
        var devicesByBrandAndState = brandAndStatePageResponse.content();
        assertThat(devicesByBrandAndState).hasSameElementsAs(List.of(device1));
    }

}
