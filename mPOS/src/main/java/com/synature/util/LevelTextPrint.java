package com.synature.util;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

/**
 * @author j1tth4
 * This utility class use for manage level of thai text 
 */
public class LevelTextPrint {
	
	/**
	 * @param prntTxt
	 * @return TextThreeLine
	 */
	public static class ThreeLevelByteCode{
		private byte[] line1;
		private byte[] line2;
		private byte[] line3;
		public byte[] getLine1() {
			return line1;
		}
		public void setLine1(byte[] line1) {
			this.line1 = line1;
		}
		public byte[] getLine2() {
			return line2;
		}
		public void setLine2(byte[] line2) {
			this.line2 = line2;
		}
		public byte[] getLine3() {
			return line3;
		}
		public void setLine3(byte[] line3) {
			this.line3 = line3;
		}
	}
	
	public static ThreeLevelByteCode parsingLaoLevel(String prntTxt){
		List<Byte> strLine1 = new ArrayList<Byte>();
		List<Byte> strLine2 = new ArrayList<Byte>();
		List<Byte> strLine3 = new ArrayList<Byte>();
		int i = 0;
		int code = 0;
		byte strChar = 0;
		ThreeLevelByteCode resultText = new ThreeLevelByteCode();
		for (; i < prntTxt.length(); i++) {
			code = prntTxt.charAt(i);
			switch (code) {
			case 3761: //ไม้กรรณ
			case 3764: //สระอิ
			case 3765: //สระอี
			case 3766: //สระอึ
			case 3767: //สระอื
				//ไม้กรรณ และ สระด้านบน เช่น อิ อี
				//Check if letter next to it is วรรณยุกต์
				if (i == prntTxt.length() - 1) {
					// This is the last letter
					strChar = (byte) prntTxt.charAt(i);
				} else {
					int nextCode = (int) prntTxt.charAt(i + 1);
					switch (nextCode) {// Comboine current and the next character into new one
					case 3784: //ไม้เอก
					case 3785: //ไม้โท
					case 3786: //ไม้ตรี
					case 3787: //ไม้จัตวา
					case 3788: //การันต์
					case 3789: //นิคหิต
						//วรรณยุกต์ และการันต์
						switch (nextCode) {
						case 3784:
							switch (code) {
							case 3761: //ไม้กรรณ์
								strChar = (byte) 132;
								break;
							case 3764: //อิ
								strChar = (byte) 133;
								break;
							case 3765: //อี
								strChar = (byte) 134;
								break;
							case 3766: //อึ
								strChar = (byte) 135;
								break;
							case 3767: //อื
								strChar = (byte) 136;
								break;
							}
							break;
						case 3785: //ไม้โท
							switch (code) {
							case 3761: //ไม้กรรณ์
								strChar = (byte) 152;
								break;
							case 3764: //อิ
								strChar = (byte) 153;
								break;
							case 3765: //อี
								strChar = (byte) 154;
								break;
							case 3766: //อึ
								strChar = (byte) 155;
								break;
							case 3767: //อื
								strChar = (byte) 156;
								break;
							}
							break;
//						case 3786: //ไม้ตรี
//							switch (code) {
//							case 3761: //ไม้กรรณ
//								nextCode = 3633;
//								break;
//							case 3764: //อิ
//								nextCode = 144;
//								break;
//							case 3765: //อี
//								nextCode = 132;
//								break;
//							case 3766: //อึ
//								nextCode = 136;
//								break;
//							case 3767: //อื
//								nextCode = 140;
//								break;
//							}
//							break;
//						case 3787: //ไมจัตวา
//							switch (code) {
//							case 3761: //ไม้หันอากาศ
//								nextCode = 3633;
//								break;
//							case 3764: //อิ
//								nextCode = 144;
//								break;
//							case 3765: //อี
//								nextCode = 132;
//								break;
//							case 3766: //อึ
//								nextCode = 136;
//								break;
//							case 3767: //อื
//								nextCode = 140;
//								break;
//							}
//							break;
//						case 3788: //การันต์
//							switch (code) {
//							case 3764: //อิ
//								nextCode = 148;
//								break;
//							}
//							break;
						}
						i += 1;
						break;
					default:
						strChar = (byte) prntTxt.charAt(i);
						break;
					}
				}
				strLine1 = strLine1.subList(0, strLine1.size() - 1);
				strLine1.add(strChar);
				break;
			case 3784: //ไม้เอก
			case 3785: //ไม้โท
			case 3786: //ไม้ตรี
			case 3787: //ไม้จัตวา
			case 3788: //การันต์
			case 3789: //นิคหิต
				//วรรณยุกต์
				// Check if letter next to it is วรรณยุกต์
				if (i == prntTxt.length() - 1) { // This is the last letter
					strLine1 = strLine1.subList(0, strLine1.size() - 1);
					strLine1.add((byte) prntTxt.charAt(i));
				} else {
					int nextCode = (int) prntTxt.charAt(i + 1);
					switch (nextCode) {
					case 3763: //สระอำ
						switch (code) {
						case 3784: //ไม้เอก
							strLine2.add((byte) 212); // สระอำ + ไม้เอก
							break;
						case 3785: //ไม้โท
							strLine2.add((byte) 213); // สระอำ + ไม้โท
							break;
//						case 3786: //ไม้ตรี
//							strLine1 = strLine1.substring(0, strLine1.length() - 1) + Character.toString((char) 3786) + " ";
//							break;
//						case 3787: //ไม้จัตวา
//							strLine1 = strLine1.substring(0, strLine1.length() - 1) + Character.toString((char) 3787) + " ";
//							break;
						}
						//strLine2 += Character.toString((char) 3763);
						strLine3.add((byte) 0); // 32 = space
						i += 1;
						break;
					default:
						strLine1 = strLine1.subList(0, strLine1.size() - 1);
						strLine1.add((byte) prntTxt.charAt(i));
						break;
					}
				}
				break;
			case 3768: //สระอุ
			case 3769: //สระอู
				strLine3 = strLine3.subList(0, strLine3.size() - 1);
				strLine3.add((byte) prntTxt.charAt(i));
				break;
			default:
				strLine1.add((byte) 0);
				strLine3.add((byte) 0);

				// เปลี่ยนพยัญชนะลาวจาก unicode เป็น ascii 
				switch(code){
				case 3713: // ก
					strLine2.add((byte) 161);
					break;
				case 3714: // ข
					strLine2.add((byte) 162);
					break;
				case 3716: // ค
					strLine2.add((byte) 163);
					break;
				case 3719: // ง
					strLine2.add((byte) 164);
					break;
				case 3720: // จ
					strLine2.add((byte) 165);
					break;
				case 3722: // ซ
					strLine2.add((byte) 167);
					break;
				case 3725: // ย
					strLine2.add((byte) 168);
					break;
				case 3732: // ด
					strLine2.add((byte) 169);
					break;
				case 3733: // ต
					strLine2.add((byte) 170);
					break;
				case 3734: // ถ
					strLine2.add((byte) 171);
					break;
				case 3735: // ท
					strLine2.add((byte) 234);
					break;
				case 3737: // น
					strLine2.add((byte) 173);
					break;
				case 3738: // บ
					strLine2.add((byte) 174);
					break;
				case 3739: // ป
					strLine2.add((byte) 175);
					break;
				case 3740: // ผ
					strLine2.add((byte) 176);
					break;
				case 3741: // ฝ
					strLine2.add((byte) 177);
					break;
				case 3742: // พ
					strLine2.add((byte) 178);
					break;
				case 3743: // ฟ
					strLine2.add((byte) 179);
					break;
				case 3745: // ม
					strLine2.add((byte) 180);
					break;
				case 3746: // ย ยา
					strLine2.add((byte) 181);
					break;
				case 3747: // ร รถ
					strLine2.add((byte) 235);
					break;
				case 3749: // ล ลีง
					strLine2.add((byte) 236);
					break;
				case 3751: // ว วี
					strLine2.add((byte) 184);
					break;
				case 3754: // ส
					strLine2.add((byte) 166);
					break;
				case 3755: // ห ห่าน
					strLine2.add((byte) 185);
					break;
				case 3757: // อ โอ
					strLine2.add((byte) 186);
					break;
				case 3758: // ฮ เฮือน
					strLine2.add((byte) 187);
					break;
				case 3759: // เครื่องหมายละ
					strLine2.add((byte) 198);
					break;
				case 3760: // ะ
					strLine2.add((byte) 189);
					break;
				case 3762: // า
					strLine2.add((byte) 190);
					break;
				case 3773: // ตัวเฟื่อง
					strLine2.add((byte) 188);
					break;
				case 3776: // เ
					strLine2.add((byte) 192);
					break;
				case 3777: // แ
					strLine2.add((byte) 193);
					break;
				case 3778: // โ
					strLine2.add((byte) 194);
					break;
				case 3779: // ใ
					strLine2.add((byte) 195);
					break;
				case 3780: // ไ
					strLine2.add((byte) 196);
					break;
				case 3782: // ๆ
					strLine2.add((byte) 197);
					break;
				case 3792: // 0
					strLine2.add((byte) 224);
					break;
				case 3793: // 1
					strLine2.add((byte) 225);
					break;
				case 3794: // 2
					strLine2.add((byte) 226);
					break;
				case 3795: // 3
					strLine2.add((byte) 227);
					break;
				case 3796: // 4
					strLine2.add((byte) 228);
					break;
				case 3797: // 5
					strLine2.add((byte) 229);
					break;
				case 3798: // 6
					strLine2.add((byte) 230);
					break;
				case 3799: // 7
					strLine2.add((byte) 231);
					break;
				case 3800: // 8
					strLine2.add((byte) 232);
					break;
				case 3801: // 9
					strLine2.add((byte) 233);
					break;
				case 3804: // หน
					strLine2.add((byte) 206);
					break;
				case 3805: // หม
					strLine2.add((byte) 207);
					break;
				default:
					strLine2.add((byte) prntTxt.charAt(i));
					if(code == 10) // new line
						strLine1.add((byte) code);
					break;
				}
				break;
			}
		}
		if(strLine1.size() > 0){
			byte[] lines1 = new byte[strLine1.size()]; 
			for(int l1 = 0; l1 < strLine1.size(); l1++){
				lines1[l1] = strLine1.get(l1);
			}
			resultText.setLine1(lines1);
		}
		if(strLine2.size() > 0){
			byte[] lines2 = new byte[strLine2.size()]; 
			for(int l2 = 0; l2 < strLine2.size(); l2++){
				lines2[l2] = strLine2.get(l2);
			}
			resultText.setLine2(lines2);
		}
		if(strLine3.size() > 0){
			byte[] lines3 = new byte[strLine3.size()]; 
			for(int l3 = 0; l3 < strLine3.size(); l3++){
				lines3[l3] = strLine3.get(l3);
			}
			resultText.setLine3(lines3);
		}
		return resultText;
	}
	
	/**
	 * @param unicode prntTxt
	 * @return OPOSThaiText obj
	 */
	public static ThreeLevelPrint parsingThaiLevel(String prntTxt) {
		String strLine1 = "";
		String strLine2 = "";
		String strLine3 = "";
		int i = 0;
		int code = 0;
		int nextCode = 0;
		String strChar = "";
		ThreeLevelPrint resultText = new ThreeLevelPrint();
		for (; i < prntTxt.length(); i++) {
			code = prntTxt.charAt(i);
			switch (code) {
			case 3633: //ไม้หันอากาศ
			case 3636: //สระอิ
			case 3637: //สระอี
			case 3638: //สระอึ
			case 3639: //สระอื
				//ไม้หันอากาศ และ สระด้านบน เช่น อิ อี
				//Check if letter next to it is วรรณยุกต์
				if (i == prntTxt.length() - 1) {
					// This is the last letter
					strChar = Character.toString(prntTxt.charAt(i));
				} else {
					nextCode = (int) prntTxt.charAt(i + 1);
					switch (nextCode) {// Comboine current and the next character into new one
					case 3656: //ไม้เอก
					case 3657: //ไม้โท
					case 3658: //ไม้ตรี
					case 3659: //ไม้จัตวา
					case 3660: //การันต์
					case 3661: //นิคหิต
						//วรรณยุกต์ และการันต์
						switch (nextCode) {
						case 3656:
							switch (code) {
							case 3633: //ไม้หันอากาศ
								nextCode = 3633;
								break;
							case 3636: //อิ
								nextCode = 144;
								break;
							case 3637: //อี
								nextCode = 132;
								break;
							case 3638: //อึ
								nextCode = 136;
								break;
							case 3639: //อื
								nextCode = 140;
								break;
							}
							break;
						case 3657: //ไม้โท
							switch (code) {
							case 3633: //ไม้หันอากาศ
								nextCode = 129;
								break;
							case 3636: //อิ
								nextCode = 145;
								break;
							case 3637: //อี
								nextCode = 133;
								break;
							case 3638: //อึ
								nextCode = 137;
								break;
							case 3639: //อื
								nextCode = 141;
								break;
							}
							break;
						case 3658: //ไม้ตรี
							switch (code) {
							case 3633: //ไม้หันอากาศ
								nextCode = 130;
								break;
							case 3636: //อิ
								nextCode = 146;
								break;
							case 3637: //อี
								nextCode = 134;
								break;
							case 3638: //อึ
								nextCode = 138;
								break;
							case 3639: //อื
								nextCode = 142;
								break;
							}
							break;
						case 3659: //ไมจัตวา
							switch (code) {
							case 3633: //ไม้หันอากาศ
								nextCode = 131;
								break;
							case 3636: //อิ
								nextCode = 147;
								break;
							case 3637: //อี
								nextCode = 135;
								break;
							case 3638: //อึ
								nextCode = 139;
								break;
							case 3639: //อื
								nextCode = 143;
								break;
							}
							break;
						case 3660: //การันต์
							switch (code) {
							case 3636: //อิ
								nextCode = 148;
								break;
							}
							break;
						}
						strChar = Character.toString((char) nextCode);
						i += 1;
						break;
					default:
						strChar = Character.toString(prntTxt.charAt(i));
						break;
					}
				}
				strLine1 = strLine1.substring(0, strLine1.length() - 1) + strChar;
				break;
			case 3655: //ไม้ไต่คู้
			case 3656:
			case 3657:
			case 3658:
			case 3659:
			case 3660:
			case 3661:
				//วรรณยุกต์
				// Check if letter next to it is วรรณยุกต์
				if (i == prntTxt.length() - 1) { // This is the last letter
					strLine1 = strLine1.substring(0, strLine1.length() - 1) + Character.toString(prntTxt.charAt(i));
				} else {
					nextCode = (int) prntTxt.charAt(i + 1);
					switch (nextCode) {
					case 3635: //สระอำ
						switch (code) {
						case 3656: //ไม้เอก
							strLine1 = strLine1.substring(0, strLine1.length() - 1) + Character.toString((char) 3656) + " ";
							break;
						case 3657: //ไม้โท
							strLine1 = strLine1.substring(0, strLine1.length() - 1) + Character.toString((char) 3657) + " ";
							break;
						case 3658: //ไม้ตรี
							strLine1 = strLine1.substring(0, strLine1.length() - 1) + Character.toString((char) 3658) + " ";
							break;
						case 3659: //ไม้จัตวา
							strLine1 = strLine1.substring(0, strLine1.length() - 1) + Character.toString((char) 3659) + " ";
							break;
						}
						strLine2 += Character.toString((char) 3635);
						strLine3 += " ";
						i += 1;
						break;
					default:
						strLine1 = strLine1.substring(0, strLine1.length() - 1) + Character.toString(prntTxt.charAt(i));
						break;
					}
				}
				break;
			case 3640: //สระอุ
			case 3641: //สระอู
				strLine3 = strLine3.substring(0, strLine3.length() - 1) + Character.toString(prntTxt.charAt(i));
				break;
			default:
				strLine1 += " ";
				strLine2 += Character.toString(prntTxt.charAt(i));
				strLine3 += " ";
				break;
			}
		}
		resultText.setLine1(TextUtils.isEmpty(strLine1.trim()) ? "" : strLine1);
		resultText.setLine2(TextUtils.isEmpty(strLine2.trim()) ? "" : strLine2);
		resultText.setLine3(TextUtils.isEmpty(strLine3.trim()) ? "" : strLine3);
		return resultText;
	}

	public static class ThreeLevelPrint
	{
	    private String line1;
	    private String line2;
	    private String line3;
		public String getLine1() {
			return line1;
		}
		public void setLine1(String line1) {
			this.line1 = line1;
		}
		public String getLine2() {
			return line2;
		}
		public void setLine2(String line2) {
			this.line2 = line2;
		}
		public String getLine3() {
			return line3;
		}
		public void setLine3(String line3) {
			this.line3 = line3;
		}
	}
}
