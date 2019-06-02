package synthesis.voice.com.voicesynthesis.utils;

import android.text.TextUtils;

public class DelayDealManager {

    public final static String PAUSE = "[停顿]";
    public final static String PAUSECHAR = ",";
    public final static String DEALY_1 = "[延迟1秒]";
    public final static String DEALY_1CHAR = "♠";
    public final static String DEALY_2 = "[延迟2秒]";
    public final static String DEALY_2CHAR = "♣";
    private String speechText;
    private int strLength = 0;
    private String originText;
   // ♠ ♣ ♥ ❤

    public DelayDealManager() {

    }

    public String getOriginText(){
        return originText;
    }
    public String getSpeechText(){
        return speechText;
    }

    public int getStrLength() {
        return strLength;
    }

    public void setOriginText(String originText) {
        strLength = 0;
        this.originText = originText;
        if(!TextUtils.isEmpty(originText)){
            originText = originText.replace(PAUSE,PAUSECHAR);
            originText = originText.replace(DEALY_1,DEALY_1CHAR);
            originText = originText.replace(DEALY_2,DEALY_2CHAR);
            strLength = originText.length();
        }
        this.speechText = originText;
    }

    public int delayCount(String str) {
        int delay = 0;
        switch (str){
            case DEALY_1CHAR:
                delay = 1000;
                break;
            case DEALY_2CHAR:
                delay = 2000;
                break;
                default:
                    delay = 0;
        }
        return delay;
    }
}
