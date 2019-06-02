package synthesis.voice.com.voicesynthesis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**GirdView 数据适配器*/
public class GridViewAdapter extends BaseAdapter {
    Context context;
    List<VoiceItem> list;
    public GridViewAdapter(Context _context, List<VoiceItem> _list) {
        this.list = _list;
        this.context = _context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView language = (TextView) convertView.findViewById(R.id.language);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.ItemImage);
        VoiceItem item = list.get(position);
        name.setText(item.getName());
        language.setText(item.getLanguage());
        if(item.getGender().equals("男")){
            imageView.setImageResource((R.mipmap.boy));
        }else{
            imageView.setImageResource((R.mipmap.gril));
        }
        return convertView;
    }
}
