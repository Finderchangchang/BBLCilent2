package com.beibeilian.me.application;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import com.beibeilian.constant.BBLConstant;
import com.beibeilian.util.CrashHandler;
import com.beibeilian.util.HelperUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.beibeilian.android.app.R;
import com.zy.pay.ZYPayTools;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

public class BBLApplication extends Application {
	public static final int NUM_PAGE = 1;// �ܹ��ж���ҳ
	public static int NUM = 20;// ÿҳ20������,�������һ��ɾ��button
	private static Map<String, Integer> mFaceMap = new LinkedHashMap<String, Integer>();
	private static Context mContext;
	// Ĭ�ϴ��ͼƬ��·��
	public final static String DEFAULT_SAVE_IMAGE_PATH = Environment.getExternalStorageDirectory() + File.separator
			+ "CircleDemo" + File.separator + "Images" + File.separator;

	public static int mKeyBoardH = 0;

	@Override
	public void onCreate() {
		super.onCreate();

		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
		initFaceMap();
//		HelperUtil.sendUserPayAction(getApplicationContext(), "0", BBLConstant.ACTION_APP_START, "0", "0", "0");
		ZYPayTools.getInstance().initPay("10001", "h5o4uyfiexpb5gzebss8ks59bef59id2");
		mContext = getApplicationContext();
		initImageLoader();
	}

	/** ��ʼ��imageLoader */
	private void initImageLoader() {
		DisplayImageOptions options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.color.bg_no_photo)
				.showImageOnFail(R.color.bg_no_photo).showImageOnLoading(R.color.bg_no_photo).cacheInMemory(true)
				.cacheOnDisk(true).build();

		File cacheDir = new File(DEFAULT_SAVE_IMAGE_PATH);
		ImageLoaderConfiguration imageconfig = new ImageLoaderConfiguration.Builder(this)
				.threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
				.diskCacheSize(50 * 1024 * 1024).diskCacheFileCount(200).diskCache(new UnlimitedDiskCache(cacheDir))
				.diskCacheFileNameGenerator(new Md5FileNameGenerator()).defaultDisplayImageOptions(options).build();

		ImageLoader.getInstance().init(imageconfig);
	}

	public static Context getContext() {
		return mContext;
	}

	public static Map<String, Integer> getFaceMap() {
		if (!mFaceMap.isEmpty())
			return mFaceMap;
		return null;
	}

	private void initFaceMap() {
		// TODO Auto-generated method stub
		mFaceMap.put("[����]", R.drawable.f_static_000);
		mFaceMap.put("[��Ƥ]", R.drawable.f_static_001);
		mFaceMap.put("[΢Ц]", R.drawable.f_static_023);
		mFaceMap.put("[͵Ц]", R.drawable.f_static_003);
		mFaceMap.put("[�ɰ�]", R.drawable.f_static_018);
		mFaceMap.put("[ɫ]", R.drawable.f_static_019);
		mFaceMap.put("[����]", R.drawable.f_static_020);
		mFaceMap.put("[�ô�]", R.drawable.f_static_005);
		mFaceMap.put("[��]", R.drawable.f_static_058);
		mFaceMap.put("[��ͷ]", R.drawable.f_static_007);
		mFaceMap.put("[õ��]", R.drawable.f_static_008);
		mFaceMap.put("[����]", R.drawable.f_static_009);
		mFaceMap.put("[����]", R.drawable.f_static_028);
		mFaceMap.put("[����]", R.drawable.f_static_030);
		mFaceMap.put("[����]", R.drawable.f_static_038);
		mFaceMap.put("[ӵ��]", R.drawable.f_static_045);
		mFaceMap.put("[����]", R.drawable.f_static_054);
		mFaceMap.put("[ǿ]", R.drawable.f_static_052);
		mFaceMap.put("[����]", R.drawable.f_static_068);
		mFaceMap.put("[�ټ�]", R.drawable.f_static_004);

	}
}
