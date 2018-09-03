package com.coderstory.FTool.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.coderstory.FTool.R;

import java.util.List;

public class AppInfoAdapter extends ArrayAdapter {
    private int resourceId;


    public AppInfoAdapter(Context context, int textViewResourceId, List<AppInfo> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public
    @NonNull
    View getView(int position, View convertView, @NonNull ViewGroup parent) {
        AppInfo appInfo = (AppInfo) getItem(position);


        View view;
        ViewHolder vh;
        if (convertView != null) { //查询布局是否已经缓存
            view = convertView;
            vh = (ViewHolder) view.getTag();//重新获取ViewHolder

        } else {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null); //读取items.xml文件并实例化
            vh = new ViewHolder();
            vh.myImage = (ImageView) view.findViewById(R.id.app_image);//查找items实例中的myimage
            vh.myText = (TextView) view.findViewById(R.id.app_name);//查找items实例中的mytext
            view.setTag(vh); //保存到view中
        }

        vh.myText.setTag(appInfo != null ? appInfo.getPackageName() : null);
        vh.myImage.setImageDrawable(appInfo != null ? appInfo.getImageId() : null);
        vh.myText.setText(" 应用名 : " + appInfo.getName() + "\r\n 版本号 : " + appInfo.getVersion());
        if (appInfo.getDisable()) {
            view.setBackgroundColor(Color.parseColor("#d0d7d7d7")); //冻结的颜色
        } else {
            view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary)); //正常的的颜色
        }
        return view;
    }

    private class ViewHolder {
        ImageView myImage;
        TextView myText;
    }
}
