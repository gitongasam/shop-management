package com.devsam.shopmanagement.service.Payment.mpesa;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.apache.tomcat.util.codec.binary.Base64;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
public class MpesaClient {

    private final RestTemplate rest;
    @Value("${mpesa.consumer.key}") private String consumerKey;
    @Value("${mpesa.consumer.secret}") private String consumerSecret;
    @Value("${mpesa.business.shortcode}") private String businessShortCode; // paybill or till
    @Value("${mpesa.passkey}") private String mpesaPasskey;
    @Value("${mpesa.callback.url}") private String callbackUrl;
    @Value("${mpesa.auth.url}") private String oauthUrl; // e.g. https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials
    @Value("${mpesa.stk.push.url}") private String stkPushUrl; // e.g. https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest

    public MpesaClient(RestTemplate rest) { this.rest = rest; }

    public String getAccessToken() {
        String auth = consumerKey + ":" + consumerSecret;
        String encoded = Base64.encodeBase64String(auth.getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + encoded);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> resp = rest.exchange(oauthUrl, HttpMethod.GET, entity, Map.class);
        if (resp.getStatusCode().is2xxSuccessful()) {
            return String.valueOf(resp.getBody().get("access_token"));
        }
        throw new RuntimeException("Failed to obtain Mpesa access token");
    }

    public ResponseEntity<Map> stkPush(String phone, String amount, String accountRef, String transactionDesc) {
        String token = getAccessToken();

        // timestamp and password
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String passwordStr = businessShortCode + mpesaPasskey + timestamp;
        String password = Base64.encodeBase64String(passwordStr.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("BusinessShortCode", businessShortCode);
        body.put("Password", password);
        body.put("Timestamp", timestamp);
        body.put("TransactionType", "CustomerPayBillOnline");
        body.put("Amount", amount);
        body.put("PartyA", phone);
        body.put("PartyB", businessShortCode);
        body.put("PhoneNumber", phone);
        body.put("CallBackURL", callbackUrl);
        body.put("AccountReference", accountRef);
        body.put("TransactionDesc", transactionDesc);
        HttpEntity<Map<String,Object>> entity = new HttpEntity<>(body, headers);
        System.out.println("STK PUSH BODY => " + body);
        return rest.postForEntity(stkPushUrl, entity, Map.class);
    }
}
