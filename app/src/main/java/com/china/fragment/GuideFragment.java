package com.china.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.china.LoginActivity;
import com.china.R;
import com.china.common.CONST;
import com.china.utils.CommonUtil;


public class GuideFragment extends Fragment implements OnClickListener{
	
	private RelativeLayout reMain = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_guide, null);
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initWidget(view);
	}
	
	private void initWidget(View view) {
		reMain = (RelativeLayout) view.findViewById(R.id.reMain);
		
		int index = getArguments().getInt("index");
		if (index == 0) {
			reMain.setBackgroundResource(R.drawable.guide_01);
		}else if (index == 1) {
			reMain.setBackgroundResource(R.drawable.guide_02);
		}else if (index == 2) {
			reMain.setBackgroundResource(R.drawable.guide_03);
			reMain.setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.reMain:
			SharedPreferences sharedPreferences = getActivity().getSharedPreferences(CONST.SHOWGUIDE, Context.MODE_PRIVATE);
			Editor editor = sharedPreferences.edit();
			editor.putString(CONST.VERSION, CommonUtil.getVersion(getActivity()));
			editor.commit();
			
			startActivity(new Intent(getActivity(), LoginActivity.class));
			getActivity().finish();
			break;

		default:
			break;
		}
	}
	
}
