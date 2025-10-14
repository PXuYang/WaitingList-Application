package com.sean.restaurant.parties.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class CreatePartyRequest {
    @NotBlank public String kind;          // "WAITLIST" or "RESERVATION"
    @NotBlank public String name;
    @Min(1)  public int size;
    public String phone;                   // optional
    public Integer quotedMinutes;          // only for WAITLIST (optional)
    public String reservationAt;           // ISO-8601, only for RESERVATION, e.g. 2025-10-11T19:00:00Z
}

