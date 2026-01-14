package com.logistics.loadplanner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class OptimizeResponse {
    
    @JsonProperty("truck_id")
    private String truckId;
    
    @JsonProperty("selected_order_ids")
    private List<String> selectedOrderIds;
    
    @JsonProperty("total_payout_cents")
    private Long totalPayoutCents;
    
    @JsonProperty("total_weight_lbs")
    private Long totalWeightLbs;
    
    @JsonProperty("total_volume_cuft")
    private Long totalVolumeCuft;
    
    @JsonProperty("utilization_weight_percent")
    private Double utilizationWeightPercent;
    
    @JsonProperty("utilization_volume_percent")
    private Double utilizationVolumePercent;
    
    public OptimizeResponse() {
    }
    
    public OptimizeResponse(String truckId, List<String> selectedOrderIds, 
                           Long totalPayoutCents, Long totalWeightLbs, 
                           Long totalVolumeCuft, Long maxWeightLbs, Long maxVolumeCuft) {
        this.truckId = truckId;
        this.selectedOrderIds = selectedOrderIds;
        this.totalPayoutCents = totalPayoutCents;
        this.totalWeightLbs = totalWeightLbs;
        this.totalVolumeCuft = totalVolumeCuft;
        this.utilizationWeightPercent = maxWeightLbs > 0 
            ? (totalWeightLbs * 100.0 / maxWeightLbs) : 0.0;
        this.utilizationVolumePercent = maxVolumeCuft > 0 
            ? (totalVolumeCuft * 100.0 / maxVolumeCuft) : 0.0;
    }
    
    public String getTruckId() {
        return truckId;
    }
    
    public void setTruckId(String truckId) {
        this.truckId = truckId;
    }
    
    public List<String> getSelectedOrderIds() {
        return selectedOrderIds;
    }
    
    public void setSelectedOrderIds(List<String> selectedOrderIds) {
        this.selectedOrderIds = selectedOrderIds;
    }
    
    public Long getTotalPayoutCents() {
        return totalPayoutCents;
    }
    
    public void setTotalPayoutCents(Long totalPayoutCents) {
        this.totalPayoutCents = totalPayoutCents;
    }
    
    public Long getTotalWeightLbs() {
        return totalWeightLbs;
    }
    
    public void setTotalWeightLbs(Long totalWeightLbs) {
        this.totalWeightLbs = totalWeightLbs;
    }
    
    public Long getTotalVolumeCuft() {
        return totalVolumeCuft;
    }
    
    public void setTotalVolumeCuft(Long totalVolumeCuft) {
        this.totalVolumeCuft = totalVolumeCuft;
    }
    
    public Double getUtilizationWeightPercent() {
        return utilizationWeightPercent;
    }
    
    public void setUtilizationWeightPercent(Double utilizationWeightPercent) {
        this.utilizationWeightPercent = utilizationWeightPercent;
    }
    
    public Double getUtilizationVolumePercent() {
        return utilizationVolumePercent;
    }
    
    public void setUtilizationVolumePercent(Double utilizationVolumePercent) {
        this.utilizationVolumePercent = utilizationVolumePercent;
    }
}
