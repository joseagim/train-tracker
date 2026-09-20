package com.joseagim.traintracker.config;

import com.joseagim.traintracker.entity.*;
import com.joseagim.traintracker.repository.*;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Log4j2
@Component
public class DataSeeder implements CommandLineRunner {

    private final StationRepository stationRepository;
    private final RouteRepository routeRepository;
    private final TrainRepository trainRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final int DAYS = 30;
    private static final int[] MINUTE_OPTIONS = { 0, 15, 30, 45 };
    // Each slot: min and max hour (inclusive) for the random draw
    private static final int[][] TIME_SLOTS = {
            { 6, 8 },    // morning
            { 10, 13 },  // midday
            { 17, 19 },  // afternoon
            { 20, 22 }   // night (skipped for routes too long to fit before the cutoff)
    };

    // Latest acceptable arrival time: 01:00 the next day (in minutes since midnight)
    private static final int LATEST_ARRIVAL_MINUTES_OF_DAY = 25 * 60;

    private final Random random = new Random();

    public DataSeeder(
            StationRepository stationRepository,
            RouteRepository routeRepository,
            TrainRepository trainRepository,
            TripRepository tripRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.stationRepository = stationRepository;
        this.routeRepository = routeRepository;
        this.trainRepository = trainRepository;
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String @NonNull ... args) {
        if (stationRepository.count() > 0) {
            log.info("Seed skipped: database already has data.");
            return;
        }

        log.info("Seeding demo data...");

        Train ave = createTrain("AVE S-112", 6, 50);
        Train iryo = createTrain("Iryo S-106", 8, 40);
        List<Train> trains = List.of(ave, iryo);

        // Original 13 stations
        Station madrid = createStation("Madrid Atocha", "Madrid");
        Station zaragoza = createStation("Zaragoza Delicias", "Zaragoza");
        Station tarragona = createStation("Tarragona", "Tarragona");
        Station barcelona = createStation("Barcelona Sants", "Barcelona");
        Station cuenca = createStation("Cuenca", "Cuenca");
        Station requenaUtiel = createStation("Requena-Utiel", "Requena");
        Station valencia = createStation("Valencia Joaquín Sorolla", "Valencia");
        Station puertollano = createStation("Puertollano", "Puertollano");
        Station cordoba = createStation("Córdoba", "Córdoba");
        Station sevilla = createStation("Sevilla Santa Justa", "Sevilla");
        Station zamora = createStation("Zamora", "Zamora");
        Station ourense = createStation("Ourense", "Ourense");
        Station santiago = createStation("Santiago de Compostela", "Santiago de Compostela");

        // New stations
        Station granada = createStation("Granada", "Granada");
        Station murcia = createStation("Murcia del Carmen", "Murcia");
        Station alicante = createStation("Alicante Terminal", "Alicante");
        Station albacete = createStation("Albacete Los Llanos", "Albacete");

        // Start today: trips are generated from today through DAYS - 1 days ahead
        LocalDate startDate = LocalDate.now();

        // Original 4 route pairs
        seedRoutePair("Madrid-Barcelona",
                List.of(madrid, zaragoza, tarragona, barcelona),
                List.of(90, 60, 40),
                trains, startDate);

        seedRoutePair("Madrid-Valencia",
                List.of(madrid, cuenca, requenaUtiel, valencia),
                List.of(55, 35, 35),
                trains, startDate);

        seedRoutePair("Madrid-Sevilla",
                List.of(madrid, puertollano, cordoba, sevilla),
                List.of(70, 60, 45),
                trains, startDate);

        seedRoutePair("Madrid-Santiago",
                List.of(madrid, zamora, ourense, santiago),
                List.of(80, 90, 30),
                trains, startDate);

        // New route pairs
        seedRoutePair("Madrid-Granada",
                List.of(madrid, puertollano, cordoba, granada),
                List.of(70, 55, 75),
                trains, startDate);

        seedRoutePair("Barcelona-Sevilla",
                List.of(barcelona, tarragona, zaragoza, madrid, cordoba, sevilla),
                List.of(40, 95, 105, 100, 45),
                trains, startDate);

        seedRoutePair("Madrid-MurciaCosta",
                List.of(madrid, cuenca, valencia, alicante, murcia),
                List.of(55, 60, 70, 45),
                trains, startDate);

        seedRoutePair("Madrid-MurciaInterior",
                List.of(madrid, cuenca, albacete, murcia),
                List.of(55, 35, 80),
                trains, startDate);

        createUser("Admin", "admin@traintracker.com", "600000000", "00000000A", "admin1234", UserRole.ROLE_ADMIN);
        createUser("User", "user@traintracker.com", "600000001", "00000001B", "user1234", UserRole.ROLE_USER);

        log.info("Seed completed: 17 stations, 16 routes (8 pairs), 2 trains, {} trips, 2 users.",
                16 * DAYS * TIME_SLOTS.length);
    }

    private void seedRoutePair(String baseName, List<Station> stations, List<Integer> segmentMinutes,
                               List<Train> trains, LocalDate startDate) {

        Route outbound = buildRoute(baseName, stations, segmentMinutes);

        List<Station> reversedStations = new ArrayList<>(stations);
        java.util.Collections.reverse(reversedStations);
        List<Integer> reversedSegments = new ArrayList<>(segmentMinutes);
        java.util.Collections.reverse(reversedSegments);

        String[] parts = baseName.split("-");
        String returnName = parts[1] + "-" + parts[0];
        Route inbound = buildRoute(returnName, reversedStations, reversedSegments);

        createTrips(outbound, trains, startDate);
        createTrips(inbound, trains, startDate);
    }

    private Route buildRoute(String name, List<Station> stations, List<Integer> segmentMinutes) {
        Route route = new Route();
        route.setName(name);

        int cumulative = 0;
        for (int i = 0; i < stations.size(); i++) {
            RouteStation rs = new RouteStation();
            rs.setStation(stations.get(i));
            rs.setStopOrder(i + 1);
            rs.setMinutesFromStart(cumulative);
            route.addRouteStation(rs);
            if (i < segmentMinutes.size()) {
                cumulative += segmentMinutes.get(i);
            }
        }
        return routeRepository.save(route);
    }

    private void createTrips(Route route, List<Train> trains, LocalDate startDate) {
        int totalDurationMinutes = getTotalDuration(route);

        for (int day = 0; day < DAYS; day++) {
            LocalDate date = startDate.plusDays(day);

            for (int i = 0; i < TIME_SLOTS.length; i++) {
                int[] slot = TIME_SLOTS[i];
                boolean isNightSlot = (i == TIME_SLOTS.length - 1);

                // Skip the night slot entirely if the route is too long to arrive
                // before the cutoff, instead of generating an unrealistic
                // middle-of-the-night arrival
                if (isNightSlot && !fitsBeforeCutoff(slot[0], totalDurationMinutes)) {
                    continue;
                }

                LocalTime time = isNightSlot
                        ? randomTimeInAdjustedNightSlot(slot, totalDurationMinutes)
                        : randomTimeInSlot(slot[0], slot[1]);

                Train train = trains.get(random.nextInt(trains.size()));

                Trip trip = new Trip();
                trip.setRoute(route);
                trip.setTrain(train);
                trip.setDepartureTime(LocalDateTime.of(date, time));
                trip.setStatus(TripStatus.ON_TIME);
                trip.setSeats("1".repeat(train.getTotalSeats()));
                tripRepository.save(trip);
            }
        }
    }

    // Total route duration: accumulated minutes to the last stop
    private int getTotalDuration(Route route) {
        return route.getRouteStations().stream()
                .mapToInt(RouteStation::getMinutesFromStart)
                .max()
                .orElse(0);
    }

    // Whether departing at the slot's earliest hour would still arrive before the cutoff
    private boolean fitsBeforeCutoff(int earliestHour, int totalDurationMinutes) {
        return earliestHour * 60 + totalDurationMinutes <= LATEST_ARRIVAL_MINUTES_OF_DAY;
    }

    // Caps the night slot's latest departure hour so the arrival stays before the cutoff
    private LocalTime randomTimeInAdjustedNightSlot(int[] slot, int totalDurationMinutes) {
        int latestDepartureMinutesOfDay = LATEST_ARRIVAL_MINUTES_OF_DAY - totalDurationMinutes;
        int latestDepartureHour = Math.min(slot[1], latestDepartureMinutesOfDay / 60);
        return randomTimeInSlot(slot[0], latestDepartureHour);
    }

    private LocalTime randomTimeInSlot(int fromHour, int toHour) {
        int hour = fromHour + random.nextInt(toHour - fromHour + 1);
        int minute = MINUTE_OPTIONS[random.nextInt(MINUTE_OPTIONS.length)];
        return LocalTime.of(hour, minute);
    }

    private Station createStation(String name, String city) {
        Station station = new Station();
        station.setName(name);
        station.setCity(city);
        return stationRepository.save(station);
    }

    private Train createTrain(String type, int bogeys, int seatsByBogey) {
        Train train = new Train();
        train.setType(type);
        train.setBogeys(bogeys);
        train.setSeatsByBogey(seatsByBogey);
        return trainRepository.save(train);
    }

    private void createUser(String firstName, String email, String phone, String dni,
                            String rawPassword, UserRole role) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName("Demo");
        user.setEmail(email);
        user.setPhoneNumber(phone);
        user.setDni(dni);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        userRepository.save(user);
    }

}