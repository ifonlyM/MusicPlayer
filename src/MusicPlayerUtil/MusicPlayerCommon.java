package MusicPlayerUtil;

import java.util.Scanner;

public class MusicPlayerCommon {

	public static final String TITLE = "title";
	public static final String ARTIST = "artist";
	public static final String ALBUM = "album";
	public static final String GENRE = "genre";
	public static final String LENGTH = "length";
	public static final String LENGTH_STR = "length_str"; 
	
	//MusicController의 재생상태
	public static final int INIT = 0;
	public static final int STARTED = 1;
	public static final int SUSPENDED = 2;
	public static final int STOPPED = 3;
	public static final int FINISHED = 4;
	
	//MusciController playSequence 상태
	public static final int NOTHING = 0;
	public static final int PREV = 1;
	public static final int NEXT = 2;
	
	public static Scanner scanner = new Scanner(System.in);
	
	public static int nextInt(String text) {
		while(true) {
			try {
				return Integer.parseInt(nextLine(text));
			}
			catch(Exception e) {
				System.out.println("숫자로 다시 입력을 해주세요.");
			}
		}
	}
	
	public static String nextLine(String text) {
		System.out.print(text);
		return scanner.nextLine();
	}
	
	//문자열을 입력받았을때 지정한 숫자 범위내에서만 int타입으로 반환
	public static int nextIntFromTo(String text, int from, int to) {
		while(true) {
			System.out.print(text);
			String str = scanner.nextLine();
			int num;
			
			try {
				num = Integer.parseInt(str);
			}
			catch(Exception e) {
				System.out.println("숫자로 다시 입력해주세요.");
				continue;
			}
			
			if(from <= num && num <= to) return num;
			else System.out.println("정확한 메뉴번호를 입력 해주세요. 입력된 번호-> " + num );
		}
	}
	
	public static int nextIntFromTo(String text, int from, int to, boolean isOneChar) {
		while(true) {
			System.out.print(text);
			String str = scanner.nextLine();
			str =  String.valueOf(str.charAt(str.length() - 1));
			int num;
			
			try {
				num = Integer.parseInt(str);
			}
			catch(Exception e) {
				System.out.println("숫자로 다시 입력해주세요.");
				continue;
			}
			
			if(from <= num && num <= to) return num;
			else System.out.println("정확한 메뉴번호를 입력 해주세요. 입력된 번호-> " + num );
		}
	}
	
	//문자열을 입력 받았을때 한글범위 내에서만 반환
	public static String nextLineKorean(String text) {
		while(true) {
			System.out.print(text);
			boolean isKorean = true;
			String str = scanner.nextLine();
			for(int i = 0; i < str.length(); i++) {
				char c = str.charAt(i);
				if('가' <= c && c <= '힣');
				else isKorean = false;
			}
			if(isKorean)return str;
			else System.out.print("한글로 다시 ");
		}
	}

	/**
	 * @author 김기락
	 * 줄바꿈  
	 */
	public void manyLine(int cnt) {
		for(int i = 0; i < cnt; i++) {
			System.out.println();
		}
	}
	
	public static void manyLines(int cnt) {
		for(int i = 0; i < cnt; i++) {
			System.out.println();
		}
	}
	
	public static void clearInputBuffer(int cnt) {
		for(int i = 0; i < cnt; i++) {
			System.out.print("\b");
		}
	}
	
	//파라메터의 문자열길이를 limit를 넘어가면 limit까지 자르고 ... 문자열 추가
	public static String getShortString(String str, int limit) {
		int koreanCnt = 0;
		for(int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if('가' <= c && c <= '힣') {
				koreanCnt++;
			}
		}
		
		if(str.length() + koreanCnt > limit) {
			double kStrLimit = (limit /(double) (str.length() + koreanCnt));
//			System.out.println(kStrLimit);
			
			return str.substring(0, (int) (str.length() * kStrLimit)) + "...";
		}
		else {
			return str;
		}
	}
	
	/**
	 * 한글과 영어의 문자크기 차이로인한 길이차이를 보정해줍니다.
	 * getSameLengthString 의 약자입니다.
	 * @param str
	 * @param max
	 * @return
	 */
	public static String getSlString(String str, int limit, int slMax) {
		
		String shortStr = getShortString(str, limit);
		
		int koreanCnt = 0;
		for(int i = 0; i < shortStr.length(); i++) {
			char c = shortStr.charAt(i);
			if('가' <= c && c <= '힣') {
				koreanCnt++;
			}
		}
		
		while(shortStr.length() < (slMax - koreanCnt)) {
			shortStr = shortStr + " ";
		}
		
		return shortStr;
	}
	
	/**
	 * 아스키 아트용 메세지를 만들때 배경텍스트에 맞게 내용텍스트를 합쳐줌
	 * @param bgStr
	 * @param str
	 * @return
	 */
	public static String getBackgroundMacthString(String bgStr, String str) {
		int bgLength = bgStr.length();
		int strLength = str.length();
//		System.out.println("bg문자개수 : " + bgLength);
//		System.out.println("str문자개수 : " + strLength);
		
		String frontBgStr = bgStr.substring(0, bgLength / 2);
		String backBgStr = bgStr.substring(bgLength / 2, bgLength);
//		System.out.println("frontBgStr :" + frontBgStr);
//		System.out.println("backBgStr  :" + backBgStr);
//		System.out.println("str 반으로 : " + strLength / 2.f);
		
		int koreanCnt = 0;
		for(int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if('가' <= c && c <= '힣') {
				koreanCnt++;
			}
		}
//		System.out.println("한글 개수 : " + koreanCnt);
		int frontLimit = frontBgStr.length() - ( Math.round(strLength / 2.f) + (int) (koreanCnt / 2.f));
		int backStart = ((int) (strLength / 2.f) + Math.round(koreanCnt / 2.f));
		frontBgStr = frontBgStr.substring(0,  frontLimit);
		backBgStr = backBgStr.substring( backStart, backBgStr.length());
//		System.out.println("프론트Bg :" + frontBgStr);
//		System.out.println("백Bg :" + backBgStr);
		
		return frontBgStr + str + backBgStr;
	}
	
	// 테스트 메인
	public static void main(String[] args) {
		String bgStr ="|                                          |";
		String str = "<Stranger Things asdfsasdf(Feat. OneRepublic)>곡을";
		AsciiArt asc = new AsciiArt();
		asc.panelArtU();
		System.out.println("		" + getBackgroundMacthString(bgStr, str));
		asc.panelArtD();
//		System.out.println(getShortString("위아더나잇(We Are The Night)", 20));
	}
}
