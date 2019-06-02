package synthesis.voice.com.voicesynthesis;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.sunflower.FlowerCollector;

import java.util.List;

import synthesis.voice.com.voicesynthesis.Callback.LoadDataListener;
import synthesis.voice.com.voicesynthesis.Callback.SoftKeyBoardListener;
import synthesis.voice.com.voicesynthesis.ttsmanager.TtsManager;
import synthesis.voice.com.voicesynthesis.utils.DelayDealManager;
import synthesis.voice.com.voicesynthesis.utils.LogUtils;

public class TtsMainActivity extends Activity implements OnClickListener ,SynthesizerListener ,LoadDataListener ,SoftKeyBoardListener.OnSoftKeyBoardChangeListener {
	private static String TAG = TtsMainActivity.class.getSimpleName();
	private final static int RESUMEPLAYCODE = 1;
	// 默认发音人
	private String voicer = "xiaoyan";
	private Toast mToast;
	// 缓冲进度
	private int mPercentForBuffering = 0;
	// 播放进度
	private int mPercentForPlaying = 0;

	private AsyncLoadData mAsyncLoadData;
	private GridViewAdapter mGridViewAdapter;
    private GridView gridView;
    private SeekBar seekBar;
    private ImageView ttsControl;
    private SoftKeyBoardListener mSoftKeyBoardListener;
    private View mDelayContent;
    private View mTtsContent;
    private EditText ttsEditer;
    private DelayDealManager mDelayDealManager;
    private TtsManager mTtsManager;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case RESUMEPLAYCODE:
                    if(mTtsManager.getTts() != null){
						LogUtils.d("resumeSpeaking");
                        mTtsManager.getTts().resumeSpeaking();
                    }
                    break;
            }

        }
    };

	@SuppressLint("ShowToast")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ttsdemo);
		//texts = getResources().getString(R.string.text_tts_source);
		initLayout();
		initData();
	}
	/**
	 * 初始化Layout。
	 */
	private void initLayout() {
		findViewById(R.id.audition).setOnClickListener(this);
		findViewById(R.id.pause).setOnClickListener(this);
		findViewById(R.id.pause_1).setOnClickListener(this);
		findViewById(R.id.pause_2).setOnClickListener(this);
		gridView = (GridView) findViewById(R.id.horizontal_layout).findViewById(R.id.grid);
		seekBar = findViewById(R.id.delayMeetingDialogSeekBar);
		ttsControl = findViewById(R.id.control);
        mDelayContent = findViewById(R.id.delay_content);
		mTtsContent = findViewById(R.id.tts_content);
		ttsEditer = findViewById(R.id.tts_text);
		ttsControl.setOnClickListener(TtsMainActivity.this);
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				VoiceItem item = (VoiceItem)mGridViewAdapter.getItem(position);
				voicer = item.getKey();
				index = 0;
				LogUtils.d("onItemClick voicer = "+voicer);
				if ("catherine".equals(voicer) || "henry".equals(voicer) || "vimary".equals(voicer)) {
					ttsEditer.setText(R.string.text_tts_source_en);
				}else {
					ttsEditer.setText(R.string.text_tts_source);
				}
			}
		});
        mSoftKeyBoardListener = new SoftKeyBoardListener(this);
        mSoftKeyBoardListener.setListener(this,this);

	}
	private void initData(){
		mAsyncLoadData = new AsyncLoadData(this,this);
		mAsyncLoadData.execute();
		// 初始化合成对象
		mToast = Toast.makeText(this,"",Toast.LENGTH_SHORT);
		mTtsManager = new TtsManager(this);
        mDelayDealManager = new DelayDealManager();
	}

	public void loadData(List<VoiceItem> list){
		if(list != null){
			if(mGridViewAdapter == null){
				mGridViewAdapter = new GridViewAdapter(this,list);
			}
			bindAdapter(list);
		}
	}
	/**设置GirdView参数，绑定数据*/
	private void bindAdapter(List<VoiceItem> list) {
		int size = list.size();
		int length = 100;
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		float density = dm.density;
		int gridviewWidth = (int) (size * (length + 4) * density);
		int itemWidth = (int) (length * density);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				gridviewWidth, LinearLayout.LayoutParams.FILL_PARENT);
		gridView.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
		gridView.setColumnWidth(itemWidth); // 设置列表项宽
		gridView.setHorizontalSpacing(5); // 设置列表项水平间距
		gridView.setStretchMode(GridView.NO_STRETCH);
		gridView.setNumColumns(size); // 设置列数量=列表集合数

		GridViewAdapter adapter = new GridViewAdapter(getApplicationContext(),
				list);
		gridView.setAdapter(adapter);
	}

	@Override
	public void onClick(View view) {
		if( null ==  mTtsManager.getTts() ){
			// 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
			this.showTip( "创建对象失败，请确认 libmsc.so 放置正确，且有调用 createUtility 进行初始化" );
			return;
		}
		int index = ttsEditer.getSelectionStart();
		Editable editable = ttsEditer.getText();
		switch(view.getId()) {
			/*case R.id.image_tts_set:
				if (SpeechConstant.TYPE_CLOUD.equals(mEngineType)) {
					Intent intent = new Intent(TtsMainActivity.this, TtsSettings.class);
					startActivity(intent);
				} else {
					showTip("请前往xfyun.cn下载离线合成体验");
				}
				break;*/
			// 开始合成
			// 收到onCompleted 回调时，合成结束、生成合成音频
			// 合成的音频格式：只支持pcm格式
			case R.id.audition:
				break;
			case R.id.pause:
				editable.insert(index, DelayDealManager.PAUSE);
				break;
			case R.id.pause_1:
				editable.insert(index, DelayDealManager.DEALY_1);
				break;
			case R.id.pause_2:
				editable.insert(index, DelayDealManager.DEALY_2);
				break;
			case R.id.control:
                ttsControl.setBackground(null);
                mHandler.removeMessages(RESUMEPLAYCODE);
				preIndex = 0;
				index = 0;
				if(mTtsManager.getTts().isSpeaking()){
					ttsControl.setImageResource(R.mipmap.play);
					mTtsManager.getTts().stopSpeaking();
				}else{
					ttsControl.setImageResource(R.mipmap.pause);
					// 移动数据分析，收集开始合成事件
					FlowerCollector.onEvent(TtsMainActivity.this, "tts_play");
                    mDelayDealManager.setOriginText(ttsEditer.getText().toString());
					// 设置参数
					mTtsManager.setParam(voicer,seekBar.getProgress());
					int code = mTtsManager.getTts().startSpeaking(mDelayDealManager.getSpeechText(), this);
					if (code != ErrorCode.SUCCESS) {
						showTip("语音合成失败,错误码: " + code);
					}
				}
				break;
		}
	}

	private void showTip(final String str) {
		mToast.setText(str);
		mToast.show();
	}

	@Override
	public void onSpeakBegin() {
		LogUtils.d("开始播放");
	}

	@Override
	public void onSpeakPaused() {
		LogUtils.d("暂停播放");
	}

	@Override
	public void onSpeakResumed() {
		LogUtils.d("继续播放");
	}

	@Override
	public void onBufferProgress(int percent, int beginPos, int endPos,
								 String info) {
		// 合成进度
		mPercentForBuffering = percent;
		LogUtils.d(String.format(getString(R.string.tts_toast_format),
				mPercentForBuffering, mPercentForPlaying));
	}

	int index = 0;
	private int preIndex = 0;
	@Override
	public void onSpeakProgress(int percent, int beginPos, int endPos) {
		// 播放进度
		index = mPercentForPlaying * mDelayDealManager.getStrLength()/100-1;
		if(index < 0){
            index = 0;
        }
		mPercentForPlaying = percent;
		LogUtils.d(String.format(getString(R.string.tts_toast_format),
				mPercentForBuffering, mPercentForPlaying));

		SpannableStringBuilder style=new SpannableStringBuilder(mDelayDealManager.getOriginText());
		if(index != preIndex) {
		    String str = mDelayDealManager.getSpeechText().substring(index,index+1);
            int deal = mDelayDealManager.delayCount(str);
            if(deal >0){
                if(mTtsManager.getTts() != null){
                    mTtsManager.getTts().pauseSpeaking();
					LogUtils.d("pauseSpeaking");
                    mHandler.sendEmptyMessageDelayed(RESUMEPLAYCODE,deal);
                }
            }
			LogUtils.d("beginPos = " + beginPos + "  endPos = " + endPos + " index "+str);
		}
		style.setSpan(new BackgroundColorSpan(getColor(R.color.current_location)),beginPos,endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		ttsEditer.setText(style);
		preIndex = index;
	}
	@Override
	public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
	}

	@Override
	public void onCompleted(SpeechError error) {
		if (error == null) {
			LogUtils.d("播放完成");
		} else if (error != null) {
			LogUtils.d(error.getPlainDescription(true));
		}
        ttsControl.setImageResource(R.mipmap.play);
	}

    @Override
    public void keyBoardHide(int height) {
		LogUtils.d("隐藏"+height);

        mTtsContent.setVisibility(View.VISIBLE);
        mDelayContent.setVisibility(View.GONE);
    }

    @Override
    public void keyBoardShow(int height) {
		LogUtils.d("显示"+height);
        mTtsContent.setVisibility(View.GONE);
        mDelayContent.setVisibility(View.VISIBLE);
    }

    @Override
	protected void onResume() {
		//移动数据统计分析
		FlowerCollector.onResume(TtsMainActivity.this);
		FlowerCollector.onPageStart(TAG);
		super.onResume();
	}

	@Override
	protected void onPause() {
		//移动数据统计分析
		FlowerCollector.onPageEnd(TAG);
		FlowerCollector.onPause(TtsMainActivity.this);
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if( null != mTtsManager.getTts() ){
			mTtsManager.getTts().stopSpeaking();
			// 退出时释放连接
			mTtsManager.getTts().destroy();
		}
	}
}
