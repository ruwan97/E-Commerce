package com.rc.ecommerce.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class PlaceOrderRequestDTO {
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    @Email
    private String email;
    @NotNull
    private String phone;
    @NotNull
    private String address;
    @NotNull
    private String city;
    @NotNull
    private String country;
    @NotNull
    private String orderId;
    @NotNull
    private List<OrderItemDTO> items;
    @NotNull
    private String currency;
    @NotNull
    private String recurrence;
    @NotNull
    private String duration;
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal amount;

    public boolean isValidRecurrence() {
        return isValidPeriod(recurrence);
    }

    public boolean isValidDuration() {
        if ("Forever".equalsIgnoreCase(duration)) {
            return true;
        }
        return isValidPeriod(duration);
    }

    private boolean isValidPeriod(String period) {
        String[] validUnits = {"Week", "Month", "Year"};
        String regex = "^\\d+\\s(" + String.join("|", validUnits) + ")$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(period);
        return matcher.matches();
    }
}
