package com.cms.dto;

import com.cms.enums.IncidentType;

public record IncidentOfficerStatJpqlDto(
        String name,
        long numberOfIncidents
) {
}
