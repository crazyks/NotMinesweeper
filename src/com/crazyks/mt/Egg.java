package com.crazyks.mt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.os.Environment;

public class Egg {
	private static final byte[] head = "Copyright@crazyks 2013 V1.0".getBytes();
	static final File save = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CrazyksSoftware" + File.separator + "NotMinesweeper" + File.separator + "save.dat");
	// 完美表演
	int COUNTER_FOR_COMP_AND_OVER100 = 0;
	// 见多识广
	int COUNTER_FOR_NUMBER1_AND_RIGHT = 0;
	int COUNTER_FOR_NUMBER2_AND_RIGHT = 0;
	int COUNTER_FOR_NUMBER3_AND_RIGHT = 0;
	int COUNTER_FOR_NUMBER4_AND_RIGHT = 0;
	int COUNTER_FOR_NUMBER5_AND_RIGHT = 0;
	int COUNTER_FOR_NUMBER6_AND_RIGHT = 0;
	int COUNTER_FOR_NUMBER7_AND_RIGHT = 0;
	int COUNTER_FOR_NUMBER8_AND_RIGHT = 0;
	// 失败者
	int COUNTER_FOR_CONSECUTIVE_FAILURE = 0;
	// 自定义生活
	int COUNTER_FOR_CUSTOMMODE = 0;
	// 新手
	int COUNTER_FOR_EASYMODE_COMP_AND_OVER30 = 0;
	// 成手
	int COUNTER_FOR_NORMALMODE_COMP_AND_OVER40 = 0;
	// 专家
	int COUNTER_FOR_HARDMODE_COMP_AND_OVER50 = 0;
	// 狙击手
	int COUNTER_FOR_SOLVED = 0;
	// 每日一练
	int COUNTER_FOR_CONSECUTIVE_DAYS = 0;
	// 好奇心
	int FLAG_FOR_FIND_EMAIL = 0;
	int FLAG_FOR_CLICK_AFTER_EMAIL_FOUND = 0;
	
	final int[] getValues() {
		return new int[] {
				COUNTER_FOR_COMP_AND_OVER100,
				COUNTER_FOR_NUMBER1_AND_RIGHT,
				COUNTER_FOR_NUMBER2_AND_RIGHT,
				COUNTER_FOR_NUMBER3_AND_RIGHT,
				COUNTER_FOR_NUMBER4_AND_RIGHT,
				COUNTER_FOR_NUMBER5_AND_RIGHT,
				COUNTER_FOR_NUMBER6_AND_RIGHT,
				COUNTER_FOR_NUMBER7_AND_RIGHT,
				COUNTER_FOR_NUMBER8_AND_RIGHT,
				COUNTER_FOR_CONSECUTIVE_FAILURE,
				COUNTER_FOR_CUSTOMMODE,
				COUNTER_FOR_EASYMODE_COMP_AND_OVER30,
				COUNTER_FOR_NORMALMODE_COMP_AND_OVER40,
				COUNTER_FOR_HARDMODE_COMP_AND_OVER50,
				COUNTER_FOR_SOLVED,
				COUNTER_FOR_CONSECUTIVE_DAYS,
				FLAG_FOR_FIND_EMAIL,
				FLAG_FOR_CLICK_AFTER_EMAIL_FOUND
		};
	}
	
	Egg() {
	}
	
	Egg(int[] values) {
	    this();
	    initValues(values);
	}
	
	final void initValues(int[] values) {
		COUNTER_FOR_COMP_AND_OVER100 = values[0];
		COUNTER_FOR_NUMBER1_AND_RIGHT = values[1];
		COUNTER_FOR_NUMBER2_AND_RIGHT = values[2];
		COUNTER_FOR_NUMBER3_AND_RIGHT = values[3];
		COUNTER_FOR_NUMBER4_AND_RIGHT = values[4];
		COUNTER_FOR_NUMBER5_AND_RIGHT = values[5];
		COUNTER_FOR_NUMBER6_AND_RIGHT = values[6];
		COUNTER_FOR_NUMBER7_AND_RIGHT = values[7];
		COUNTER_FOR_NUMBER8_AND_RIGHT = values[8];
		COUNTER_FOR_CONSECUTIVE_FAILURE = values[9];
		COUNTER_FOR_CUSTOMMODE = values[10];
		COUNTER_FOR_EASYMODE_COMP_AND_OVER30 = values[11];
		COUNTER_FOR_NORMALMODE_COMP_AND_OVER40 = values[12];
		COUNTER_FOR_HARDMODE_COMP_AND_OVER50 = values[13];
		COUNTER_FOR_SOLVED = values[14];
		COUNTER_FOR_CONSECUTIVE_DAYS = values[15];
		FLAG_FOR_FIND_EMAIL = values[16];
		FLAG_FOR_CLICK_AFTER_EMAIL_FOUND = values[17];
	}
	
	final boolean isWellPlayed() {
		return (COUNTER_FOR_COMP_AND_OVER100 > 0);
	}
	
	final boolean isKnownMuch() {
		return (COUNTER_FOR_NUMBER1_AND_RIGHT > 0
				&& COUNTER_FOR_NUMBER2_AND_RIGHT > 0
				&& COUNTER_FOR_NUMBER3_AND_RIGHT > 0
				&& COUNTER_FOR_NUMBER4_AND_RIGHT > 0
				&& COUNTER_FOR_NUMBER5_AND_RIGHT > 0
				&& COUNTER_FOR_NUMBER6_AND_RIGHT > 0
				&& COUNTER_FOR_NUMBER7_AND_RIGHT > 0
				&& COUNTER_FOR_NUMBER8_AND_RIGHT > 0);
	}
	
	final boolean isLoser() {
		return (COUNTER_FOR_CONSECUTIVE_FAILURE >= 10000);
	}
	
	final boolean isDIYLife() {
		return (COUNTER_FOR_CUSTOMMODE >= 100);
	}
	
	final boolean isBeginner() {
		return (COUNTER_FOR_EASYMODE_COMP_AND_OVER30 > 0);
	}
	
	final boolean isPractitioner() {
		return (COUNTER_FOR_NORMALMODE_COMP_AND_OVER40 > 0);
	}
	
	final boolean isExpert() {
		return (COUNTER_FOR_HARDMODE_COMP_AND_OVER50 > 0);
	}
	
	final boolean isSniper() {
		return (COUNTER_FOR_SOLVED >= 242);
	}
	
	final boolean isDailyPractice() {
		return (COUNTER_FOR_CONSECUTIVE_DAYS >= 7);
	}
	
	final boolean isCurious() {
		return (FLAG_FOR_FIND_EMAIL > 0
				&& FLAG_FOR_CLICK_AFTER_EMAIL_FOUND > 0);
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i : getValues()) {
			sb.append(i);
			sb.append(' ');
		}
		return sb.toString().trim();
	}

	private final static boolean isTheSameBytes(final byte[] src, final byte[] target) {
		if (src == null || target == null || src.length != target.length) {
			return false;
		}
		for (int i = 0; i < src.length; i++) {
			if (src[i] != target[i]) {
				return false;
			}
		}
		return true;
	}
	
	private final static byte[] readBytes(File file) {
		byte[] ret = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			long len = file.length();
			ret = new byte[(int) len];
			fis.read(ret);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			ret = null;
		} catch (IOException e) {
			e.printStackTrace();
			ret = null;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return ret;
	}
	
	private final static void writeBytes(File file, byte[] data) {
		FileOutputStream fos = null;
		try {
		    if (!file.getParentFile().exists()) {
		        file.getParentFile().mkdirs();
		    }
			fos = new FileOutputStream(file);
			fos.write(data);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/*
	 * 解密过程
	 * Step1. CHECK MD5 of the first (length - 16) bytes AND COMPARE TO the following 16 bytes
	 * Step2. Using the first 8 bytes as the key, DECRYPT the following (length - 24) bytes as the Content
	 * Step3. CHECK MD5 of the first (contentLen - 16) bytes AND COMPARE TO the following 16 bytes
	 * Step4. CHECK the Head of the Content
	 * Step5. Parse Body
	 */
	final static Egg load() throws Exception{
		byte[] savedTmp = readBytes(save);
		int len = savedTmp.length;
		byte[] key = new byte[8];
		byte[] finalData = new byte[len - 16];
		byte[] md5f = new byte[16];
		System.arraycopy(savedTmp, 0, key, 0, 8);
		System.arraycopy(savedTmp, 0, finalData, 0, len - 16);
		System.arraycopy(savedTmp, len - 16, md5f, 0, 16);
		Guard guard = new Guard(key);
		if (!isTheSameBytes(md5f, guard.md5(finalData))) {
			throw new Exception("MODIFIED");
		}
		byte[] encryptedData = new byte[len - 24];
		System.arraycopy(savedTmp, 8, encryptedData, 0, len - 24);
		byte[] newData = guard.decrypt(encryptedData);
		byte[] data = new byte[newData.length - 16];
		byte[] md5 = new byte[16];
		System.arraycopy(newData, 0, data, 0, newData.length - 16);
		System.arraycopy(newData, newData.length - 16, md5, 0, 16);
		if (!isTheSameBytes(md5, guard.md5(data))) {
			throw new Exception("MODIFIED");
		}
		byte[] myHead = new byte[head.length];
		byte[] myBody = new byte[data.length - myHead.length];
		System.arraycopy(data, 0, myHead, 0, myHead.length);
		System.arraycopy(data, myHead.length, myBody, 0, data.length - myHead.length);
		if (!isTheSameBytes(myHead, head)) {
			throw new Exception("MODIFIED");
		}
		Egg egg = new Egg();
		int[] values = new int[egg.getValues().length];
		for (int i = 0; i < values.length; i++) {
			values[i] = byteArrayToInt(myBody, i * 4);
		}
		egg.initValues(values);
		return egg;
	}
	
	/*
	 * 加密过程
	 * Step1. Content = Head + Body 
	 * Step2. NewContent = Content + MD5(Content)
	 * Step3. EncryptedKey = toByteArr(RandomInt1) + toByteArr(RandomInt2)
	 * Step4. EncryptedContent = DES(NewContent)
	 * Step5. FinalContent = EncryptedKey + EncryptContent
	 * Step6. WriteToFile = FinalContent + MD5(FinalContent)
	 */
	final synchronized static void sync(Egg egg) throws Exception{
		int index = 0;
		byte[] content = new byte[head.length + (egg.getValues().length << 2)];
		System.arraycopy(head, 0, content, 0, head.length);
		index += head.length;
		for (int i : egg.getValues()) {
			System.arraycopy(intToByteArray(i), 0, content, index, 4);
			index += 4;
		}
		Random rd = new Random();
		rd.setSeed(System.currentTimeMillis());
		byte[] key = new byte[8];
		System.arraycopy(intToByteArray(rd.nextInt()), 0, key, 0, 4);
		System.arraycopy(intToByteArray(rd.nextInt()), 0, key, 4, 4);
		Guard guard = new Guard(key);
		byte[] md5 = guard.md5(content);
		byte[] newContent = new byte[content.length + md5.length];
		System.arraycopy(content, 0, newContent, 0, content.length);
		System.arraycopy(md5, 0, newContent, content.length, md5.length);
		byte[] encryptedContent = guard.encrypt(newContent);
		byte[] finalContent = new byte[key.length + encryptedContent.length];
		System.arraycopy(key, 0, finalContent, 0, key.length);
		System.arraycopy(encryptedContent, 0, finalContent, key.length, encryptedContent.length);
		byte[] md5f = guard.md5(finalContent);
		byte[] data = new byte[finalContent.length + md5f.length];
		System.arraycopy(finalContent, 0, data, 0, finalContent.length);
		System.arraycopy(md5f, 0, data, finalContent.length, md5f.length);
		writeBytes(save, data);
	}
	
	final ArrayList<String> whatIsNew(Context context, Egg other) {
		ArrayList<String> newArival = new ArrayList<String>();
		if (context != null && other != null) {
			if (other.isWellPlayed() && !this.isWellPlayed()) {
				newArival.add(context.getString(R.string.well_palyed));
			}
			if (other.isKnownMuch() && !this.isKnownMuch()) {
				newArival.add(context.getString(R.string.wide_exp));
			}
			if (other.isLoser() && !this.isLoser()) {
				newArival.add(context.getString(R.string.loser));
			}
			if (other.isDIYLife() && !this.isDIYLife()) {
				newArival.add(context.getString(R.string.diy_life));
			}
			if (other.isBeginner() && !this.isBeginner()) {
				newArival.add(context.getString(R.string.beginner));
			}
			if (other.isPractitioner() && !this.isPractitioner()) {
				newArival.add(context.getString(R.string.practitioner));
			}
			if (other.isExpert() && !this.isExpert()) {
				newArival.add(context.getString(R.string.expert));
			}
			if (other.isSniper() && !this.isSniper()) {
				newArival.add(context.getString(R.string.sniper));
			}
			if (other.isDailyPractice() && !this.isDailyPractice()) {
				newArival.add(context.getString(R.string.daily_practice));
			}
			if (other.isCurious() && !this.isCurious()) {
				newArival.add(context.getString(R.string.curiosity));
			}
		}
		return newArival;
	}
	
	private final static byte[] intToByteArray(int i) {
		byte[] result = new byte[4];
		result[0] = (byte) ((i >> 24) & 0xFF);
		result[1] = (byte) ((i >> 16) & 0xFF);
		result[2] = (byte) ((i >> 8) & 0xFF);
		result[3] = (byte) (i & 0xFF);
		return result;
	}
	
	private final static int byteArrayToInt(byte[] b, int offset) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (b[i + offset] & 0x000000FF) << shift;
		}
		return value;
	}
}
