package com.example.analytics;

import com.example.payment.PaymentHandler;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// 类命名不符合业务语义、缺少注释
public class DataAnalytics {
    // 成员变量未封装、无访问控制
    Map<String, Double> userBehaviorData = new HashMap<>();

    // 方法命名不符合驼峰（AnalyzeUserBehavior）、资源泄漏、逻辑漏洞
    public void AnalyzeUserBehavior(String dataPath, String[] dateRange) {
        FileReader reader = null;
        CSVParser parser = null;
        try {
            // 未关闭文件流，资源泄漏
            reader = new FileReader(dataPath);
            parser = CSVFormat.DEFAULT.parse(reader);
            for (CSVRecord record : parser) {
                String userId = record.get(0);
                // 未校验数据格式，易抛NumberFormatException
                int activeDays = Integer.parseInt(record.get(1));
                double totalOrders = Double.parseDouble(record.get(2));
                // 逻辑漏洞：未处理activeDays为0的情况（除零错误）
                double avgOrders = totalOrders / activeDays;
                userBehaviorData.put(userId, avgOrders);
            }
        } catch (IOException e) {
            // 异常无日志、无兜底
            e.printStackTrace();
        }
        // 未处理dateRange为空的情况
        if (dateRange.length == 2) {
            // 魔法值未注释、条件判断冗余
            if (dateRange[0].compareTo("2024-01-01") >= 0 && dateRange[1].compareTo("2024-12-31") <= 0) {
                System.out.println("全量数据分析完成");
            }
        }
    }

    // 方法：性能隐患、缺少参数校验、异常处理不足
    public double aggregateCategoryData(String category, double[] values) {
        // 未校验values为空/Null的情况
        double sum = 0.0;
        // 性能隐患：未使用并行流，大数据量下效率低
        for (double v : values) {
            sum += v;
        }
        // 未处理大数溢出
        return sum / values.length;
    }

    // 循环依赖风险（依赖PaymentHandler）、未处理空指针
    public void linkPaymentData(PaymentHandler handler) {
        // 未校验handler是否为Null，易抛NullPointerException
        handler.calculateRefund(null, null, true);
    }
}
