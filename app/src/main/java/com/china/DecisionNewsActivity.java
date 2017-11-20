package com.china;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.china.adapter.DecisionNewsAdapter;
import com.china.common.CONST;
import com.china.dto.DisasterDto;
import com.china.utils.CommonUtil;
import com.china.utils.CustomHttpClient;
import com.china.view.RefreshLayout;
import com.china.view.RefreshLayout.OnRefreshListener;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 决策专报
 * @author shawn_sun
 *
 */
@SuppressLint("SimpleDateFormat")
public class DecisionNewsActivity extends BaseActivity implements OnClickListener{
	
	private Context mContext = null;
	private LinearLayout llBack = null;
	private TextView tvTitle = null;
	private ListView mListView = null;
	private DecisionNewsAdapter mAdapter = null;
	private List<DisasterDto> mList = new ArrayList<DisasterDto>();
	private RefreshLayout refreshLayout = null;//下拉刷新布局
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmm");
	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年MM月dd日 HH时");
	private SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy年MM月dd日");
	private ProgressBar progressBar = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.decision_news);
		mContext = this;
		initRefreshLayout();
		initWidget();
		initListView();
	}
	
	/**
	 * 初始化下拉刷新布局
	 */
	private void initRefreshLayout() {
		refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
		refreshLayout.setColor(CONST.color1, CONST.color2, CONST.color3, CONST.color4);
		refreshLayout.setMode(RefreshLayout.Mode.PULL_FROM_START);
		refreshLayout.setLoadNoFull(false);
		refreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				refresh();
			}
		});
	}
	
	private void refresh() {
		String url = getIntent().getStringExtra(CONST.WEB_URL);
		if (!TextUtils.isEmpty(url)) {
			asyncTask(url);
		}
	}
	
	private void initWidget() {
		llBack = (LinearLayout) findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		
		String title = getIntent().getStringExtra(CONST.ACTIVITY_NAME);
		if (title != null) {
			tvTitle.setText(title);
		}
		
		refresh();
		
		String columnId = getIntent().getStringExtra(CONST.COLUMN_ID);
		CommonUtil.submitClickCount(columnId, title);
	}
	
	private void initListView() {
		mListView = (ListView) findViewById(R.id.listView);
		mAdapter = new DecisionNewsAdapter(mContext, mList);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				DisasterDto dto = mList.get(arg2);
				Intent intent = new Intent(mContext, PDFActivity.class);
				intent.putExtra(CONST.ACTIVITY_NAME, dto.title);
				intent.putExtra(CONST.WEB_URL, dto.url);
				startActivity(intent);
			}
		});
	}
	
	/**
	 * 获取天气网眼数据
	 */
	private void asyncTask(String url) {
		//异步请求数据
		HttpAsyncTask task = new HttpAsyncTask();
		task.setMethod("GET");
		task.setTimeOut(CustomHttpClient.TIME_OUT);
		task.execute(url);
	}
	
	/**
	 * 异步请求方法
	 * @author dell
	 *
	 */
	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
		private String method = "GET";
		private List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
		
		public HttpAsyncTask() {
		}

		@Override
		protected String doInBackground(String... url) {
			String result = null;
			if (method.equalsIgnoreCase("POST")) {
				result = CustomHttpClient.post(url[0], nvpList);
			} else if (method.equalsIgnoreCase("GET")) {
				result = CustomHttpClient.get(url[0]);
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null) {
				try {
					JSONObject obj = new JSONObject(result);
					if (!obj.isNull("info")) {
						mList.clear();
						JSONArray array = obj.getJSONArray("info");
						for (int i = 0; i < array.length(); i++) {
							DisasterDto dto = new DisasterDto();
							String url = array.getString(i);
							if (!TextUtils.isEmpty(url)) {
								dto.url = url;
								String title = null;
								String time = null;
								if (!TextUtils.isEmpty(url)) {
									time = url.substring(url.length()-21, url.length()-9);
									if (url.contains("SEVP_CMA_SJCF_SFER_EME_ACHN_LNO_P9")) {
										title = getString(R.string.decision_news_1);
										if (!TextUtils.isEmpty(time)) {
											try {
												time = sdf2.format(sdf1.parse(time));
											} catch (ParseException e) {
												e.printStackTrace();
											}
										} 
									}else if (url.contains("BSEP_NMC_SDMS_SFER_EME_ACHN_LNO_P9")) {
										title = getString(R.string.decision_news_2);
										if (!TextUtils.isEmpty(time)) {
											try {
												time = sdf2.format(sdf1.parse(time));
											} catch (ParseException e) {
												e.printStackTrace();
											}
										} 
									}else if (url.contains("SEVP_CMA_IMIB_SFER_EME_ACHN_LNO_P9")) {
										title = getString(R.string.decision_news_3);
										if (!TextUtils.isEmpty(time)) {
											try {
												time = sdf3.format(sdf1.parse(time));
											} catch (ParseException e) {
												e.printStackTrace();
											}
										} 
									}
									
									dto.time = time;
									dto.title = title+time;
								}
								mList.add(dto);
							}
						}
						
						Collections.sort(mList, new Comparator<DisasterDto>() {
							@Override
							public int compare(DisasterDto a, DisasterDto b) {
								return b.time.compareTo(a.time);
							}
						});
						
						if (mAdapter != null) {
							mAdapter.notifyDataSetChanged();
						}
						progressBar.setVisibility(View.GONE);
						refreshLayout.setRefreshing(false);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		@SuppressWarnings("unused")
		private void setParams(NameValuePair nvp) {
			nvpList.add(nvp);
		}

		private void setMethod(String method) {
			this.method = method;
		}

		private void setTimeOut(int timeOut) {
			CustomHttpClient.TIME_OUT = timeOut;
		}

		/**
		 * 取消当前task
		 */
		@SuppressWarnings("unused")
		private void cancelTask() {
			CustomHttpClient.shuttdownRequest();
			this.cancel(true);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setBackEmit();
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.llBack:
			setBackEmit();
			finish();
			break;

		default:
			break;
		}
	}

}
