package com.logistics.loadplanner.service;

import com.logistics.loadplanner.dto.OrderDto;
import com.logistics.loadplanner.dto.OptimizeRequest;
import com.logistics.loadplanner.dto.OptimizeResponse;
import com.logistics.loadplanner.dto.TruckDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class LoadOptimizationService {
    
    /**
     * Optimized order state with pre-computed values for faster access.
     */
    private static class OrderState {
        final int index;
        final long payoutCents;
        final long weightLbs;
        final long volumeCuft;
        final String origin;
        final String destination;
        final LocalDate pickupDate;
        final LocalDate deliveryDate;
        final boolean isHazmat;
        final int routeHash; // Pre-computed hash for route comparison
        
        OrderState(int index, OrderDto order) {
            this.index = index;
            this.payoutCents = order.getPayoutCents();
            this.weightLbs = order.getWeightLbs();
            this.volumeCuft = order.getVolumeCuft();
            this.origin = order.getOrigin();
            this.destination = order.getDestination();
            this.pickupDate = order.getPickupDate();
            this.deliveryDate = order.getDeliveryDate();
            this.isHazmat = order.getIsHazmat();
            // Pre-compute route hash for faster comparison
            this.routeHash = (origin + "|" + destination).hashCode();
        }
    }
    
    /**
     * Solution state for DP table.
     */
    private static class Solution {
        final long payoutCents;
        final long weightLbs;
        final long volumeCuft;
        final int mask;
        final int routeHash; // Cached route hash for compatibility checks
        final boolean hasHazmat; // Cached hazmat flag
        final LocalDate minPickupDate; // Cached min pickup for time window checks
        final LocalDate maxDeliveryDate; // Cached max delivery for time window checks
        
        Solution(long payoutCents, long weightLbs, long volumeCuft, int mask,
                 int routeHash, boolean hasHazmat, LocalDate minPickup, LocalDate maxDelivery) {
            this.payoutCents = payoutCents;
            this.weightLbs = weightLbs;
            this.volumeCuft = volumeCuft;
            this.mask = mask;
            this.routeHash = routeHash;
            this.hasHazmat = hasHazmat;
            this.minPickupDate = minPickup;
            this.maxDeliveryDate = maxDelivery;
        }
        
        // Constructor for empty solution
        Solution() {
            this.payoutCents = 0;
            this.weightLbs = 0;
            this.volumeCuft = 0;
            this.mask = 0;
            this.routeHash = 0;
            this.hasHazmat = false;
            this.minPickupDate = null;
            this.maxDeliveryDate = null;
        }
    }
    
    public OptimizeResponse optimize(OptimizeRequest request) {
        TruckDto truck = request.getTruck();
        List<OrderDto> orders = request.getOrders();
        
        // Edge case: empty orders
        if (orders == null || orders.isEmpty()) {
            return new OptimizeResponse(
                truck.getId(),
                Collections.emptyList(),
                0L, 0L, 0L,
                truck.getMaxWeightLbs(), truck.getMaxVolumeCuft()
            );
        }
        
        // Validate order count
        if (orders.size() > 25) {
            throw new IllegalArgumentException("Maximum 25 orders allowed");
        }
        
        // Convert to internal state
        List<OrderState> orderStates = new ArrayList<>();
        for (int i = 0; i < orders.size(); i++) {
            orderStates.add(new OrderState(i, orders.get(i)));
        }
        
        int n = orderStates.size();
        int maxMask = 1 << n;
        long maxWeight = truck.getMaxWeightLbs();
        long maxVolume = truck.getMaxVolumeCuft();
        
        // DP: dp[mask] = best solution for subset represented by mask
        Solution[] dp = new Solution[maxMask];
        dp[0] = new Solution();
        
        Solution bestSolution = dp[0];
        long bestPayout = 0;
        
        // Iterate through all possible subsets
        for (int mask = 1; mask < maxMask; mask++) {
            Solution best = null;
            long bestCandidatePayout = -1;
            
            // Try adding each order to the subset
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) == 0) continue;
                
                int prevMask = mask ^ (1 << i);
                Solution prev = dp[prevMask];
                
                if (prev == null) continue;
                
                OrderState order = orderStates.get(i);
                
                // Early pruning: Check weight and volume constraints first (fastest check)
                long newWeight = prev.weightLbs + order.weightLbs;
                long newVolume = prev.volumeCuft + order.volumeCuft;
                
                if (newWeight > maxWeight || newVolume > maxVolume) {
                    continue;
                }
                
                // Optimized compatibility check using cached values
                if (!isCompatibleFast(order, prev)) {
                    continue;
                }
                
                long newPayout = prev.payoutCents + order.payoutCents;
                
                // Early pruning: Skip if this can't improve best solution
                if (newPayout <= bestPayout) {
                    continue;
                }
                
                // Compute new cached values
                int newRouteHash = prev.mask == 0 ? order.routeHash : prev.routeHash;
                boolean newHasHazmat = prev.hasHazmat || order.isHazmat;
                LocalDate newMinPickup = prev.mask == 0 ? order.pickupDate : 
                    (order.pickupDate.isBefore(prev.minPickupDate) ? order.pickupDate : prev.minPickupDate);
                LocalDate newMaxDelivery = prev.mask == 0 ? order.deliveryDate : 
                    (order.deliveryDate.isAfter(prev.maxDeliveryDate) ? order.deliveryDate : prev.maxDeliveryDate);
                
                Solution candidate = new Solution(newPayout, newWeight, newVolume, mask,
                    newRouteHash, newHasHazmat, newMinPickup, newMaxDelivery);
                
                if (best == null || candidate.payoutCents > best.payoutCents) {
                    best = candidate;
                    bestCandidatePayout = candidate.payoutCents;
                }
            }
            
            dp[mask] = best;
            
            // Update best solution
            if (best != null && best.payoutCents > bestPayout) {
                bestSolution = best;
                bestPayout = best.payoutCents;
            }
        }
        
        // Extract selected order IDs
        List<String> selectedOrderIds = new ArrayList<>();
        if (bestSolution.mask > 0) {
            for (int i = 0; i < n; i++) {
                if ((bestSolution.mask & (1 << i)) != 0) {
                    selectedOrderIds.add(orders.get(i).getId());
                }
            }
        }
        
        return new OptimizeResponse(
            truck.getId(),
            selectedOrderIds,
            bestSolution.payoutCents,
            bestSolution.weightLbs,
            bestSolution.volumeCuft,
            truck.getMaxWeightLbs(),
            truck.getMaxVolumeCuft()
        );
    }
    
    /**
     * Optimized compatibility check using cached values from previous solution.
     * Avoids extracting orders from mask and iterating through them.
     * 
     * Compatibility rules:
     * 1. Same origin → destination route
     * 2. Time windows don't conflict (pickup_date ≤ delivery_date for all, no overlapping conflicts)
     * 3. Hazmat isolation (if order is hazmat, no other orders should be in the same load)
     */
    private boolean isCompatibleFast(OrderState newOrder, Solution prev) {
        // If no existing orders, it's always compatible
        if (prev.mask == 0) {
            return true;
        }
        
        // Check route compatibility using pre-computed hash
        // If prev has orders, routeHash must match newOrder's routeHash
        if (prev.routeHash != newOrder.routeHash) {
            return false;
        }
        
        // Check hazmat isolation (fast check using cached flag)
        if (newOrder.isHazmat) {
            // Hazmat orders must be isolated - cannot have any existing orders
            return false;
        }
        if (prev.hasHazmat) {
            // Cannot add non-hazmat to existing hazmat orders
            return false;
        }
        
        // Check time window compatibility using cached min/max dates
        // If new order's pickup is after existing orders' max delivery, conflict
        if (newOrder.pickupDate.isAfter(prev.maxDeliveryDate)) {
            return false;
        }
        // If existing orders' min pickup is after new order's delivery, conflict
        if (prev.minPickupDate.isAfter(newOrder.deliveryDate)) {
            return false;
        }
        
        return true;
    }
}
