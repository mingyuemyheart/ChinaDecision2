package com.china.adapter;

/**
 * 预警统计列表筛选
 */

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.china.R;
import com.china.dto.WarningDto;

import java.util.HashMap;
import java.util.List;

public class WarningStatisticListSelectAdapter extends BaseAdapter{

	private Context mContext = null;
	private LayoutInflater mInflater = null;
	private List<WarningDto> mArrayList = null;
	public HashMap<Integer, Boolean> isSelected = new HashMap<>();

	private final class ViewHolder {
		TextView tvName;//预警信息名称
	}

	private ViewHolder mHolder = null;

	public WarningStatisticListSelectAdapter(Context context, List<WarningDto> mArrayList) {
		mContext = context;
		this.mArrayList = mArrayList;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		for (int i = 0; i < mArrayList.size(); i++) {
			if (i == 0) {
				isSelected.put(i, true);
			}else {
				isSelected.put(i, false);
			}
		}
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
			convertView = mInflater.inflate(R.layout.adapter_warning_statistic_list_select, null);
			mHolder = new ViewHolder();
			mHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		
		WarningDto dto = mArrayList.get(position);
		if (!TextUtils.isEmpty(dto.name)) {
			mHolder.tvName.setText(dto.name);
		}

		if (isSelected.get(position) == true) {
			mHolder.tvName.setTextColor(0xff2d5a9d);
			mHolder.tvName.setBackgroundResource(R.drawable.bg_layer_button);
		}else {
			if (position == 0) {
				mHolder.tvName.setTextColor(mContext.getResources().getColor(R.color.text_color3));
			}else {
				if (dto.count == 0) {
					mHolder.tvName.setTextColor(mContext.getResources().getColor(R.color.text_color2));
				}else {
					mHolder.tvName.setTextColor(mContext.getResources().getColor(R.color.text_color3));
				}
			}
			mHolder.tvName.setBackgroundColor(mContext.getResources().getColor(R.color.white));
		}
		
		return convertView;
	}

}
