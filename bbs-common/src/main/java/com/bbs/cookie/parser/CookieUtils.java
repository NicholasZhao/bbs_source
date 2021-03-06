package com.bbs.cookie.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbs.cookie.CookieKeyEnum;
import com.bbs.util.EncryptUtils;

/**
 * 类CookieUtils.java的实现描述：TODO 类实现描述
 * 
 * @author jie.xu 2014年11月23日 下午8:54:27
 */
public class CookieUtils {
	/**
	 * String类型的Cookie值中用于间隔每对Key、Value之间的符号
	 */
	public static final char COOKIE_MAP_SEPARATOR_CHAR = '&';
	/**
	 * String类型的Cookie值中用于间隔Key和Value之间的符号
	 */
	public static final char COOKIE_KEY_VALUE_SEPARATOR_CHAR = '=';

	private static final Logger logger = LoggerFactory
			.getLogger(CookieUtils.class);

	/**
	 * 将Map转换为字符串
	 * 
	 * @return 如果传入的KV是空(null或者empty),则返回null
	 */
	public static String mapToStr(Map<CookieKeyEnum, String> kv) {
		if (kv == null || kv.isEmpty()) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		List<CookieKeyEnum> keys = new ArrayList<CookieKeyEnum>(kv.keySet());
		for (int i = 0, j = keys.size(); i < j; i++) {
			CookieKeyEnum key = keys.get(i);
			String value = kv.get(key);
			sb.append(key.getKey()).append(COOKIE_KEY_VALUE_SEPARATOR_CHAR)
					.append(value);
			// 不是最后一个则
			boolean notTheLast = (i < keys.size() - 1);
			if (notTheLast) {
				sb.append(COOKIE_MAP_SEPARATOR_CHAR);
			}
		}
		return sb.toString();
	}

	/**
	 * 根据CookieNameConfig的配置，将字符转换为该那么下的Map<CookieKeyEnum,String>.
	 * 对于那些不是该CookieName的CookieKey直接忽略掉
	 * 
	 * @return 如果转换失败返回emptyMap
	 */
	public static Map<CookieKeyEnum, String> strToKVMap(String value,
			CookieNameConfig cookieNameConfig) {
		// 分隔类似a=11&b=22
		String[] kvs = StringUtils.split(value, COOKIE_MAP_SEPARATOR_CHAR);
		if (kvs == null || kvs.length == 0) {
			return Collections.<CookieKeyEnum, String> emptyMap();
		}
		Map<CookieKeyEnum, String> kvMap = new HashMap<CookieKeyEnum, String>();
		for (String kv : kvs) {
			int offset = kv.indexOf(COOKIE_KEY_VALUE_SEPARATOR_CHAR);
			if (offset > 0 && offset < kv.length()) {
				CookieKeyEnum key = CookieKeyEnum.getEnum(kv.substring(0,
						offset));
				if (key != null) {
					if (cookieNameConfig.isKeyWithIn(key)) {
						kvMap.put(key, kv.substring(offset + 1, kv.length()));
					} else {
						logger.error("解析" + cookieNameConfig.getCookieName()
								+ "是发现Cookiekey" + key
								+ "没有配置在该Cookie name 下，它的值将被忽略掉。");
					}
				}
			}
		}
		return kvMap;
	}

	/**
	 * 将Cookie数组转换以cookie name为Key的，Cookie Value为Value的Map
	 * 
	 * @return cookies是空或者null,则返回emptyMap
	 */
	public static Map<String, String> arrayToMap(Cookie[] cookies) {
		if (cookies == null || cookies.length == 0) {
			return Collections.<String, String> emptyMap();
		}
		Map<String, String> values = new HashMap<String, String>(cookies.length);
		for (Cookie cookie : cookies) {
			values.put(cookie.getName(), cookie.getValue());
		}
		return values;
	}

	/**
	 * 如果是字符串"null",则转化为 <code>null</code>
	 * 
	 * @return <code>null</code>
	 */
	public static String filterNullValue(String value) {
		if (StringUtils.equalsIgnoreCase("null", value)) {
			return null;
		}
		return value;
	}

	public static String encrypt(String source) {
		return EncryptUtils.encrypt(source);
	}

	public static String decrypt(String source) {
		return EncryptUtils.decrypt(source);
	}
}
