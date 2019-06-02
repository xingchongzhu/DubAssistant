package synthesis.voice.com.voicesynthesis;

import synthesis.voice.com.voicesynthesis.utils.LogUtils;

public class VoiceItem {
    private String name;
    private String language;
    private String gender;
    private String key;

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public String getLanguage() {
        return language;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getGender() {
        return gender;
    }

    @Override
    public String toString() {
        String msg = "name "+name+" language "+language+" gender "+gender+" key "+key;
        LogUtils.i(msg);
        return msg;
    }
}
