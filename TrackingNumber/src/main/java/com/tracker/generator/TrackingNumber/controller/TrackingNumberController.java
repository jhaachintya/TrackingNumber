package com.tracker.generator.TrackingNumber.controller;

import com.tracker.generator.TrackingNumber.service.UniqueTrackingNumberGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrackingNumberController {
    Logger logger
            = LoggerFactory.getLogger(TrackingNumberController.class);
    @Autowired
    UniqueTrackingNumberGenerator uniqueTrackingNumberGenerator;

    @Value("${node.id}")
    private String nodeId;
    @GetMapping("/trackingnumber")
    public String generateUniqueTrackingNumber(){
        logger.info("generateUniqueTrackingNumber for " + nodeId);
        String inputWithChecksum = "";
        try {
            String number =  nodeId  + uniqueTrackingNumberGenerator.getUUID();
            int[] values = uniqueTrackingNumberGenerator.mapEachCharacter(number);
            int checksum = uniqueTrackingNumberGenerator.calculateChecksum(values);
            inputWithChecksum = number + checksum;
        } catch (InterruptedException e) {
            logger.error("Error occured in "+"generateUniqueTrackingNumber method with message" + e.getMessage());
            throw new RuntimeException(e);
        }
        return inputWithChecksum;
    }

}
