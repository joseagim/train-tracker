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

@Log4j2
@Component
public class DataSeeder implements CommandLineRunner {

    private final StationRepository stationRepository;
    private final RouteRepository routeRepository;
    private final TrainRepository trainRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final int DAYS = 14;
    private static final LocalTime[] OUTBOUND_TIMES = { LocalTime.of(7, 0), LocalTime.of(13, 0), LocalTime.of(20, 0) };
    private static final LocalTime[] RETURN_TIMES = { LocalTime.of(9, 0), LocalTime.of(15, 0), LocalTime.of(22, 0) };

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

        // Trenes
        Train ave = createTrain("AVE S-112", 6, 50);
        Train iryo = createTrain("Iryo S-106", 8, 40);
        List<Train> trains = List.of(ave, iryo);

        // Estaciones
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

        LocalDate startDate = LocalDate.now().plusDays(1);

        // Madrid - Barcelona (vía Zaragoza y Tarragona)
        seedRoutePair("Madrid-Barcelona",
                List.of(madrid, zaragoza, tarragona, barcelona),
                List.of(90, 60, 40),
                trains, startDate);

        // Madrid - Valencia (vía Cuenca y Requena-Utiel)
        seedRoutePair("Madrid-Valencia",
                List.of(madrid, cuenca, requenaUtiel, valencia),
                List.of(55, 35, 35),
                trains, startDate);

        // Madrid - Sevilla (vía Puertollano y Córdoba)
        seedRoutePair("Madrid-Sevilla",
                List.of(madrid, puertollano, cordoba, sevilla),
                List.of(70, 60, 45),
                trains, startDate);

        // Madrid - Galicia (vía Zamora y Ourense)
        seedRoutePair("Madrid-Santiago",
                List.of(madrid, zamora, ourense, santiago),
                List.of(80, 90, 30),
                trains, startDate);

        // Usuarios demo
        createUser("Admin", "admin@traintracker.com", "600000000", "00000000A", "admin1234", UserRole.ROLE_ADMIN);
        createUser("User", "user@traintracker.com", "600000001", "00000001B", "user1234", UserRole.ROLE_USER);

        log.info("Seed completed: 13 stations, 8 routes (4 pairs), 2 trains, {} trips, 2 users.",
                8 * DAYS * OUTBOUND_TIMES.length);
    }

    /**
     * Crea una ruta de ida (con las paradas y minutos dados) y su vuelta (mismas
     * paradas invertidas, mismos tramos invertidos), y genera los viajes de los
     * próximos DAYS días para ambos sentidos.
     */
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

        createTrips(outbound, trains, startDate, OUTBOUND_TIMES);
        createTrips(inbound, trains, startDate, RETURN_TIMES);
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

    private void createTrips(Route route, List<Train> trains, LocalDate startDate, LocalTime[] times) {
        int trainIndex = 0;
        for (int day = 0; day < DAYS; day++) {
            for (LocalTime time : times) {
                Train train = trains.get(trainIndex % trains.size());
                trainIndex++;

                Trip trip = new Trip();
                trip.setRoute(route);
                trip.setTrain(train);
                trip.setDepartureTime(LocalDateTime.of(startDate.plusDays(day), time));
                trip.setStatus(TripStatus.ON_TIME);
                trip.setSeats("1".repeat(train.getTotalSeats()));
                tripRepository.save(trip);
            }
        }
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