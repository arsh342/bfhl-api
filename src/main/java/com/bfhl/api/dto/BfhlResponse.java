package com.bfhl.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Unified response DTO for all POST /bfhl operations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BfhlResponse {

    @JsonProperty("is_success")
    private boolean success;

    @JsonProperty("official_email")
    private String officialEmail;

    private Object data;
}
