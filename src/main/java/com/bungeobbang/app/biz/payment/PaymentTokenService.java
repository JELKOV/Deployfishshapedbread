package com.bungeobbang.app.biz.payment;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentTokenService {
    @Value("${imp.key}")
    private String impKey;

    @Value("${imp.secret}")
    private String impSecret;
    //private final static String IMP_KEY = "API 키 재발급";
    //private final static String IMP_SECRET = "API 키 재발급";

    public PaymentInfoDTO getAccessToken(PaymentInfoDTO paymentInfoDTO) {
        log.info("[GetAccessToken] 시작");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.iamport.kr/users/getToken"))
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString("{\"imp_key\":\"" + impKey + "\",\"imp_secret\":\"" + impSecret + "\"}"))
                .build();

        HttpResponse<String> response;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(response.body());
            JSONObject responseObject = (JSONObject) jsonObject.get("response");
            String token = (String) responseObject.get("access_token");
            log.info("[GetAccessToken 발급 받은 토큰 값] : {}", token);
            paymentInfoDTO.setToken(token);
        } catch (IOException | InterruptedException | ParseException e) {
            e.printStackTrace();
        }
        return paymentInfoDTO;
    }
}
