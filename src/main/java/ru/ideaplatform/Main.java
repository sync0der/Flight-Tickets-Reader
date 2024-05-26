package ru.ideaplatform;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    private static final String ticketsJson;

    static {
        try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("tickets.json")) {
            assert inputStream != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                StringBuilder jsonBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                    jsonBuilder.append(line);
                ticketsJson = jsonBuilder.toString();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read tickets.json", e);
        }
    }

    public static void main(String[] args) {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                .registerTypeAdapter(LocalTime.class, new LocalTimeDeserializer())
                .create();
        TicketsCollection ticketsCollection = gson.fromJson(ticketsJson, TicketsCollection.class);
        List<Ticket> tickets = ticketsCollection.getTickets();

        List<Ticket> ticketList = filterByOriginAndDestination(tickets, "VVO", "TLV");
        printMinFlightTimeForCarrier(ticketList, "TK");
        printMinFlightTimeForCarrier(ticketList, "S7");
        printMinFlightTimeForCarrier(ticketList, "SU");
        printMinFlightTimeForCarrier(ticketList, "BA");

        System.out.println("\nAverage Price for flights between VVO and TLV: " + getAveragePrice(ticketList));
        System.out.println("Median for flights between VVO and TLV: " + getMedian(ticketList));
        System.out.println("The difference between the average price and the median: " + (getAveragePrice(ticketList) - getMedian(ticketList)));

    }


    private static List<Ticket> filterByOriginAndDestination(List<Ticket> ticketsList, String origin, String destination) {
        return ticketsList.stream()
                .filter(ticket -> ticket.getOrigin().equals(origin) && ticket.getDestination().equals(destination))
                .collect(Collectors.toList());
    }

    private static void printMinFlightTimeForCarrier(List<Ticket> ticketList, String carrier) {
        ticketList.stream()
                .filter(ticket -> ticket.getCarrier().equals(carrier))
                .min(Comparator.comparing(Main::calculateFlightDuration))
                .ifPresent(ticket -> {
                    Duration duration = calculateFlightDuration(ticket);
                    long hours = duration.toHours();
                    long minutes = duration.toMinutes() % 60;
                    System.out.printf("Carrier: %s | Minimum flight time: %d hours %d minutes%n", carrier, hours, minutes);
                    System.out.println(ticket + "\n");
                });
    }

    private static Duration calculateFlightDuration(Ticket ticket) {
        LocalDateTime departureDateTime = LocalDateTime.of(ticket.getDepartureDate(), ticket.getDepartureTime());
        LocalDateTime arrivalDateTime = LocalDateTime.of(ticket.getArrivalDate(), ticket.getArrivalTime());
        return Duration.between(departureDateTime, arrivalDateTime);
    }

    private static double getAveragePrice(List<Ticket> ticketList) {
        double allPrices = 0;
        for (Ticket ticket : ticketList)
            allPrices += ticket.getPrice();
        return allPrices / ticketList.size();
    }

    private static double getMedian(List<Ticket> ticketList) {
        List<Ticket> sortedList = ticketList.stream()
                .sorted(Comparator.comparing(Ticket::getPrice)).toList();
        int size = sortedList.size();
        if (size % 2 == 0)
            return (sortedList.get(size / 2 - 1).getPrice() + sortedList.get(size / 2).getPrice()) / 2;
        return sortedList.get(size / 2).getPrice();
    }


}


