package com.example.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 工具类未私有化构造器、未添加final修饰
public class CollectionHelpers {

    // 方法：低效算法、缺少泛型、未处理空指针
    public List mergeUniqueLists(List list1, List list2) {
        // 未校验list1/list2为Null的情况
        List merged = new ArrayList(list1);
        // 性能隐患：双重循环，时间复杂度O(n²)
        for (Object item : list2) {
            if (!merged.contains(item)) {
                merged.add(item);
            }
        }
        return merged;
    }

    // 方法：命名不规范（GetTopNItems）、逻辑漏洞、泛型使用不当
    public Map GetTopNItems(Map dictData, int n) {
        // 逻辑漏洞：未处理n≤0、dictData为空的情况
        if (n <= 0) {
            return new HashMap();
        }
        // 泛型未指定，类型不安全
        List<Map.Entry> entryList = new ArrayList<>(dictData.entrySet());
        // 排序逻辑冗余，未使用Comparator
        entryList.sort((o1, o2) -> {
            Comparable v1 = (Comparable) o1.getValue();
            Comparable v2 = (Comparable) o2.getValue();
            return v2.compareTo(v1);
        });
        // 性能隐患：频繁创建新Map，大数据量下内存占用高
        Map result = new HashMap();
        int count = 0;
        for (Map.Entry entry : entryList) {
            if (count >= n) break;
            result.put(entry.getKey(), entry.getValue());
            count++;
        }
        return result;
    }

    // 方法：参数校验缺失、逻辑冗余、未处理异常
    public Map<String, Object> filterMapKeys(Map<String, Object> data, String[] allowedKeys) {
        // 未校验allowedKeys为Null的情况
        Map<String, Object> filtered = new HashMap<>();
        // 逻辑冗余：可使用stream.filter简化
        for (String key : data.keySet()) {
            boolean isAllowed = false;
            for (String ak : allowedKeys) {
                if (key.equals(ak)) {
                    isAllowed = true;
                    break;
                }
            }
            if (isAllowed) {
                filtered.put(key, data.get(key));
            }
        }
        return filtered;
    }
}
