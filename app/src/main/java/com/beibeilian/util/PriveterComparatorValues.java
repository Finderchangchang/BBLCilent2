package com.beibeilian.util;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import com.beibeilian.privateletter.model.HomeMessage;
import com.beibeilian.privateletter.model.MessageList;

public class PriveterComparatorValues implements Comparator<MessageList> {

	@Override
	public int compare(MessageList object1, MessageList object2) {
		int result = 0;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date1 = formatter.parse(object1.getFromtime());
			Date date2 = formatter.parse(object2.getFromtime());
			result = date1.compareTo(date2);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}

}
