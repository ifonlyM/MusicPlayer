package UserService;

import static MusicPlayerUtil.MusicPlayerCommon.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import MusicPlayerUtil.AsciiArt;
import UserVo.User;

/**
 * 
 * @author 김기락 210802
 * 유저서비스의 메서드를 구현하는 클래스입니다.
 * 회원가입, 유저 데이터 저장, 불러오기 기능 구현
 * 
 * 210805 User user 삭제, 시작 시 유저데이터 확인
 */


public class UserServiceImpl implements UserService {
	private AsciiArt as = new AsciiArt();
	private List<User> users = new ArrayList<User>();
	private int errorState = NOTHING;				//에러 상태를 통해 알맞는 에러 메시지를 출력합니다.
	
	/**
	 * 에러메시지 출력을 위한 조건 비교에 사용되는 상수입니다.
	 * @author 문현석
	 * 2021-08-09
	 */
	static final int NOTHING 				= 0;	//에러 메세지 타입 기본값입니다.
	static final int INVALID_ID_PATTERN 	= 1;	//잘못된 아이디 패턴일때
	static final int NO_ID 					= 2;	//데이터 베이스에 없는 아이디 일때
	static final int INVALID_PW_PATTERN 	= 3;	//잘못된 비밀번호 패턴일때
	static final int WRONG_PW 				= 4;	//틀린 비밀번호 일때 
	static final int LOGIN_SUCCEED			= 5;	//로그인에 성공했을때
	static final int LOGIN_EXIT 			= 6;	//로그인을 취소하고 나갔을때
	static final int ADD_ID_PATTERN			= 7;	//아이디 패턴 통과
	static final int ALREADY_EXISTS			= 8;	//아이디 이미 존재
	static final int ADD_USER				= 9;	//회원가입 성공
	static final int GUEST_LOGIN			=10;	//게스트 로그인
	
	
	{
		manyLines(60);
		as.titleMenu();
		loadDb();
//		users.forEach(System.out::println);
			
	}
	// 파일 저장
	public void saveDb() {			
		try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("UserDB.ser"))) {
			oos.writeObject(users);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 파일 불러오기
	public void loadDb() {			
		try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream("UserDB.ser"))) {
			users = (List<User>) ois.readObject();
		} catch (Exception e) {															//파일을 못찾을 경우 더미데이터 생성
			users.add(new User("Test1", "asdfasdf1"));
			users.add(new User("Test2", "asdfasdf2"));
			users.add(new User("Test3", "asdfasdf3"));
		}
	}
		
	// 입력된 아이디를 통한 해당 리스트 호출
	public User userListID(String inputId) {
		for(int i = 0; i < users.size(); i++) {												// 유저목록의 id들을 inputId와 비교 
			if (inputId.equals(users.get(i).getId())) {										// 아이디 비교 한 결과 존재하면 로그인으로 진입
				return users.get(i);
			}
		}
		return null;
	}
	
	// 입력된 패스워드를 통한 해당 리스트 호출
	public User userListPW(String inputPw) {
		for(int i = 0; i < users.size(); i++) {												// 유저목록의 id들을 inputId와 비교 
			if (inputPw.equals(users.get(i).getPw())) {										// 아이디 비교 한 결과 존재하면 로그인으로 진입
				return users.get(i);
			}
		}
		return null;
	}
	
	// 게스트유저 생성
	public User guestUser() {
		errorState = GUEST_LOGIN;
		return null;
	} 
	
	
	//입력된 아이디를 통해 해당 리스트의 아이디를 호출
	public String userID(String inputId) {
		for(int i = 0; i < users.size(); i++) {												// 유저목록의 id들을 inputId와 비교 
			if (inputId.equals(users.get(i).getId())) {										// 아이디 비교 한 결과 존재하면 로그인으로 진입
				return users.get(i).getId();
			}
		}
		return null;
	}
	
	////입력된 패스워드를 통해 해당 리스트의 패스워드를 호출
	public String userPW(String inputPw) {
		for(int i = 0; i < users.size(); i++) {												// 유저목록의 id들을 inputId와 비교 
			if (inputPw.equals(users.get(i).getPw())) {										// 아이디 비교 한 결과 존재하면 로그인으로 진입
				return users.get(i).getPw();
			}
		}
		return null;
	}
	
	// 회원가입 
	public void addUser() {
		String inputId = ""; 						
		User userID = null; 
		
		while(true) {
			manyLine(60);
			errorMessage();
			System.out.println("     ┌──────────────────────────────────────────────────────────┐"); 
			System.out.println("     ┃              아이디를 입력해 주세요.  ( 종료 > 0 )       ┃");
			System.out.println("     └──────────────────────────────────────────────────────────┘");
			inputId = nextLine("	>>>> ");
			userID = userListID(inputId);										
			manyLine(60);
			if(inputId.equals("0")) {
				errorState = LOGIN_EXIT;
				return;
			}
			else if(inputId.contains(" ") || inputId.equals("")) {
				errorState = INVALID_ID_PATTERN;
				continue;
			}
			else if(userID == null) {
				errorState = ADD_ID_PATTERN;
				break;
			}
			else if(userID.getId().equals(inputId)) {
				errorState =  ALREADY_EXISTS;
				continue;
			}
			else {
				break;
			}
		}
		
		String inputPw = "";
		String inputPw2 =""; 
		//비밀 번호 검사 반복문
		while(true) {
			manyLine(60);
			errorMessage();
			System.out.println("     ┌──────────────────────────────────────────────────────────┐"); 
			System.out.println("     ┃            비밀번호를 입력해 주세요.  ( 종료 > 0 )       ┃");
			System.out.println("     └──────────────────────────────────────────────────────────┘");
			inputPw = nextLine("	>>>> ");

			if(inputPw.equals("0")) {
				errorState = LOGIN_EXIT;
				return;
			}
			else if(inputPw.contains(" ") || inputPw.equals("")) {
				errorState = INVALID_PW_PATTERN;
				continue;
			} 
			
			manyLine(60);
			System.out.println("     ┌──────────────────────────────────────────────────────────┐"); 
			System.out.println("     ┃      한 번 더 비밀번호를 입력해 주세요.  ( 종료 > 0 )    ┃");
			System.out.println("     └──────────────────────────────────────────────────────────┘");
			inputPw2 = nextLine("	>>>> ");
			
			manyLine(60);
			if(inputPw2.equals("0")) {
				errorState = LOGIN_EXIT;
				return;
			}
			else if (!inputPw.equals(inputPw2)) {										// 입력된 패스워드 비교 후 다를 시 처음으로 회귀
				errorState = WRONG_PW;
				continue;
			} 
			else {
				errorState = ADD_USER;
				users.add(new User(inputId, inputPw));
				saveDb();
				break;
			}
		}
	}

	// 로그인 서비스 
	public User login() {
		String inputId = ""; 						
		User userID = null; 												

		// 아이디 검사 반복문
		while(true) {
			manyLine(60);
			errorMessage();
			System.out.println("     ┌──────────────────────────────────────────────────────────┐"); 
			System.out.println("     ┃              아이디를 입력해 주세요.  ( 종료 > 0 )       ┃");
			System.out.println("     └──────────────────────────────────────────────────────────┘");
			inputId = nextLine("	>>>> ");										// 유저 생성할 아이디 입력
			userID = userListID(inputId);											// 입력한 아이디의 값과 동일한 리스트를 찾아옴
			manyLine(60);
			if(inputId.equals("0")) {
				errorState = LOGIN_EXIT;
				return null;
			}
			else if(inputId.contains(" ") || inputId.equals("")) {
				errorState = INVALID_ID_PATTERN;
				continue;
			}
			else if(userID == null) {
				errorState = NO_ID;
				continue;
			}
			
			// 아이디 검사를 통과한 경우 아이디 검사 반복문을 빠져나갑니다.
			else {
				break;
			}
		}
		String inputPw = "";
		
		//비밀 번호 검사 반복문
		while(true) {
			manyLine(60);
			errorMessage();
			System.out.println("     ┌──────────────────────────────────────────────────────────┐"); 
			System.out.println("     ┃            비밀번호를 입력해 주세요.  ( 종료 > 0 )       ┃");
			System.out.println("     └──────────────────────────────────────────────────────────┘");
			inputPw = nextLine("	>>>> ");
			manyLine(60);
			if(inputPw.equals("0")) {
				errorState = LOGIN_EXIT;
				return null;
			}
			else if (inputPw.contains(" ") || inputPw.equals("")){
				errorState = INVALID_PW_PATTERN;
				continue;
			} 
			else if (!userID.getPw().equals(inputPw)) {	// 입력된 패스워드 비교 후 다를 시 처음으로 회귀
				errorState = WRONG_PW;
				continue;
			} 
			else {
				errorState = LOGIN_SUCCEED;
				return userID;
			}
		}
	}
	public void manyLine(int cnt) {
		for(int i = 0; i < cnt; i++) {
			System.out.println();
		}
	}
	public void errorMessage() {
		
		switch (errorState) {
		case INVALID_ID_PATTERN:
			as.panelArtU();
			System.out.println("		|         잘못된 아이디 패턴입니다.        |");
			System.out.println("		|             다시 시도해주세요.           |");
			as.panelArtD();
			break;
		case NO_ID:
			as.panelArtU();
			System.out.println("		|    입력한 아이디가 존재하지 않습니다.    |");
			System.out.println("		|             다시 확인해주세요.           |");
			as.panelArtD();
			break;
		case INVALID_PW_PATTERN:
			as.panelArtU();
			System.out.println("		|        잘못된 비밀번호 패턴입니다.       |");
			as.panelArtD();
			break;
		case WRONG_PW:
			as.panelArtU();
			System.out.println("		|            비밀번호가 다릅니다.          |");
			System.out.println("		|             다시 확인해주세요.           |");
			as.panelArtD();
			break;
		case LOGIN_EXIT:
			as.panelArtU();
			System.out.println("		|           로그인을 취소했습니다.         |");
			as.panelArtD();
			break;
		case ADD_ID_PATTERN:
			as.panelArtU();
			System.out.println("		|     입력한 아이디는 사용 가능합니다.     |");
			as.panelArtD();
			break;
		case ALREADY_EXISTS:
			as.panelArtU();
			System.out.println("		|        이미 존재하는 아이디입니다.       |");
			as.panelArtD();
			break;
		case ADD_USER:
			as.panelArtU();
			System.out.println("		|        회원가입이 완료 되었습니다.       |");
			as.panelArtD();
			break;
		default:
			break;
		}
		
		errorState = NOTHING;
	}

	public void toMainMessage() {
		switch (errorState) {
		case LOGIN_SUCCEED:
			as.panelArtU();
			System.out.println("		|           로그인에 성공했습니다.         |");
			as.panelArtD();
			errorState = INIT;
			break;
		case GUEST_LOGIN:
			System.out.println("     ┌──────────────────────────────────────────────────────────┐"); 
			System.out.println("     ┃                   게스트로 입장하셨습니다.               ┃");
			System.out.println("     └──────────────────────────────────────────────────────────┘");
			break;
		}
	}
}
