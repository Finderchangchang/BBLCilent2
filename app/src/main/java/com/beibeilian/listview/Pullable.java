package com.beibeilian.listview;

public interface Pullable {
	/**
	 * �ж��Ƿ�����������������Ҫ�������ܿ���ֱ��return false
	 * 
	 * @return true��������������򷵻�false
	 */
	boolean canPullDown();

	boolean canPullUp();
}
