package synthesis.voice.com.voicesynthesis.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import synthesis.voice.com.voicesynthesis.VoiceItem;

public class JsonUtils {
    public static List<VoiceItem> convertJSONArray(String result) {
        List<VoiceItem> list = new ArrayList<>();

        try{
            // 整个最大的JSON数组
            JSONObject jsonObjectALL = new JSONObject(result);

            // 通过标识(person)，获取JSON数组
            JSONArray jsonArray = jsonObjectALL.getJSONArray("data");
            LogUtils.d( "analyzeJSONArray1 jsonArray:" + jsonArray);
            // [{"name":"君君","age":89,"sex":"男"},{"name":"小君","age":99,"sex":"女"},{"name":"大君","age":88,"sex":"男"}]

            for (int i = 0; i < jsonArray.length(); i++) {
                // JSON数组里面的具体-JSON对象
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                VoiceItem item = new VoiceItem();
                item.setName(jsonObject.optString("name", null));
                item.setGender(jsonObject.optString("gender", null));
                item.setLanguage(jsonObject.optString("language", null));
                item.setKey(jsonObject.optString("key", null));
                item.toString();
                list.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
