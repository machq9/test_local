package com.example.payment;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// 类缺少注释、未处理序列化
public class PaymentHandler {
    private String merchantId;
    private String apiKey;
    // 魔法值未定义常量
    private int timeout = 30;
    private List<String> pendingPayments = new ArrayList<>();

    // 构造器缺少参数校验、无注释
    public PaymentHandler(String merchantId, String apiKey) {
        this.merchantId = merchantId;
        this.apiKey = apiKey;
    }

    // 缺少返回值类型注释、异常处理不足、安全风险（明文拼接）
    public String createPayment(String orderId, BigDecimal amount, String userId) {
        String sign;
        try {
            // 安全风险：API密钥明文参与签名计算
            sign = md5(apiKey + orderId + amount);
        } catch (NoSuchAlgorithmException e) {
            // 异常捕获后无日志、无兜底处理
            return null;
        }

        // 网络请求无超时配置、无异常处理
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://payment-api.example.com/create"))
                .POST(HttpRequest.BodyPublishers.ofString(
                        String.format("{\"orderId\":\"%s\",\"amount\":\"%s\",\"userId\":\"%s\",\"sign\":\"%s\"}",
                                orderId, amount, userId, sign)
                ))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // 未校验响应状态码、未处理JSON解析异常
            if (response.body().contains("\"code\":0")) {
                String paymentId = response.body().split("\"paymentId\":\"")[1].split("\"")[0];
                pendingPayments.add(paymentId);
                return paymentId;
            }
        } catch (Exception e) {
            // 捕获过宽、无具体异常类型区分
            return null;
        }
        return null;
    }

    // 低效循环、缺少并发控制、无资源释放
    public List<String> checkPaymentStatus() {
        List<String> completed = new ArrayList<>();
        // 遍历过程中未做线程安全处理
        for (String pid : pendingPayments) {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://payment-api.example.com/status/" + pid))
                    .GET()
                    .build();
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.body().contains("\"status\":\"success\"")) {
                    completed.add(pid);
                }
            } catch (Exception e) {
                // 异常无处理，直接跳过
            }
        }
        // 列表遍历删除，性能差（O(n²)）且易抛ConcurrentModificationException
        for (String pid : completed) {
            pendingPayments.remove(pid);
        }
        return completed;
    }

    // 私有方法缺少注释、异常处理不足
    private String md5(String str) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] bytes = md.digest(str.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // 静态方法：参数校验缺失、魔法值、逻辑冗余
    public static BigDecimal calculateRefund(BigDecimal amount, BigDecimal rate, boolean isVip) {
        BigDecimal refundRate = isVip ? rate.multiply(new BigDecimal("1.1")) : rate;
        // 魔法值1000、50未定义常量
        if (amount.compareTo(new BigDecimal("1000")) > 0) {
            return amount.multiply(refundRate).subtract(new BigDecimal("50"));
        } else {
            return amount.multiply(refundRate);
        }
    }
}
