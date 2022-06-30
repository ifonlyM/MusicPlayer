package MusicPlayerEx;

/**
 * @author 성지혜
 * 2021-07-30 MusicPlayerEx 생성 
 * 
 */

import static MusicPlayerUtil.MusicPlayerCommon.*;
import MusicPlayerService.MusicPlayerService;
import MusicPlayerService.MusicPlayerServiceImpl;
import MusicPlayerUtil.AsciiArt;

public class MusicPlayerEx {
	public static void main(String[] args) {
		
		MusicPlayerService mps = new MusicPlayerServiceImpl();
		
		while(true) {
			execute(mps);
		}
	}
	
	static void execute(MusicPlayerService mps) {
		// 프로그램 시작시 로그인 합니다.
		mps.login();
		AsciiArt as = new AsciiArt();
		while (true) {
			
			manyLines(60);
			mps.fromUserMessage();
			mps.message();
			as.systemArt();
			int input = nextIntFromTo("	>>>> ", 0, 5);

			switch (input) {
			case 1:
				mps.allList();
				break;
			case 2:
				mps.myList();
				break;
			case 3:
				mps.favoritList();
				break;
			case 4:
				mps.musicController();
				break;
			case 5:
				mps.settings();
				break;
			case 0:
				//로그아웃, 다시 login()메서드부터 실행되어야함.
				mps.logout();
				return;
			}
		}
	}
}
