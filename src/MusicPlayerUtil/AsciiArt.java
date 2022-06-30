package MusicPlayerUtil;
import MusicPlayerUtil.*;

public class AsciiArt {
	MusicPlayerCommon mpc = new MusicPlayerCommon();
		
	public void panelArtU() {
		mpc.manyLine(50);
		System.out.println("		┌──────────────────────────────────────────┐");
		System.out.println("		|                                          |");
	}
	public void panelArtD() {
		System.out.println("		|                                          |");
		System.out.println("		└──────────────────────────────────────────┘");
		System.out.println("				     ᕱ ᕱ  ||\n				( ･ω･ ||\n 				/   つΦ");
//		System.out.println("                                                         ᕱ ᕱ  ||");
//		System.out.println("                                                         ( ･ω･ ||");	 	
//		System.out.println("                               /   つΦ");
	}

	public void meunArt() {
		System.out.println("		 ♡ ∩_∩");
		System.out.println("		（„• ֊ •„)♡");
//		System.out.println("────────────────────UU──────────────────────────────────────────────────────────────────────────────────");
		System.out.println("====================UU==================================================================================");
//		System.out.println("========================================================================================================");
//		System.out.println("|      1.로그인    2.회원가입    3.게스트로 입장    0.종료        |");
//		System.out.println("|                                                                 |");
//		System.out.println("└─────────────────────────────────────────────────────────────────┘");
	}

	public void systemArt() {
		System.out.println("┌─────────────────────────────────────────────────────────────────────┐");
		System.out.println("|　MENU!!!   　                                          [－][口][×]  |");
		System.out.println("|￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣ |");
		System.out.println("|                                                                     |");
		System.out.println("|                           1.Top 100 리스트                          |");
		System.out.println("|                                                                     |");
		System.out.println("|                           2.내 재생목록                             |");
		System.out.println("|                                                                     |");
		System.out.println("|                           3.즐겨찾기                                |");
		System.out.println("|                                                                     |");
		System.out.println("|                           4.뮤직컨트롤러                            |");
		System.out.println("|                                                                     |");
		System.out.println("|                           5.환경설정                                |");
		System.out.println("|                                                                     |");
		System.out.println("|                           0.로그아웃                                |");
		System.out.println("|                                                                     |");
		System.out.println("└─────────────────────────────────────────────────────────────────────┘");
	}

	public void titleMenu() {
		System.out.println("╭━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━╮");
		System.out.println("┃                                                                    ┃");
		System.out.println("┃                    ╒◖═══════════════════════◗╕                     ┃");
		System.out.println("┃                       J A V A     M U S I C                        ┃");
		System.out.println("┃                    ╘◖═══════════════════════◗╛                     ┃");
		System.out.println("┃                                                                    ┃");
		System.out.println("┃                                                                    ┃");
		System.out.println("┃                [1 조     E L D E R   S C R I P T S]                ┃");
		System.out.println("┃                                                                    ┃");
		System.out.println("┃                                                                    ┃");
		System.out.println("╰━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━╯");
	}
	public void menu() {
		System.out.println("┌────────────────────────────────────────────────────────────────────┐"); 
		System.out.println("┃   1.로그인   ┃   2.회원가입   ┃   3.게스트로 입장    ┃   0.종료    ┃");
		System.out.println("└────────────────────────────────────────────────────────────────────┘");
	}
	
	public void prev() {
		System.out.println("┌──────────────────────────────────────────────────────────┐"); 
		System.out.println("┃  1.이전  ┃  3.선택  ┃  4.검색  ┃  5.전체재생  ┃  0.뒤로  ┃");
		System.out.println("└──────────────────────────────────────────────────────────┘");
	}
	public void next() {
		System.out.println("┌──────────────────────────────────────────────────────────┐"); 
		System.out.println("┃  2.다음  ┃  3.선택  ┃  4.검색  ┃  5.전체재생  ┃  0.뒤로  ┃");
		System.out.println("└──────────────────────────────────────────────────────────┘");
	}
	public void all() {
		System.out.println("┌─────────────────────────────────────────────────────────────────────┐"); 
		System.out.println("┃  1.이전  ┃  2.다음  ┃  3.선택  ┃  4.검색  ┃  5.전체재생  ┃  0.뒤로  ┃");
		System.out.println("└─────────────────────────────────────────────────────────────────────┘");
	}
	public void none() {
		System.out.println("┌───────────────────────────────────────────────┐"); 
		System.out.println("┃  3.선택  ┃  4.검색  ┃  5.전체재생  ┃  0.뒤로  ┃");
		System.out.println("└───────────────────────────────────────────────┘");
	}
	
	public void playList_prev() {
		System.out.println("┌────────────────────────────────────────────────────────────────────────┐"); 
		System.out.println("┃  1.이전  ┃  3.선택  ┃  4.검색  ┃  5.전체재생  ┃  6.곡 삭제  ┃  0.뒤로  ┃");
		System.out.println("└────────────────────────────────────────────────────────────────────────┘");
	}
	public void playList_next() {
		System.out.println("┌────────────────────────────────────────────────────────────────────────┐"); 
		System.out.println("┃  2.다음  ┃  3.선택  ┃  4.검색  ┃  5.전체재생  ┃  6.곡 삭제  ┃  0.뒤로  ┃");
		System.out.println("└────────────────────────────────────────────────────────────────────────┘");
	}
	public void playList_all() {
		System.out.println("┌───────────────────────────────────────────────────────────────────────────────────┐"); 
		System.out.println("┃  1.이전  ┃  2.다음  ┃  3.선택  ┃  4.검색  ┃  5.전체재생  ┃  6.곡 삭제  ┃  0.뒤로  ┃");
		System.out.println("└───────────────────────────────────────────────────────────────────────────────────┘");
	}
	public void playList_none() {
		System.out.println("┌─────────────────────────────────────────────────────────────┐"); 
		System.out.println("┃  3.선택  ┃  4.검색  ┃  5.전체재생  ┃  6.곡 삭제  ┃  0.뒤로  ┃");
		System.out.println("└─────────────────────────────────────────────────────────────┘");
	}
	
	public void prevString() {
		System.out.println("┌───────────────────────────────────────────────────────────────────────────────────┐"); 
		System.out.println("┃  1.이전  ┃  3.선택  ┃  4.검색  ┃  5.재생목록 삭제  ┃  6.재생목록 생성  ┃  0.뒤로  ┃");
		System.out.println("└───────────────────────────────────────────────────────────────────────────────────┘");
	}
	public void nextString() {
		System.out.println("┌───────────────────────────────────────────────────────────────────────────────────┐"); 
		System.out.println("┃  2.다음  ┃  3.선택  ┃  4.검색  ┃  5.재생목록 삭제  ┃  6.재생목록 생성  ┃  0.뒤로  ┃");
		System.out.println("└───────────────────────────────────────────────────────────────────────────────────┘");
	}
	public void allString() {
		System.out.println("┌──────────────────────────────────────────────────────────────────────────────────────────────┐"); 
		System.out.println("┃  1.이전  ┃  2.다음  ┃  3.선택  ┃  4.검색  ┃  5.재생목록 삭제  ┃  6.재생목록 생성  ┃  0.뒤로  ┃");
		System.out.println("└──────────────────────────────────────────────────────────────────────────────────────────────┘");
	}
	public void noneString() {
		System.out.println("┌────────────────────────────────────────────────────────────────────────┐"); 
		System.out.println("┃  3.선택  ┃  4.검색  ┃  5.재생목록 삭제  ┃  6.재생목록 생성  ┃  0.뒤로  ┃");
		System.out.println("└────────────────────────────────────────────────────────────────────────┘");
	}
	
	
	public void byebye(){
		System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
		System.out.println("\r\n\n\n\n\n\n\n\n\n\n\n\n" + 
				".      　。　　　　•　    　ﾟ　　。。　　　　•　\r\n" + 
				"　　.　　　.　　　  　　.　　　　　。　　   。　.\r\n" + 
				" 　.　　      。　        ඞ   。　    .    •\r\n" + 
				" •.        .... G O O D       B Y E.....\r\n" + 
				"　 　　。　　　　　　ﾟ　　　.　　　　　    .\r\n" + 
				",　　　　.　 .　　       .               。\r\n" + 
				"\r\n" + 
				"");
	}
	
	public void consoleUp() {
		System.out.println("-----(~˘▾˘)~♫•*¨*•.¸¸♪♪----------------------------------------");
	}
	
	public void consoleDown() {
		System.out.println("----------------------------------------♬·¯·♫♪♫¸♬♩~~♪----------");
	}
}

