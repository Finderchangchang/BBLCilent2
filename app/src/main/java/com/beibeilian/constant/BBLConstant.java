package com.beibeilian.constant;

import com.beibeilian.util.HttpConstantUtil;

/**
 *
 * @author ����:��ƽԭ, ����ʱ��: 2016��8��20�� ����9:36:43
 * @desc ˵��:
 *
 */
public class BBLConstant {

	public static final int MEMBER_STATE_NUMBER_OUT = 0;// ���ǻ�Ա,�����ѳ�,�շ�

	public static final int MEMBER_STATE_YES = 1;// �ǻ�Ա������ʹ��

	public static final int MEMBER_STATE_OUT = -1;// ����

	public static final int MEMBER_STATE_NUMBER_NO_OUT = 2;// δ��������

	public static final int ANLIAN_STATE_YES = 1;// �����ɹ�

	public static final int ANLIAN_STATE_OUT = -1;// �Ѿ�������

	public static final int ANLIAN_STATE_NO = 0;// ����ʧ��

	public static final String ACTION_APP_START = "0";// APP����

	public static final String ACTION_PAY_CONFIRM = "1";// ֧��ȷ��

	public static final String ACTION_PAY_CANCEL = "2";// ֧��ȡ��

	public static final String ACTION_PAY_FAILE = "3";// ֧��ʧ��

	public static final String ACTION_PAY_UNKNOW = "4";// ֧��δ֪

	public static final String ACTION_PAY_SUCCESS = "5";// ֧���ɹ�

	public static final String ACTION_PAY_UNKNOW_NO = "6";// ֧��δ֪NO

	/**
	 * ͼƬ��ַǰ׺
	 */
	public static final String PHOTO_BEFORE_URL = HttpConstantUtil.FILE_UPLOAD_PreUrl+"upload/";
	
	public static final String PANDASDK_ID="c246e69b730e43ec803dab213d30c8a5";
}
