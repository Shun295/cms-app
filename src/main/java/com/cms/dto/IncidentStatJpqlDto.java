package com.cms.dto;

import com.cms.enums.IncidentType;

public record IncidentStatJpqlDto(
        IncidentType typeOfIncident,
        long numberOfIncident
) {
}
