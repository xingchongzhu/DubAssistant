package synthesis.voice.com.voicesynthesis;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.List;

import synthesis.voice.com.voicesynthesis.Callback.LoadDataListener;
import synthesis.voice.com.voicesynthesis.utils.FileUtils;
import synthesis.voice.com.voicesynthesis.utils.JsonUtils;
import synthesis.voice.com.voicesynthesis.utils.LogUtils;

public class AsyncLoadData extends AsyncTask<Void, List<VoiceItem>,  List<VoiceItem>> {
    private WeakReference<Context> mContext;
    private LoadDataListener mLoadDataListener;
    public AsyncLoadData(Context context, LoadDataListener loadDataListener){
        mContext = new WeakReference<>(context);//这里传入activity的上下文
        mLoadDataListener = loadDataListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<VoiceItem> doInBackground(Void... paramArrayOfParams) {
        String string = FileUtils.getStringFromRaw((Context)mContext.get(),R.raw.voice);
        LogUtils.d( "doInBackground string " + string);
        return JsonUtils.convertJSONArray(string);
    }

    @Override
    protected void onPostExecute( List<VoiceItem> list) {
        super.onPostExecute(list);
        if(mLoadDataListener != null){
            mLoadDataListener.loadData(list);
        }
    }

}
