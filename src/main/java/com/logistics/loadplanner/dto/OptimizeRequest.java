package com.logistics.loadplanner.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public class OptimizeRequest {
    
    @NotNull(message = "Truck is required")
    @Valid
    private TruckDto truck;
    
    @NotNull(message = "Orders list is required")
    @Size(max = 25, message = "Maximum 25 orders allowed")
    @Valid
    private List<OrderDto> orders;
    
    public OptimizeRequest() {
    }
    
    public OptimizeRequest(TruckDto truck, List<OrderDto> orders) {
        this.truck = truck;
        this.orders = orders;
    }
    
    public TruckDto getTruck() {
        return truck;
    }
    
    public void setTruck(TruckDto truck) {
        this.truck = truck;
    }
    
    public List<OrderDto> getOrders() {
        return orders;
    }
    
    public void setOrders(List<OrderDto> orders) {
        this.orders = orders;
    }
}
