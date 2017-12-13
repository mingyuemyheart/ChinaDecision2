package com.china.adapter;

/**
 * 新闻、图文类
 */

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.china.R;
import com.china.dto.NewsDto;
import com.china.utils.CommonUtil;

import net.tsz.afinal.FinalBitmap;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends BaseAdapter{
	
	private Context mContext = null;
	private LayoutInflater mInflater = null;
	private List<NewsDto> mArrayList = new ArrayList<>();
	
	private final class ViewHolder{
		ImageView imageView;
		TextView tvTitle;
		TextView tvTime;
	}
	
	private ViewHolder mHolder = null;
	
	public NewsAdapter(Context context, List<NewsDto> mArrayList) {
		mContext = context;
		this.mArrayList = mArrayList;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mArrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_news, null);
			mHolder = new ViewHolder();
			mHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
			mHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
			mHolder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		
		NewsDto dto = mArrayList.get(position);
		mHolder.tvTitle.setText(dto.title);
		
		if (!TextUtils.isEmpty(dto.time)) {
			mHolder.tvTime.setText(dto.time);
			mHolder.tvTime.setVisibility(View.VISIBLE);
		}else {
			mHolder.tvTime.setVisibility(View.INVISIBLE);
		}
		
		if (TextUtils.isEmpty(dto.imgUrl)) {
			mHolder.imageView.setImageResource(R.drawable.iv_default_news);
		}else {
			FinalBitmap finalBitmap = FinalBitmap.create(mContext);
			finalBitmap.display(mHolder.imageView, dto.imgUrl, null, 0);
		}
		
		return convertView;
	}

}
