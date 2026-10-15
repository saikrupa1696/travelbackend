package com.travelmate.dto.itinerary;

import java.math.BigDecimal;

public class BudgetBreakdownResponse {
    private BigDecimal totalBudget;
    private BigDecimal totalEstimatedCost;
    private BigDecimal remainingBudget;
    private String budgetMode;
    private Integer travellerCount;
    private BigDecimal perPersonBudget;
    private BigDecimal perPersonEstimatedCost;

    private BigDecimal accommodationEst;
    private BigDecimal foodEst;
    private BigDecimal transportEst;
    private BigDecimal activitiesEst;
    private BigDecimal miscEst;

    private String budgetStatus;
    private String advice;
    private Double percentageUsed;
    private BigDecimal dailyEstimatedSpending;
    private String disclaimer;
    private String currency = "INR";

    public BudgetBreakdownResponse() {}

    public BudgetBreakdownResponse(BigDecimal totalBudget, BigDecimal totalEstimatedCost, BigDecimal remainingBudget,
                                   String budgetMode, Integer travellerCount, BigDecimal perPersonBudget,
                                   BigDecimal perPersonEstimatedCost, BigDecimal accommodationEst, BigDecimal foodEst,
                                   BigDecimal transportEst, BigDecimal activitiesEst, BigDecimal miscEst,
                                   String budgetStatus, String advice, Double percentageUsed,
                                   BigDecimal dailyEstimatedSpending, String disclaimer, String currency) {
        this.totalBudget = totalBudget;
        this.totalEstimatedCost = totalEstimatedCost;
        this.remainingBudget = remainingBudget;
        this.budgetMode = budgetMode;
        this.travellerCount = travellerCount;
        this.perPersonBudget = perPersonBudget;
        this.perPersonEstimatedCost = perPersonEstimatedCost;
        this.accommodationEst = accommodationEst;
        this.foodEst = foodEst;
        this.transportEst = transportEst;
        this.activitiesEst = activitiesEst;
        this.miscEst = miscEst;
        this.budgetStatus = budgetStatus;
        this.advice = advice;
        this.percentageUsed = percentageUsed;
        this.dailyEstimatedSpending = dailyEstimatedSpending;
        this.disclaimer = disclaimer;
        this.currency = currency != null ? currency : "INR";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private BigDecimal totalBudget;
        private BigDecimal totalEstimatedCost;
        private BigDecimal remainingBudget;
        private String budgetMode;
        private Integer travellerCount;
        private BigDecimal perPersonBudget;
        private BigDecimal perPersonEstimatedCost;
        private BigDecimal accommodationEst;
        private BigDecimal foodEst;
        private BigDecimal transportEst;
        private BigDecimal activitiesEst;
        private BigDecimal miscEst;
        private String budgetStatus;
        private String advice;
        private Double percentageUsed;
        private BigDecimal dailyEstimatedSpending;
        private String disclaimer;
        private String currency = "INR";

        public Builder totalBudget(BigDecimal totalBudget) { this.totalBudget = totalBudget; return this; }
        public Builder totalEstimatedCost(BigDecimal totalEstimatedCost) { this.totalEstimatedCost = totalEstimatedCost; return this; }
        public Builder remainingBudget(BigDecimal remainingBudget) { this.remainingBudget = remainingBudget; return this; }
        public Builder budgetMode(String budgetMode) { this.budgetMode = budgetMode; return this; }
        public Builder travellerCount(Integer travellerCount) { this.travellerCount = travellerCount; return this; }
        public Builder perPersonBudget(BigDecimal perPersonBudget) { this.perPersonBudget = perPersonBudget; return this; }
        public Builder perPersonEstimatedCost(BigDecimal perPersonEstimatedCost) { this.perPersonEstimatedCost = perPersonEstimatedCost; return this; }
        public Builder accommodationEst(BigDecimal accommodationEst) { this.accommodationEst = accommodationEst; return this; }
        public Builder foodEst(BigDecimal foodEst) { this.foodEst = foodEst; return this; }
        public Builder transportEst(BigDecimal transportEst) { this.transportEst = transportEst; return this; }
        public Builder activitiesEst(BigDecimal activitiesEst) { this.activitiesEst = activitiesEst; return this; }
        public Builder miscEst(BigDecimal miscEst) { this.miscEst = miscEst; return this; }
        public Builder budgetStatus(String budgetStatus) { this.budgetStatus = budgetStatus; return this; }
        public Builder advice(String advice) { this.advice = advice; return this; }
        public Builder percentageUsed(Double percentageUsed) { this.percentageUsed = percentageUsed; return this; }
        public Builder dailyEstimatedSpending(BigDecimal dailyEstimatedSpending) { this.dailyEstimatedSpending = dailyEstimatedSpending; return this; }
        public Builder disclaimer(String disclaimer) { this.disclaimer = disclaimer; return this; }
        public Builder currency(String currency) { this.currency = currency; return this; }

        public BudgetBreakdownResponse build() {
            return new BudgetBreakdownResponse(totalBudget, totalEstimatedCost, remainingBudget,
                    budgetMode, travellerCount, perPersonBudget, perPersonEstimatedCost,
                    accommodationEst, foodEst, transportEst, activitiesEst, miscEst, budgetStatus, advice,
                    percentageUsed, dailyEstimatedSpending, disclaimer, currency);
        }
    }

    public BigDecimal getTotalBudget() { return totalBudget; }
    public void setTotalBudget(BigDecimal totalBudget) { this.totalBudget = totalBudget; }
    public BigDecimal getTotalEstimatedCost() { return totalEstimatedCost; }
    public void setTotalEstimatedCost(BigDecimal totalEstimatedCost) { this.totalEstimatedCost = totalEstimatedCost; }
    public BigDecimal getRemainingBudget() { return remainingBudget; }
    public void setRemainingBudget(BigDecimal remainingBudget) { this.remainingBudget = remainingBudget; }
    public String getBudgetMode() { return budgetMode; }
    public void setBudgetMode(String budgetMode) { this.budgetMode = budgetMode; }
    public Integer getTravellerCount() { return travellerCount; }
    public void setTravellerCount(Integer travellerCount) { this.travellerCount = travellerCount; }
    public BigDecimal getPerPersonBudget() { return perPersonBudget; }
    public void setPerPersonBudget(BigDecimal perPersonBudget) { this.perPersonBudget = perPersonBudget; }
    public BigDecimal getPerPersonEstimatedCost() { return perPersonEstimatedCost; }
    public void setPerPersonEstimatedCost(BigDecimal perPersonEstimatedCost) { this.perPersonEstimatedCost = perPersonEstimatedCost; }
    public BigDecimal getAccommodationEst() { return accommodationEst; }
    public void setAccommodationEst(BigDecimal accommodationEst) { this.accommodationEst = accommodationEst; }
    public BigDecimal getFoodEst() { return foodEst; }
    public void setFoodEst(BigDecimal foodEst) { this.foodEst = foodEst; }
    public BigDecimal getTransportEst() { return transportEst; }
    public void setTransportEst(BigDecimal transportEst) { this.transportEst = transportEst; }
    public BigDecimal getActivitiesEst() { return activitiesEst; }
    public void setActivitiesEst(BigDecimal activitiesEst) { this.activitiesEst = activitiesEst; }
    public BigDecimal getMiscEst() { return miscEst; }
    public void setMiscEst(BigDecimal miscEst) { this.miscEst = miscEst; }
    public String getBudgetStatus() { return budgetStatus; }
    public void setBudgetStatus(String budgetStatus) { this.budgetStatus = budgetStatus; }
    public String getAdvice() { return advice; }
    public void setAdvice(String advice) { this.advice = advice; }
    public Double getPercentageUsed() { return percentageUsed; }
    public void setPercentageUsed(Double percentageUsed) { this.percentageUsed = percentageUsed; }
    public BigDecimal getDailyEstimatedSpending() { return dailyEstimatedSpending; }
    public void setDailyEstimatedSpending(BigDecimal dailyEstimatedSpending) { this.dailyEstimatedSpending = dailyEstimatedSpending; }
    public String getDisclaimer() { return disclaimer; }
    public void setDisclaimer(String disclaimer) { this.disclaimer = disclaimer; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
