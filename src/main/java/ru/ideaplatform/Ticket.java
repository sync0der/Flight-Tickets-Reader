package ru.ideaplatform;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class Ticket {
    @SerializedName("origin")
    private String origin;
    @SerializedName("origin_name")
    private String originName;
    @SerializedName("destination")
    private String destination;
    @SerializedName("destination_name")
    private String destinationName;
    @SerializedName("departure_date")
    private LocalDate departureDate;
    @SerializedName("departure_time")
    private LocalTime departureTime;
    @SerializedName("arrival_date")
    private LocalDate arrivalDate;
    @SerializedName("arrival_time")
    private LocalTime arrivalTime;
    @SerializedName("carrier")
    private String carrier;
    @SerializedName("stops")
    private int stops;
    @SerializedName("price")
    private double price;

}
