package com.logistics.loadplanner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class OrderDto {
    
    @NotBlank(message = "Order ID is required")
    private String id;
    
    @NotNull(message = "Payout is required")
    @Positive(message = "Payout must be positive")
    @JsonProperty("payout_cents")
    private Long payoutCents;
    
    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    @JsonProperty("weight_lbs")
    private Long weightLbs;
    
    @NotNull(message = "Volume is required")
    @Positive(message = "Volume must be positive")
    @JsonProperty("volume_cuft")
    private Long volumeCuft;
    
    @NotBlank(message = "Origin is required")
    private String origin;
    
    @NotBlank(message = "Destination is required")
    private String destination;
    
    @NotNull(message = "Pickup date is required")
    @JsonProperty("pickup_date")
    private LocalDate pickupDate;
    
    @NotNull(message = "Delivery date is required")
    @JsonProperty("delivery_date")
    private LocalDate deliveryDate;
    
    @NotNull(message = "Hazmat flag is required")
    @JsonProperty("is_hazmat")
    private Boolean isHazmat;
    
    public OrderDto() {
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public Long getPayoutCents() {
        return payoutCents;
    }
    
    public void setPayoutCents(Long payoutCents) {
        this.payoutCents = payoutCents;
    }
    
    public Long getWeightLbs() {
        return weightLbs;
    }
    
    public void setWeightLbs(Long weightLbs) {
        this.weightLbs = weightLbs;
    }
    
    public Long getVolumeCuft() {
        return volumeCuft;
    }
    
    public void setVolumeCuft(Long volumeCuft) {
        this.volumeCuft = volumeCuft;
    }
    
    public String getOrigin() {
        return origin;
    }
    
    public void setOrigin(String origin) {
        this.origin = origin;
    }
    
    public String getDestination() {
        return destination;
    }
    
    public void setDestination(String destination) {
        this.destination = destination;
    }
    
    public LocalDate getPickupDate() {
        return pickupDate;
    }
    
    public void setPickupDate(LocalDate pickupDate) {
        this.pickupDate = pickupDate;
    }
    
    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }
    
    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
    
    public Boolean getIsHazmat() {
        return isHazmat;
    }
    
    public void setIsHazmat(Boolean isHazmat) {
        this.isHazmat = isHazmat;
    }
}
