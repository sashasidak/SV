package common.helpers;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;

import static constants.Constants.COREZOID.*;

//Документация: https://openapi.corezoid.com/#section/Introduction

public class CorezoidApiClient {

    private static final String API_LOGIN = COREZOID_API_LOGIN;
    private static final String API_SECRET = COREZOID_API_SECRET;
    private static final String API_URL = COREZOID_API_URL;
    private static final Integer COREZOID_conv_id = COREZOID_CONV_MY;

    public static void main(String[] args) {

    }

    public static void sendRequest(JSONObject content) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            long gmtUnixTime = Instant.now().getEpochSecond();
            String signature = generateSignature(gmtUnixTime, API_SECRET, content.toString());

            String requestUrl = API_URL + "/" + API_LOGIN + "/" + gmtUnixTime + "/" + signature;
            System.out.println("========== Corezoid API Request ==========");
            System.out.println("Request URL: " + requestUrl);
            System.out.println("Request Content: " + content.toString());
            System.out.println("Generated Signature: " + signature);
            System.out.println("=========================================");

            HttpPost post = new HttpPost(requestUrl);
            post.setHeader("Content-Type", "application/json");
            post.setEntity(new StringEntity(content.toString(), StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = client.execute(post)) {
                String responseString = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
                System.out.println("Response Code: " + response.getStatusLine().getStatusCode());
                System.out.println("Response Body: " + responseString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JSONObject blockCard(String cardNumber, String type, String amount) {
        long currentTimestamp = System.currentTimeMillis() / 1000;
        JSONObject request = new JSONObject();
        JSONArray ops = new JSONArray();

        JSONObject task = new JSONObject();
        task.put("type", "create");
        task.put("conv_id", COREZOID_conv_id);
        task.put("obj", "task");

        JSONObject data = new JSONObject();
        data.put("amount", amount);
        data.put("block", true);
        data.put("opertype", "enable");
        data.put("pan", cardNumber);
        data.put("type", type);
        data.put("type_bloc", type + "." + amount);
        data.put("update_id", 922622733);

        task.put("data", data);
        task.put("ref", "BLOCK-" + Instant.now().getEpochSecond());

        ops.put(task);
        request.put("ops", ops);

        System.out.println("Created Block Card Request: " + request.toString());
        return request;
    }

    private static String generateSignature(long time, String secret, String content) throws Exception {
        String data = time + secret + content + secret;
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}
