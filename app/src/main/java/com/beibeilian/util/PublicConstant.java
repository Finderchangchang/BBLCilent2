package com.beibeilian.util;

import android.os.Environment;

public class PublicConstant {

	/**
	 * ��������
	 */
	public static final String XMPPDomain = "@wpy";

	public static final String GroupXMPPDomain = "@conference.wpy";

	/**
	 * ʡ
	 */
	public static final String MeDataDialogProvinceTag = "MeDataDialogProvinceTag";
	/**
	 * ��
	 */
	public static final String MeDataDialogCityTag = "MeDataDialogCityTag";

	/**
	 * ����
	 */
	public static final String MeDataDialogYearTag = "MeDataDialogYearTag";

	/**
	 * ��������
	 */
	public static final String MeDataDialogConditionYearTag = "MeDataDialogConditionYearTag";

	/**
	 * ���
	 */
	public static final String MeDataDialogHeightTag = "MeDataDialogHeightTag";

	/**
	 * ����
	 */
	public static final String MeDataDialogWeightTag = "MeDataDialogWeightTag";

	/**
	 * Ѫ��
	 */
	public static final String MeDataDialogBloodTag = "MeDataDialogBloodTag";

	/**
	 * ѧ��
	 */
	public static final String MeDataDialogEducationTag = "MeDataDialogEducationTag";

	/**
	 * ְҵ
	 */
	public static final String MeDataDialogJobTag = "MeDataDialogJobTag";

	/**
	 * ��н
	 */
	public static final String MeDataDialogMonthlyTag = "MeDataDialogMonthlyTag";

	/**
	 * ����
	 */
	public static final String MeDataDialogHouseTag = "MeDataDialogHouseTag";

	/**
	 * ϲ��������
	 */
	public static final String MeDataDialogLikeoppositesexTag = "MeDataDialogLikeoppositesexTag";

	/**
	 * ����״̬
	 */
	public static final String MeDataDialogMarriagestatusTag = "MeDataDialogMarriagestatusTag";

	/**
	 * ��ǰ��
	 */
	public static final String MeDataDialogMarriageSexTag = "MeDataDialogMarriageSexTag";

	/**
	 * �����
	 */
	public static final String MeDataDialogPlaceotherloveTag = "MeDataDialogPlaceotherloveTag";

	/**
	 * ��ҪС��
	 */
	public static final String MeDataDialogIswantchildTag = "MeDataDialogIswantchildTag";

	/**
	 * �͸�ĸ
	 */
	public static final String MeDataDialogANDFMOMTag = "MeDataDialogANDFMOMTag";

	/**
	 * �Ա�
	 */
	public static final String MeDataDialogSEXTag = "MeDataDialogSEXTag";

	/**
	 * dialog�ύ
	 */
	public static final String DialogSubmit = "�������������,���Ժ�...";

	/**
	 * toast�������쳣
	 */
	public static final String ToastCatch = "���������Ƿ���û��Ժ�����";

	/**
	 * size>0
	 */
	public static final int JsonYesUI = 1;

	/**
	 * size==0
	 */
	public static final int JsonNoUI = 0;

	/**
	 * catch
	 */
	public static final int JsonCatch = -1;

	/**
	 * ˢ��֪ͨ
	 */
	public static final int RefreashUI = 2;

	/**
	 * updateList
	 */
	public static final int UpdateList = 3;

	/**
	 * ��ҳ��
	 */
	public static int PageSize = 15;

	/**
	 * �ļ�·��
	 */
	public static String FilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/beibeilian/image/";

	public static String VoiceFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ "/beibeilian/voice/";

	/**
	 * apk
	 */
	public static String APKPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/beibeilian/apk/";;

	/**
	 * crash
	 */
	public static String CRASHPATH = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ "/beibeilian/crash/";;

	/**
	 * ��������
	 */
	public static String CodeServicePackName = "com.beibeilian.service.CoreIMService";

	/**
	 * ��Ϣ����
	 */
	public static final int MessagePend = 1;

	public static final int VisitPend = 2;

	public static final int NetPend = 3;

	public static final int BADPend = 4;

	public static final int PADPend = 5;

	public static final int VERSIONPend = 6;

	public static final int ZANPend = 7;

	public static final int COMMITPend = 8;

	public static final int ANLIANPend = 9;

	public static final int POINTSPend = 10;

	public static final int STARTCOMMANDSERVICCEPEND = 11;
	
	public static final int GPSPend = 12;

	public static final String VISITTYPE = "0";

	public static final String TIETYPE = "1";

	public static final String ANLIANTYPE = "2";
	

	public static final String GROUPTYPE = "3";

	public static final String MYQAQTYPE = "4";

	public static final int GroupMessagePend = 11;
}
