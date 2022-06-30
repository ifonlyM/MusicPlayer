package MusicPlayerService;

/**
 * @author 김문수
 * 2021-07-28
 * Music매서드 기능 정의 인터페이스
 */
public interface MusicPlayerService {
	
	void allList();
	
	void myList();
	
	void favoritList();
	
	void musicController();
	
	/**
	 * 로그인 메서드입니다
	 * @author 문현석
	 * 2021-08-09
	 */
	void login();

	/**
	 * 로그아웃 메서드입니다
	 * @author 문현석
	 * 2021-08-09
	 */
	void logout();
	
	/**
	 * 에러메세지를 출력하는 메서드입니다.
	 * @author 문현석
	 * 2021-08-09
	 */
	void message();
	
	
	void settings();
	
	void fromUserMessage();
}
