package io.devflow.organizations.controller;

import io.devflow.organizations.dto.CreateOrganizationRequest;
import io.devflow.organizations.dto.OrganizationDto;
import io.devflow.organizations.service.OrganizationService;
import io.devflow.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orgs")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @PostMapping
    public ResponseEntity<OrganizationDto> createOrganization(
            @CurrentUser UUID userId,
            @Valid @RequestBody CreateOrganizationRequest request) {
        OrganizationDto dto = organizationService.createOrganization(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/{name}")
    public ResponseEntity<OrganizationDto> getOrganization(@PathVariable String name) {
        return ResponseEntity.ok(organizationService.getOrganization(name));
    }
}
