package com.logistics.loadplanner.controller;

import com.logistics.loadplanner.dto.OptimizeRequest;
import com.logistics.loadplanner.dto.OptimizeResponse;
import com.logistics.loadplanner.service.LoadOptimizationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/load-optimizer")
public class LoadOptimizerController {
    
    private final LoadOptimizationService optimizationService;
    
    public LoadOptimizerController(LoadOptimizationService optimizationService) {
        this.optimizationService = optimizationService;
    }
    
    @PostMapping("/optimize")
    public ResponseEntity<?> optimize(@Valid @RequestBody OptimizeRequest request, 
                                      BindingResult bindingResult) {
        // Validate request structure
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder("Validation failed: ");
            bindingResult.getFieldErrors().forEach(error -> 
                errorMsg.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append("; ")
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(errorMsg.toString().trim()));
        }
        
        // Additional validation
        if (request.getTruck() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Truck information is required"));
        }
        
        if (request.getOrders() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Orders list is required"));
        }
        
        // Validate order count
        int orderCount = request.getOrders().size();
        if (orderCount > 25) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(new ErrorResponse(String.format("Maximum 25 orders allowed, received %d", orderCount)));
        }
        
        // Validate truck constraints
        if (request.getTruck().getMaxWeightLbs() == null || request.getTruck().getMaxWeightLbs() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Truck max_weight_lbs must be positive"));
        }
        
        if (request.getTruck().getMaxVolumeCuft() == null || request.getTruck().getMaxVolumeCuft() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Truck max_volume_cuft must be positive"));
        }
        
        // Validate each order
        for (int i = 0; i < request.getOrders().size(); i++) {
            var order = request.getOrders().get(i);
            
            // Validate dates
            if (order.getPickupDate() != null && order.getDeliveryDate() != null) {
                if (order.getPickupDate().isAfter(order.getDeliveryDate())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse(String.format(
                            "Order %s (index %d): pickup_date must be before or equal to delivery_date", 
                            order.getId(), i)));
                }
            }
            
            // Validate order values
            if (order.getWeightLbs() != null && order.getWeightLbs() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(String.format("Order %s: weight_lbs must be positive", order.getId())));
            }
            
            if (order.getVolumeCuft() != null && order.getVolumeCuft() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(String.format("Order %s: volume_cuft must be positive", order.getId())));
            }
            
            if (order.getPayoutCents() != null && order.getPayoutCents() < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(String.format("Order %s: payout_cents must be non-negative", order.getId())));
            }
        }
        
        try {
            OptimizeResponse response = optimizationService.optimize(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal server error: " + e.getMessage()));
        }
    }
    
    private static class ErrorResponse {
        private final String error;
        
        public ErrorResponse(String error) {
            this.error = error;
        }
        
        public String getError() {
            return error;
        }
    }
}
