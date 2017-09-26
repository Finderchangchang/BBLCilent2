package com.beibeilian.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.util.Log;

/**
 * ʱ���ʽ������ ת�������磬���磬����ǰ��
 * 
 * @author ��ƽԭ
 * 
 */
@SuppressLint("SimpleDateFormat")
public class DateFormatUtil {

	// private static final String TAG = Class.class.getName();
	private static final String TAG = "DDDateFormat";
	/**
	 * ʱ��ת����ʽ
	 */
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/**
	 * String ת��Ϊ Date
	 * 
	 * @param time
	 * @return
	 */
	public static Date getDateByString(String time) {
		Date date = null;
		try {
			date = getSDF().parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * Date ת��Ϊ String
	 * 
	 * @param date
	 * @return
	 */
	public static String getStringByDate(Date date) {
		return getSDF().format(date);
	}

	/**
	 * Date ת��Ϊ String
	 * 
	 * @return
	 */
	public static String getStringByDate() {
		return getSDF().format(new Date());
	}

	/**
	 * ��ȡSimpleDateFormat
	 * 
	 * @return
	 */
	private static SimpleDateFormat getSDF() {
		return new SimpleDateFormat(DATE_FORMAT);
	}

	/**
	 * Calendarת��ΪString
	 * 
	 * @param c
	 * @return
	 */
	public static String getStringByCalendar(Calendar c) {
		String dateStr = null;
		dateStr = getSDF().format(c.getTime());
		return dateStr;
	}

	/**
	 * String ת��ΪCalendar
	 * 
	 * @param time
	 * @return
	 */
	public static Calendar getCalendarByString(String time) {
		Calendar c = Calendar.getInstance();
		Date date = getDateByString(time);
		c.setTime(date);
		return c;
	}

	/**
	 * ��ȡ��ǰʱ��
	 * 
	 * @return
	 */
	public static String getCurrentTime() {
		Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
		String str = getSDF().format(curDate);
		return str;
	}

	/**
	 * ����һ�����ڣ����������ڼ����ַ���
	 * 
	 * @param txtDate
	 * @return
	 */
	public static String getWeek(String txtDate) {
		Date date = getDateByString(txtDate);
		String week = new SimpleDateFormat("EEEE").format(date);
		Log.w(TAG, "week=" + week);
		return week;
	}

	/**
	 * ���һ���������ڵ��ܵ����ڼ������ڣ���Ҫ�ҳ�2002��2��3�������ܵ�����һ�Ǽ���
	 * 
	 * @param sdate
	 * @param num
	 * @return
	 */
	public static String getWeek(String sdate, String num) {
		// ��ת��Ϊʱ��
		Date dd = getDateByString(sdate);
		Calendar c = Calendar.getInstance();
		c.setTime(dd);
		if (num.equals("1")) // ��������һ���ڵ�����
			c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		else if (num.equals("2")) // �������ڶ����ڵ�����
			c.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
		else if (num.equals("3")) // �������������ڵ�����
			c.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
		else if (num.equals("4")) // �������������ڵ�����
			c.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
		else if (num.equals("5")) // �������������ڵ�����
			c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
		else if (num.equals("6")) // �������������ڵ�����
			c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		else if (num.equals("0")) // �������������ڵ�����
			c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		return new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
	}

	/**
	 * �����������ڼ�ļ������
	 * 
	 * @param oldTime
	 *            ֮ǰ��ʱ��
	 * @param newTime
	 *            ֮���ʱ��
	 * @return
	 */
	public static long getDaysFromTwoDate(String oldTime, String newTime) {
		if (oldTime == null || oldTime.equals("")) {
			return 0;
		}
		if (newTime == null || newTime.equals("")) {
			return 0;
		}

		SimpleDateFormat sDateFormat = new SimpleDateFormat(DATE_FORMAT);
		long days = 0;
		try {
			Date date1 = sDateFormat.parse(oldTime);
			Date date2 = sDateFormat.parse(newTime);
			days = (date2.getTime() - date1.getTime()) / (24 * 60 * 60 * 1000); // ͨ��getTime()��������ʱ��Dateת���ɺ����ʽLong���ͣ����м���
		} catch (Exception e) {
			e.printStackTrace();
		}
		return days;
	}

	/**
	 * �����������ڼ�ĺ�����
	 * 
	 * @param oldTime
	 *            ֮ǰ��ʱ��
	 * @param newTime
	 *            ֮���ʱ��
	 * @return
	 */
	public static long getSecondsFromTwoDate(String oldTime, String newTime) {
		if (oldTime == null || oldTime.equals("")) {
			// oldTime = "2013-4-30 17:39:32";
			return 0;
		}
		if (newTime == null || newTime.equals("")) {
			return 0;
		}

		SimpleDateFormat sDateFormat = new SimpleDateFormat(DATE_FORMAT);
		long min = 0;
		try {
			Date date1 = sDateFormat.parse(oldTime);
			Date date2 = sDateFormat.parse(newTime);
			min = (date2.getTime() - date1.getTime()) / 1000; // ͨ��getTime()��������ʱ��Dateת���ɺ����ʽLong���ͣ����м���
			// DDLog.e(TAG, "oldTime------------->="+oldTime);
			// DDLog.e(TAG, "newTime------------->="+newTime);
			// DDLog.e(TAG, "�����������ڼ�ĺ�����------------->="+min+"��");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return min;
	}

	/**
	 * ������������֮��ķ�����
	 * 
	 * @param oldTime
	 * @param newTime
	 * @return
	 */
	public static int getMinitesFromTwoDate(String oldTime, String newTime) {
		// long seconds = getSecondsFromTwoDate(oldTime, newTime);
		int min = -1;
		int year = getYear(newTime) - getYear(oldTime);
		if (year == 0) {
			int month = getMonth(newTime) - getMonth(oldTime);
			if (month == 0) {
				int day = getDay(newTime) - getDay(oldTime);
				if (day == 0) {
					int hour = Integer.parseInt(getHour(newTime).trim()) - Integer.parseInt(getHour(oldTime).trim());
					if (hour == 0) {
						min = Integer.parseInt(getMinute(newTime).trim()) - Integer.parseInt(getMinute(oldTime).trim());
					}
				}
			}
		}
		return min;
	}

	/**
	 * �ж϶���ʱ���Ƿ���ͬһ����
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameWeekDates(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date1);
		cal2.setTime(date2);
		int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
		if (0 == subYear) {
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
				return true;
		} else if (1 == subYear && 11 == cal2.get(Calendar.MONTH)) {
			// ���12�µ����һ�ܺ�������һ�ܵĻ������һ�ܼ���������ĵ�һ��
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
				return true;
		} else if (-1 == subYear && 11 == cal1.get(Calendar.MONTH)) {
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
				return true;
		}
		return false;
	}

	/**
	 * ��ȡ�·�
	 * 
	 * @param time
	 * @return
	 */
	public static int getMonth(String time) {
		return getCalendarByString(time).get(Calendar.MONTH) + 1;
	}

	/**
	 * ��ȡ�µ�����
	 * 
	 * @param time
	 * @return
	 */
	public static int getMonthMax(String time) {
		int year = getYear(time);
		int month = getMonth(time);
		if (month == 2) {
			return (year % 4 == 0 && (year % 400 == 0 || year % 100 != 0)) ? 29 : 28;
		} else {
			int k = (int) (Math.abs(month - 7.5));
			return (k % 2) == 0 ? 31 : 30;
		}
	}

	/**
	 * ��ȡ���
	 * 
	 * @param time
	 * @return
	 */
	public static int getYear(String time) {
		return getCalendarByString(time).get(Calendar.YEAR);
	}

	/**
	 * ��ȡ����
	 * 
	 * @param time
	 * @return
	 */
	public static int getDay(String time) {
		return getCalendarByString(time).get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * ��ȡСʱ
	 * 
	 * @param time
	 * @return
	 */
	public static String getHour(String time) {
		String hourStr = null;
		int hour = getCalendarByString(time).get(Calendar.HOUR_OF_DAY);
		if (hour < 10) {
			hourStr = "0" + hour;
		} else {
			hourStr = hour + "";
		}
		return hourStr;
	}

	/**
	 * ��ȡ����
	 * 
	 * @param time
	 * @return
	 */
	public static String getMinute(String time) {
		String minuteStr = null;
		int minute = getCalendarByString(time).get(Calendar.MINUTE);
		if (minute < 10) {
			minuteStr = "0" + minute;
		} else {
			minuteStr = minute + "";
		}
		return minuteStr;
	}

	/**
	 * ��ȡʱ����
	 * 
	 * @param time
	 * @return
	 */
	public static String getHourAndMin(String time) {
		// return getCalendarByString(time).get(Calendar.DAY_OF_MONTH);
		return getHour(time) + ":" + getMinute(time);
	}

	/**
	 * ��ȡʱ��
	 * 
	 * @param time
	 * @return
	 */
	public static String getDDTime(String time) {
		StringBuffer timeStr = new StringBuffer();
		String currentTime = getCurrentTime();
		// DDLog.e(TAG, "time-------------->="+time);
		// DDLog.e(TAG, "currentTime-------------->="+currentTime);
		int year = getYear(currentTime) - getYear(time);
		int month_time = getMonth(time);
		int day_time = getDay(time);
		int month = getMonth(currentTime) - month_time;
		int day = getDay(currentTime) - day_time;
		// DDLog.w(TAG, "day-------------->="+day);
		if (year != 0) {
			timeStr.append(getYear(time) + "��" + month_time + "��" + day_time + "��" + " ");
		} else if (year == 0 && month == 0) {

			switch (day) {
			case 0:
				String morningOrEvening = getMorningOrEvening(time);
				String hourAndMin = getHourAndMin(time);
				timeStr.append(morningOrEvening + "" + hourAndMin);
				Log.w(TAG, timeStr.toString());
				break;
			case 1:
				timeStr.append("���� ");
				morningOrEvening = getMorningOrEvening(time);
				hourAndMin = getHourAndMin(time);
				timeStr.append(morningOrEvening + "" + hourAndMin);
				Log.w(TAG, timeStr.toString());
				break;
			case 2:
				timeStr.append("ǰ�� ");
				morningOrEvening = getMorningOrEvening(time);
				hourAndMin = getHourAndMin(time);
				timeStr.append(morningOrEvening + "" + hourAndMin);
				Log.w(TAG, timeStr.toString());
				break;
			default:
				timeStr.append(month_time + "��" + day_time + "��" + " ");
				break;
			}
		} else if (year == 0 && month > 0) {

			timeStr.append(month_time + "��" + day_time + "��" + " ");
		}
		// timeStr.append(month + "��" + day + "��" + " ");

		// String morningOrEvening = getMorningOrEvening(time);
		// String hourAndMin = getHourAndMin(time);
		// timeStr.append(morningOrEvening + "" + hourAndMin);
		// Log.w(TAG, timeStr.toString());
		return timeStr.toString();
	}

	/**
	 * ����������,���õ���ǰʱ�����ڵ�����ǵڼ���
	 * 
	 * @return
	 */
	public static String getSeqWeek() {
		Calendar c = Calendar.getInstance(Locale.CHINA);
		String week = Integer.toString(c.get(Calendar.WEEK_OF_YEAR));
		if (week.length() == 1)
			week = "0" + week;
		String year = Integer.toString(c.get(Calendar.YEAR));
		return year + week;
	}

	/**
	 * ��ȡһ����ʱ���
	 * 
	 * @return
	 */
	public static String getMorningOrEvening(String time) {

		int hours = getDateByString(time).getHours();
		String timeName = null;
		switch (hours) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
			timeName = "�賿";
			break;
		case 7:
		case 8:
		case 9:
		case 10:
		case 11:
		case 12:
			timeName = "����";
			break;
		case 13:
		case 14:
		case 15:
		case 16:
		case 17:
		case 18:
			timeName = "����";
			break;
		case 19:
		case 20:
		case 21:
		case 22:
		case 23:
		case 24:
			timeName = "����";
			break;
		}
		// Log.w(TAG, "������"+timeName+","+hours+"��");
		return timeName;
	}

	/**
	 * ��ȡ�϶̵�ʱ����Ϣ
	 * 
	 * @param time
	 * @return
	 */
	public static String getShortTime(String time) {
		String shortstring = null;
		long now = Calendar.getInstance().getTimeInMillis();
		Date date = getDateByString(time);
		if (date == null)
			return shortstring;
		long deltime = (now - date.getTime()) / 1000;
		if (deltime > 365 * 24 * 60 * 60) {
			shortstring = (int) (deltime / (365 * 24 * 60 * 60)) + "��ǰ";
		} else if (deltime > 3 * 24 * 60 * 60) {
			shortstring = (int) (deltime / (24 * 60 * 60)) + "��ǰ";
		} else if (deltime > 2 * 24 * 60 * 60 && deltime <= 3 * 24 * 60 * 60) {
			shortstring = "ǰ��";
			// shortstring = (int) (deltime / (24 * 60 * 60)) + "��ǰ";
		} else if (deltime > 24 * 60 * 60 && deltime <= 24 * 60 * 60) {
			shortstring = "����";
			// shortstring = (int) (deltime / (24 * 60 * 60)) + "��ǰ";
		} else if (deltime > 60 * 60) {
			shortstring = (int) (deltime / (60 * 60)) + "Сʱǰ";
		} else if (deltime > 60) {
			shortstring = (int) (deltime / (60)) + "��ǰ";
		} else if (deltime > 1) {
			shortstring = deltime + "��ǰ";
		} else {
			shortstring = "1��ǰ";
		}
		// Log.w(TAG, "���ʱ��deltime="+deltime+",shortstring="+shortstring);
		return shortstring;
	}

}
