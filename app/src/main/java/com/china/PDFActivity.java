package com.china;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.china.common.CONST;
import com.china.utils.CommonUtil;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.PDFView.Configurator;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import static android.R.attr.permission;

public class PDFActivity extends BaseActivity implements OnPageChangeListener, OnLoadCompleteListener, OnClickListener {
	
	private Context mContext = null;
	private LinearLayout llBack = null;
	private TextView tvTitle = null;
	private PDFView pdfView;
	private ImageView preBtn;
	private ImageView nextBtn;
	private int curPage;
	private int totalPage;
	private String pdfUrl;
	private ImageView ivShare = null;
	private TextView tvPercent = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pdfview);
		mContext = this;
		initWidget();
		initPDFView();
	}

	private void initWidget() {
		llBack = (LinearLayout) findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		preBtn = (ImageView) findViewById(R.id.pdf_pre_btn);
		preBtn.setOnClickListener(this);
		nextBtn = (ImageView) findViewById(R.id.pdf_next_btn);
		nextBtn.setOnClickListener(this);
		ivShare = (ImageView) findViewById(R.id.ivShare);
		ivShare.setOnClickListener(this);
		tvPercent = (TextView) findViewById(R.id.tvPercent);
		
		String title = getIntent().getStringExtra(CONST.ACTIVITY_NAME);
		if (title != null) {
			tvTitle.setText(title);
		}
		
		pdfUrl = getIntent().getStringExtra(CONST.WEB_URL);
	}
	
	// 根据Unicode编码完美的判断中文汉字和符号
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }
 
    // 完整的判断中文汉字和符号
    private String isChinese(String strName) {
        char[] ch = strName.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
            	try {
					strName = strName.replace(c+"", URLEncoder.encode(c+"", "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
            }
        }
		return strName;
    }
	
	private void initPDFView() {
		pdfView = (PDFView) findViewById(R.id.pdf_view);
		pdfView.enableDoubletap(true);
		pdfView.enableSwipe(true);
		
		if (TextUtils.isEmpty(pdfUrl)) {
			return;
		}else {
			pdfUrl = isChinese(pdfUrl);
		}

		asynLoadPdf(pdfUrl);
	}
	
	/**
	 * 异步下载pdf文件
	 * @param pdfUrl pdf地址
	 */
	private void asynLoadPdf(String pdfUrl) {
		AsynLoadTask task = new AsynLoadTask(new AsynLoadCompleteListener() {
			@Override
			public void loadComplete(File file) {
				if (file != null) {
					Configurator conf = pdfView.fromFile(file);
					conf.onPageChange(PDFActivity.this);
					conf.onLoad(new OnLoadCompleteListener() {
						@Override
						public void loadComplete(int arg0) {
							pdfView.setVisibility(View.VISIBLE);
							tvPercent.setVisibility(View.GONE);
						}
					});
					conf.load();
				}
			}
		}, pdfUrl);
        task.execute();
	}
	
	private interface AsynLoadCompleteListener {
		public void loadComplete(File file);
	}
    
	private class AsynLoadTask extends AsyncTask<Void, File, File> {
		
		private String url;
		private AsynLoadCompleteListener completeListener;

		private AsynLoadTask(AsynLoadCompleteListener completeListener, String url) {
			this.url = url;
			this.completeListener = completeListener;
		}

		@Override
		protected void onPreExecute() {
		}
		
		@Override
		protected void onProgressUpdate(File... values) {
		}

		@Override
		protected File doInBackground(Void... params) {
			return downLoadPdf(url);
		}

		@Override
		protected void onPostExecute(File file) {
			if (completeListener != null) {
				completeListener.loadComplete(file);
            }
		}
	}
	
	/**
	 * 下载pdf文件
	 * @param pdfUrl
	 * @return
	 */
	private File downLoadPdf(String pdfUrl) {
		try {
			URL u = new URL(pdfUrl);
			File file = new File(Environment.getExternalStorageDirectory()+"/pdf");
			if (!file.exists()) {
				file.mkdirs();
			}
			String path = file.getAbsolutePath()+"/"+"1.pdf";
			byte[] buffer = new byte[1024 * 8];
			int read;
			int ava = 0;
//			long start = System.currentTimeMillis();
			HttpURLConnection urlcon = (HttpURLConnection) u.openConnection();
			double fileLength = (double) urlcon.getContentLength();
			BufferedInputStream bin = new BufferedInputStream(u.openStream());
			BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(path));
			while ((read = bin.read(buffer)) > -1) {
				bout.write(buffer, 0, read);
				ava += read;
				int percent = (int) Math.floor((ava / fileLength * 100));
				Message msg = new Message();
				msg.what = 0;
				msg.obj = percent;
				handler.sendMessage(msg);
//				dialog.setProgress(a);
//				long speed = ava / (System.currentTimeMillis() - start);
			}
			bout.flush();
			bout.close();
			return new File(path);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				tvPercent.setText(msg.obj+getString(R.string.unit_percent));
			}
		};
	};
	
	private void refreshPreAndNextBtn() {
		curPage = pdfView.getCurrentPage();
		totalPage = pdfView.getPageCount();
//		if (totalPage <= 1) {
//			preBtn.setVisibility(View.INVISIBLE);
//			nextBtn.setVisibility(View.INVISIBLE);
//		} else if (curPage == 0) {
//			preBtn.setVisibility(View.INVISIBLE);
//			nextBtn.setVisibility(View.VISIBLE);
//		} else if (curPage == totalPage - 1) {
//			preBtn.setVisibility(View.VISIBLE);
//			nextBtn.setVisibility(View.INVISIBLE);
//		} else {
//			preBtn.setVisibility(View.VISIBLE);
//			nextBtn.setVisibility(View.VISIBLE);
//		}
	}
	
	@Override
	public void loadComplete(int nbPages) {
		refreshPreAndNextBtn();
	}

	@Override
	public void onPageChanged(int page, int pageCount) {
		curPage = pdfView.getCurrentPage();
		refreshPreAndNextBtn();
	}

	@Override
	public void onClick(View v) {
		curPage = pdfView.getCurrentPage();
		totalPage = pdfView.getPageCount();
		switch (v.getId()) {
		case R.id.llBack:
			finish();
			break;
		case R.id.pdf_pre_btn:
			if (curPage > 0) {
				pdfView.jumpTo(curPage);
			}
			refreshPreAndNextBtn();
			break;
		case R.id.pdf_next_btn:
			if (curPage < totalPage) {
				pdfView.jumpTo(curPage + 2);
			}
			refreshPreAndNextBtn();
			break;
		case R.id.ivShare:
			Bitmap bitmap1 = CommonUtil.captureView(pdfView);
			Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.iv_share_bottom);
//			Bitmap bitmap2 = BitmapFactory.decodeStream(getClass().getResourceAsStream("/res/drawable-hdpi/iv_share_bottom.png"));
			Bitmap bitmap = CommonUtil.mergeBitmap(PDFActivity.this, bitmap1, bitmap2, false);
			CommonUtil.clearBitmap(bitmap1);
			CommonUtil.clearBitmap(bitmap2);
			CommonUtil.share(PDFActivity.this, bitmap);
			break;

		default:
			break;
		}
	}
}
