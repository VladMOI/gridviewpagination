package ua.vladmoyseienko.gridviewpagination;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class GridViewAdapter extends BaseAdapter {
    ArrayList<HashMap<String, Integer>> data = new ArrayList<>();
    Context context;
    public GridViewAdapter(ArrayList<HashMap<String, Integer>> data, Context ctx){
        this.data = data;
        this.context = ctx;
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public void add(HashMap<String, Integer> newData) {
        //не исплюзуется
        data.add(newData);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Берем шаблон элемента
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.gridview_item, null);
        }
        //создаем вьюшки
        TextView tvData = (TextView) convertView.findViewById(R.id.tv_data);
        TextView tvMoreData = (TextView) convertView.findViewById(R.id.tv_moredata);

        HashMap<String, Integer> item = (HashMap<String, Integer>) getItem(position);

        String data = item.get("data").toString();
        String moreData = item.get("moreData").toString();

        tvData.setText(data);
        tvMoreData.setText(moreData);

        return convertView;
    }
}
