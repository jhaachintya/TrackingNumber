package com.tracker.generator.TrackingNumber.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@Component
public class UniqueTrackingNumberGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final BlockingQueue<String> uuidQueue;
    private final Random random = new Random();
    Logger logger = LoggerFactory.getLogger(UniqueTrackingNumberGenerator.class);
    private int queueCapacity = 1000;
    private int batchsize = 100;

    public UniqueTrackingNumberGenerator() {
        this.uuidQueue = new LinkedBlockingQueue<>(queueCapacity);
        startUUIDProducer();
    }

    private static int sumDigits(int num) {
        int sum = 0;
        while (num > 0) {
            sum += num % 10;
            num /= 10;
        }
        return sum;
    }



    /**
     * Start a thread to produce UUIDs and put them in the queue
     */
    private void startUUIDProducer() {
        new Thread(() -> {
            while (true) {
                try {
                    List<String> uuids = generateUUIDs(batchsize); // Generate in batches of 100
                    for (String uuid : uuids) {
                        uuidQueue.put(uuid); // Blocking put
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }

    /**
     * @param batchSize fixed size for which will generate unique id at a time.
     * @return list of generated alphanumeric list
     */
    private List<String> generateUUIDs(int batchSize) {
        List<String> uuids = new ArrayList<>(batchSize);
        for (int i = 0; i < batchSize; i++) {
            String uuid = generateReadableUUID();
            uuids.add(uuid);
        }
        return uuids;
    }

    /**
     * @return generate readable tracking number
     */
    private String generateReadableUUID() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    /**
     * @return UUID from the queue
     * @throws InterruptedException
     */
    public String getUUID() throws InterruptedException {
        return uuidQueue.take(); // Blocking take
    }

    /**
     * @param input is the unique string generate for which we need to calculate checksum.
     * @return the generated checksum value.
     */
    public int[] mapEachCharacter(String input) {
        return input.replaceAll("[^a-zA-Z0-9]", "")
                .toUpperCase()
                .chars()
                .map(c -> {
                    if (Character.isDigit(c)) {
                        return c - '0';
                    } else {
                        return 10 + (c - 'A'); // Assuming A=10, B=11, ..., Z=35
                    }
                })
                .toArray();
    }

    /**
     * @param values number for which checksum needs to be calculated
     * @return the digit which fulfills Luhn validation
     */
    public int calculateChecksum(int[] values) {
        int sum = 0;
        boolean doubleDigit = false;
        for(int i = 0; i < values.length; i++) {
            int digit = values[i];
            if (doubleDigit) {
                digit *= 2;
            }
            digit = sumDigits(digit);
            sum += digit;
            doubleDigit = !doubleDigit;
        }
        int checksum = (sum * 9) % 10;
        return checksum;
    }


    // method to consume records from blocking queue in parallel and test the unique number generation.
    public static void main(String[] args) {
        UniqueTrackingNumberGenerator generator = new UniqueTrackingNumberGenerator();
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    while (true) {
                        String uuid = generator.getUUID();
                        System.out.println(Thread.currentThread().getName() + " consumed UUID: " + uuid);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }
}