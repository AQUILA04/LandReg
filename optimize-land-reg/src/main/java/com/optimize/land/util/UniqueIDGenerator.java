package com.optimize.land.util;

import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

public class UniqueIDGenerator {

    private UniqueIDGenerator() {
    }

    public static String generateUIN () {
        long date = LocalDate.now().toEpochDay();
        long random = Long.parseLong(RandomStringUtils.randomNumeric(10));
        long uin  = date + random;
        return "LIN-"+uin;
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
