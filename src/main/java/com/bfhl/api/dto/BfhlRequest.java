package com.bfhl.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Request DTO for POST /bfhl.
 * Exactly one of the five fields must be non-null.
 */
@Data
public class BfhlRequest {

    private Integer fibonacci;

    private List<Integer> prime;

    private List<Integer> lcm;

    private List<Integer> hcf;

    @JsonProperty("AI")
    private String ai;
}
