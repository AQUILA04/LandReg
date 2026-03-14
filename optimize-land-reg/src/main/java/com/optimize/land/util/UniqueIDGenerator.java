package com.optimize.land.util;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

public class UniqueIDGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private UniqueIDGenerator() {
    }

    public static String generateUIN () {
        long date = LocalDate.now().toEpochDay();
        // Generate a positive random long and take modulo to fit within 10 digits
        long random = (SECURE_RANDOM.nextLong() & Long.MAX_VALUE) % 10_000_000_000L;
        long uin  = date + random;

        String uinStr = String.valueOf(uin);
        if (uinStr.length() > 10) {
            uinStr = uinStr.substring(uinStr.length() - 10); // truncate to keep exactly 10 digits
        } else if (uinStr.length() < 10) {
            uinStr = String.format("%010d", uin); // pad with zeros if less than 10 digits
        }

        return "LIN-" + uinStr;
    }

    private static final int MAX_ID_PER_MILLISECOND = 999;
    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    public static String generateRID() {
        long timestamp = System.currentTimeMillis();
        int sequence = COUNTER.getAndIncrement();

        if (sequence > MAX_ID_PER_MILLISECOND) {
            COUNTER.set(0);
            sequence = COUNTER.getAndIncrement();
        }
        //        if (uniqueId.length() > 21) {
//            uniqueId = uniqueId.substring(0, 21); // Tronquer si trop long
//        } else if (uniqueId.length() < 21) {
//            uniqueId = String.format("%-21s", uniqueId).replace(' ', '0'); // Padding
//        }

        return String.format("%d%03d", timestamp, sequence);
    }

}
