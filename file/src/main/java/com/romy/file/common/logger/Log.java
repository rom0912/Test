package com.romy.file.common.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log {

	private static final Logger log = LoggerFactory.getLogger(Log.class);

	/**
	 * 로그 디버깅용
	 *
	 * @param message
	 */

	public static void Debug() {
		Debug("####################" + Thread.currentThread().getStackTrace()[2].getClassName() + "."
				+ Thread.currentThread().getStackTrace()[2].getMethodName());
	}

	public static void DebugStart() {
		Log.Debug("####################" + Thread.currentThread().getStackTrace()[2].getClassName() + "."
				+ Thread.currentThread().getStackTrace()[2].getMethodName() + " Start");
	}

	public static void DebugEnd() {
		Log.Debug("####################" + Thread.currentThread().getStackTrace()[2].getClassName() + "."
				+ Thread.currentThread().getStackTrace()[2].getMethodName() + " End");
	}

	public static void Debug(String message) {
		if (log.isDebugEnabled())
			log.debug(message);
	}

	/**
	 * 로그 정보
	 *
	 * @param message
	 */
	public static void Info(String message) {
		if (log.isDebugEnabled())
			log.info(message);
	}

	/**
	 * 로그 위험 정보
	 *
	 * @param message
	 */
	public static void Warn(String message) {
		log.warn(message);
	}

	/**
	 * 로그 에러 정보
	 *
	 * @param message
	 */
	public static void Error(String message) {
		log.error(message);
	}
	
}
