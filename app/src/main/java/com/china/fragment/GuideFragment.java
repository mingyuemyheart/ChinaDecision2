package com.china.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.china.R;
import com.china.activity.ShawnLoginActivity;
import com.china.activity.ShawnMainActivity;
import com.china.common.CONST;
import com.china.common.ColumnData;
import com.china.common.MyApplication;
import com.china.dto.NewsDto;
import com.china.utils.CommonUtil;
import com.china.utils.OkHttpUtil;
import com.tendcloud.tenddata.TCAgent;
import com.tendcloud.tenddata.TDAccount;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 引导页
 */
public class GuideFragment extends Fragment implements OnClickListener{

	private List<ColumnData> dataList = new ArrayList<>();
	private List<NewsDto> pdfList = new ArrayList<>();//pdf文档类
	
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
		RelativeLayout reMain = view.findViewById(R.id.reMain);
		
		int index = getArguments().getInt("index");
		if (index == 0) {
			reMain.setBackgroundResource(R.drawable.bg_guide_01);
		}else if (index == 1) {
			reMain.setBackgroundResource(R.drawable.bg_guide_02);
		}else if (index == 2) {
			reMain.setBackgroundResource(R.drawable.bg_guide_03);
			reMain.setOnClickListener(this);
		}
	}

	/**
	 * 登录
	 */
	private void OkHttpLogin() {
		final String url = "http://decision-admin.tianqi.cn/home/Work/login";
		FormBody.Builder builder = new FormBody.Builder();
		builder.add("username", MyApplication.USERNAME);
		builder.add("password", MyApplication.PASSWORD);
		builder.add("appid", CONST.APPID);
		builder.add("device_id", CommonUtil.getUniqueId(getActivity()));
		builder.add("platform", "android");
		builder.add("os_version", android.os.Build.VERSION.RELEASE);
		builder.add("software_version", CommonUtil.getVersion(getActivity()));
		builder.add("mobile_type", android.os.Build.MODEL);
		builder.add("address", "");
		builder.add("lat", "0'");
		builder.add("lng", "0");
		final RequestBody body = builder.build();
		new Thread(new Runnable() {
			@Override
			public void run() {
				OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {
					}
					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String result = response.body().string();
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (!TextUtils.isEmpty(result)) {
									try {
										JSONObject object = new JSONObject(result);
										if (!object.isNull("status")) {
											int status  = object.getInt("status");
											if (status == 1) {//成功
												JSONArray array = object.getJSONArray("column");
												dataList.clear();
												for (int i = 0; i < array.length(); i++) {
													JSONObject obj = array.getJSONObject(i);
													ColumnData data = new ColumnData();
													if (!obj.isNull("id")) {
														data.columnId = obj.getString("id");
													}
													if (!obj.isNull("localviewid")) {
														data.id = obj.getString("localviewid");
													}
													if (!obj.isNull("name")) {
														data.name = obj.getString("name");
													}
													if (!obj.isNull("icon")) {
														data.icon = obj.getString("icon");
													}
													if (!obj.isNull("desc")) {
														data.desc = obj.getString("desc");
													}
													if (!obj.isNull("showtype")) {
														data.showType = obj.getString("showtype");
													}
													if (!obj.isNull("dataurl")) {
														data.dataUrl = obj.getString("dataurl");
													}
													if (!obj.isNull("child")) {
														JSONArray childArray = obj.getJSONArray("child");
														for (int j = 0; j < childArray.length(); j++) {
															JSONObject childObj = childArray.getJSONObject(j);
															ColumnData dto = new ColumnData();
															dto.groupColumnId = data.columnId;
															if (!childObj.isNull("id")) {
																dto.columnId = childObj.getString("id");
															}
															if (!childObj.isNull("localviewid")) {
																dto.id = childObj.getString("localviewid");
															}
															if (!childObj.isNull("name")) {
																dto.name = childObj.getString("name");
															}
															if (!childObj.isNull("desc")) {
																dto.desc = childObj.getString("desc");
															}
															if (!childObj.isNull("icon")) {
																dto.icon = childObj.getString("icon");
															}
															if (!childObj.isNull("showtype")) {
																dto.showType = childObj.getString("showtype");
															}
															if (!childObj.isNull("dataurl")) {
																dto.dataUrl = childObj.getString("dataurl");
															}

															if (!childObj.isNull("child")) {
																JSONArray childArray2 = childObj.getJSONArray("child");
																for (int m = 0; m < childArray2.length(); m++) {
																	JSONObject childObj2 = childArray2.getJSONObject(m);
																	ColumnData d = new ColumnData();
																	if (!childObj2.isNull("id")) {
																		d.columnId = childObj2.getString("id");
																	}
																	if (!childObj2.isNull("localviewid")) {
																		d.id = childObj2.getString("localviewid");
																	}
																	if (!childObj2.isNull("name")) {
																		d.name = childObj2.getString("name");
																	}
																	if (!childObj2.isNull("desc")) {
																		d.desc = childObj2.getString("desc");
																	}
																	if (!childObj2.isNull("icon")) {
																		d.icon = childObj2.getString("icon");
																	}
																	if (!childObj2.isNull("showtype")) {
																		d.showType = childObj2.getString("showtype");
																	}
																	if (!childObj2.isNull("dataurl")) {
																		d.dataUrl = childObj2.getString("dataurl");
																	}
																	dto.child.add(d);
																}
															}

															data.child.add(dto);
														}
													}
													dataList.add(data);
												}

												if (!object.isNull("appinfo")) {
													JSONObject obj = object.getJSONObject("appinfo");
													if (!obj.isNull("counturl")) {
														CONST.COUNTURL = obj.getString("counturl");
													}
													if (!obj.isNull("recommendurl")) {
														CONST.RECOMMENDURL = obj.getString("recommendurl");
													}
													if (!obj.isNull("news")) {
														pdfList.clear();
														JSONArray newsArray = obj.getJSONArray("news");
														for (int i = 0; i < newsArray.length(); i++) {
															JSONObject itemObj = newsArray.getJSONObject(i);
															NewsDto dto = new NewsDto();
															if (!itemObj.isNull("name")) {
																dto.title = itemObj.getString("name");
															}
															if (!itemObj.isNull("url")) {
																dto.detailUrl = itemObj.getString("url");
															}
															if (!itemObj.isNull("time")) {
																dto.time = itemObj.getString("time");
															}
															if (!itemObj.isNull("img")) {
																dto.imgUrl = itemObj.getString("img");
															}
															pdfList.add(dto);
														}
													}
												}

												if (!object.isNull("info")) {
													Intent intent = new Intent(getActivity(), ShawnMainActivity.class);
													Bundle bundle = new Bundle();
													bundle.putParcelableArrayList("dataList", (ArrayList<? extends Parcelable>) dataList);
													bundle.putParcelableArrayList("pdfList", (ArrayList<? extends Parcelable>) pdfList);
													intent.putExtras(bundle);
													startActivity(intent);
													getActivity().finish();

													//统计登录事件
													TCAgent.onLogin(MyApplication.UID, TDAccount.AccountType.REGISTERED, MyApplication.USERNAME);
												}
											}else {
												//失败
												if (!object.isNull("msg")) {
													String msg = object.getString("msg");
													if (msg != null) {
														Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
													}
												}
											}
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}

							}
						});
					}
				});
			}
		}).start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.reMain:
			SharedPreferences sharedPreferences = getActivity().getSharedPreferences(CONST.SHOWGUIDE, Context.MODE_PRIVATE);
			Editor editor = sharedPreferences.edit();
			editor.putString(CONST.VERSION, CommonUtil.getVersion(getActivity()));
			editor.apply();

			if (!TextUtils.isEmpty(MyApplication.USERNAME) && !TextUtils.isEmpty(MyApplication.PASSWORD)) {
				OkHttpLogin();
			}else {
				startActivity(new Intent(getActivity(), ShawnLoginActivity.class));
				getActivity().finish();
			}
			break;

		default:
			break;
		}
	}
	
}
