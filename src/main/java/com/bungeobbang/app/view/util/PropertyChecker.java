package com.bungeobbang.app.view.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class PropertyChecker {

    // Database configuration
    @Value("${DB_URL}")
    private String dbUrl;

    @Value("${DB_USERNAME}")
    private String dbUsername;

    @Value("${DB_PASSWORD}")
    private String dbPassword;

    // Email configuration
    @Value("${MAIL_USERNAME}")
    private String mailUsername;

    @Value("${MAIL_PASSWORD}")
    private String mailPassword;

    // API keys
    @Value("${map.api.key}")
    private String mapApiKey;

    // Channel keys
    @Value("${channel.key.shinhan}")
    private String channelKeyShinhan;

    @Value("${channel.key.woori}")
    private String channelKeyWoori;

    @Value("${channel.key.virtualAccount}")
    private String channelKeyVirtualAccount;

    @Value("${channel.key.toss}")
    private String channelKeyToss;

    // PortOne keys
    @Value("${imp.init.key}")
    private String impInitKey;

    @Value("${imp.key}")
    private String impKey;

    @Value("${imp.secret}")
    private String impSecret;

    @PostConstruct
    public void init() {
        // Logging the values to verify they are correctly loaded
        System.out.println("DB URL is: " + dbUrl);
        System.out.println("DB Username is: " + dbUsername);
        System.out.println("DB Password is: " + dbPassword);
        System.out.println("Mail Username is: " + mailUsername);
        System.out.println("Mail Password is: " + mailPassword);
        System.out.println("Map API Key is: " + mapApiKey);
        System.out.println("Channel Key (Shinhan) is: " + channelKeyShinhan);
        System.out.println("Channel Key (Woori) is: " + channelKeyWoori);
        System.out.println("Channel Key (Virtual Account) is: " + channelKeyVirtualAccount);
        System.out.println("Channel Key (Toss) is: " + channelKeyToss);
        System.out.println("IMP Init Key is: " + impInitKey);
        System.out.println("IMP Key is: " + impKey);
        System.out.println("IMP Secret is: " + impSecret);
    }
}
