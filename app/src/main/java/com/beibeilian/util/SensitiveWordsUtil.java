package com.beibeilian.util;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class SensitiveWordsUtil {

	public static boolean sensitiveWords(String words) {
		if (words.contains("qq") || words.contains("QQ")
				|| words.contains("�ۿ�") || words.contains("΢��")
				|| words.contains("weixin") || words.contains("�ֻ���")
				|| words.contains("��ϵ��ʽ") || words.contains("İİ")
				|| words.contains("֧����") || words.contains("�˺�")
				|| words.contains("���п�") || words.contains("����")
				|| words.contains("��")) {
			return true;
		}
		if (hasDigit(words)) {
			return true;
		}
		if (judgeContainsStr(words)) {
			return true;
		}
		return false;
	}

	public static boolean hasDigit(String content) {
		boolean flag = false;
		Pattern p = Pattern.compile(".*\\d+.*");
		Matcher m = p.matcher(content);
		if (m.matches())
			flag = true;
		return flag;
	}

	public static boolean judgeContainsStr(String cardNum) {
		String regex = ".*[a-zA-Z]+.*";
		Matcher m = Pattern.compile(regex).matcher(cardNum);
		return m.matches();
	}
}
