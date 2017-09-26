package com.beibeilian.util;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import com.beibeilian.privateletter.model.HomeMessage;

public  class ComparatorValues implements Comparator<HomeMessage>{

    @Override
    public int compare(HomeMessage object1, HomeMessage object2) {
    	int result=0;
    	try
    	{
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date date1=formatter.parse(object1.getTime());
    	Date date2=formatter.parse(object2.getTime());
    	result=date1.compareTo(date2);
    	}
    	catch (Exception e) {
			// TODO: handle exception
		}
        return result;
    }        
    
}
