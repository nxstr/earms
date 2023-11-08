package cz.cvut.kbss.ear.ms.environment;



import cz.cvut.kbss.ear.ms.model.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Generator {

    private static final Random RAND = new Random();

    public static int randomInt() {
        return RAND.nextInt();
    }

    public static int randomInt(int max) {
        return RAND.nextInt(max);
    }

    public static int randomInt(int min, int max) {
        assert min >= 0;
        assert min < max;

        int result;
        do {
            result = randomInt(max);
        } while (result < min);
        return result;
    }

    public static boolean randomBoolean() {
        return RAND.nextBoolean();
    }

    public static User generateUser(){
        final User user = new User();
        user.setFirstName("FirstName" + randomInt());
        user.setLastName("LastName" + randomInt());
        user.setUsername("username" + randomInt());
        user.setEmail("LastName" + randomInt() + "@kbss.felk.cvut.cz");
        user.setPassword("password1");
        return user;
    }

    public static Admin generateAdmin(){
        final Admin user = new Admin();
        user.setUsername("username" + randomInt(200, 300));
        user.setPassword("password1");
        return user;
    }

    public static PollOption generatePollOption(Event event) {

        long startDate = LocalDate.now().toEpochDay();
        long endDate = LocalDate.of(2023, 12, 31).toEpochDay();
        long random1 = ThreadLocalRandom.current().nextLong(startDate, endDate);
        LocalDate date = LocalDate.ofEpochDay(random1);
        final Random random = new Random();
        LocalTime time = LocalTime.of(random.nextInt(24), random.nextInt(60), random.nextInt(60));
        final PollOption d = new PollOption(date, time, event);
        return d;
    }

    public static Event generateEvent(){
        final Event event = new Event();
        final Random random = new Random();
        final User user = generateUser();
        event.setOwner(user);
        event.setDetail("test");
        event.setName("name" + randomInt());
        event.setLocation("address" + randomInt());
        long start = LocalDate.of(2023, 1, 25).toEpochDay();
        long endDate = LocalDate.of(2023, 12, 31).toEpochDay();
        long random1 = ThreadLocalRandom.current().nextLong(start, endDate);
        LocalDate startDate = LocalDate.ofEpochDay(random1);
        event.setOpenDueTo(startDate);
        List<PollOption> options = new ArrayList<>();
        options.add(generatePollOption(event));
        event.setOptions(options);
        return event;
    }

}
