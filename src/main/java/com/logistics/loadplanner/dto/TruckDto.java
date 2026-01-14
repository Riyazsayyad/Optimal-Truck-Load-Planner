package com.logistics.loadplanner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class TruckDto {
    
    @NotBlank(message = "Truck ID is required")
    private String id;
    
    @NotNull(message = "Max weight is required")
    @Positive(message = "Max weight must be positive")
    @JsonProperty("max_weight_lbs")
    private Long maxWeightLbs;
    
    @NotNull(message = "Max volume is required")
    @Positive(message = "Max volume must be positive")
    @JsonProperty("max_volume_cuft")
    private Long maxVolumeCuft;
    
    public TruckDto() {
    }
    
    public TruckDto(String id, Long maxWeightLbs, Long maxVolumeCuft) {
        this.id = id;
        this.maxWeightLbs = maxWeightLbs;
        this.maxVolumeCuft = maxVolumeCuft;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public Long getMaxWeightLbs() {
        return maxWeightLbs;
    }
    
    public void setMaxWeightLbs(Long maxWeightLbs) {
        this.maxWeightLbs = maxWeightLbs;
    }
    
    public Long getMaxVolumeCuft() {
        return maxVolumeCuft;
    }
    
    public void setMaxVolumeCuft(Long maxVolumeCuft) {
        this.maxVolumeCuft = maxVolumeCuft;
    }
}
