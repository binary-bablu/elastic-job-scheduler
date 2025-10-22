package com.scheduler.manager.dto;

import java.util.List;

public class RetryConfig {
	
	 private int maxAttempts = 0;
     private String backOffStrategy = "FIXED";//Default
     private int initialDelayMs = 60000;  // 1 minute default
     private int multiplier = 2;
     private List<Integer> nonRetryableExitCodes = List.of(2, 126, 127, -998, -999);
     
	 public int getMaxAttempts() {
		 return maxAttempts;
	 }
	 public void setMaxAttempts(int maxAttempts) {
		 this.maxAttempts = maxAttempts;
	 }
	 
	 public String getBackOffStrategy() {
		return backOffStrategy;
	 }
	 public void setBackOffStrategy(String backOffStrategy) {
		 this.backOffStrategy = backOffStrategy;
	 }
	 public int getInitialDelayMs() {
		 return initialDelayMs;
	 }
	 public void setInitialDelayMs(int initialDelayMs) {
		 this.initialDelayMs = initialDelayMs;
	 }
	 public int getMultiplier() {
		 return this.multiplier;
	 }
	 public void setMultiplier(int multiplier) {
		 this.multiplier = multiplier;
	 }
	 public List<Integer> getNonRetryableExitCodes() {
		 return this.nonRetryableExitCodes;
	 }
	 public void setNonRetryableExitCodes(List<Integer> nonRetryableExitCodes) {
		 this.nonRetryableExitCodes = nonRetryableExitCodes;
	 }
}
