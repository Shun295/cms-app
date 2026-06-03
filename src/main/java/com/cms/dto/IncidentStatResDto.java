package com.cms.dto;

import java.util.List;

public record IncidentStatResDto(
        String Title,
        List<com.cms.enums.IncidentType> label,
        List<Long> data
) {
}
