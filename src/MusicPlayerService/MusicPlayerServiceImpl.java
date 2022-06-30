package MusicPlayerService;

/**
 * @author 문현석
 * 2021-07-30
 * MusicPlayerUtill -> MusicPlayerUtil로 변경
 * MusicPlayerCommon내의 멤버는 new없이 사용가능
 */
import static MusicPlayerUtil.MusicPlayerCommon.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.LogManager;

import MusicVo.*;
import UserService.*;
import Mp3PathParser.*;
import MusicPlayerController.MusicPlayerControllerService;
import MusicPlayerController.MusicPlayerControllerServiceImpl;
import MusicPlayerUtil.AsciiArt;
import MusicPlayerUtil.PageViewer;
import UserVo.*;

/** MusicPlayerServie 인터페이스의 기능 구현. 실제 구동의 경우 testList는        
 * allList가 되어야 함. allList 구현 작업중
 * @author 김문수 
 * 2021-07-28
 */
public class MusicPlayerServiceImpl implements MusicPlayerService {
	/**systemPlayList는 실제 재생을 원하는 곡들만 저장되는 리스트 입니다. allList는
	 * 초기 음악폴더를 파싱할때 모든 곡리스트를 저장합니다.
	 * @author 문현석 
	 * 2021-08-02 
	 */
//	private List<Music> systemPlayList = new ArrayList<Music>();
	private List<Music> allList = new ArrayList<Music>();
	private AsciiArt asc = new AsciiArt();
	private UserService us = new UserServiceImpl();
	private PageViewer pv = new PageViewer(this); // 페이지뷰어 객체 생성
	private MusicPlayerControllerService mpc = new MusicPlayerControllerServiceImpl(pv);
	private User user = null; // 테스트용 유저객체
	
	private int messageState				= INIT;
	private static final int GUEST_ERROR 	= 1;
	
	{
		/**
		 * mp3파일을 불러올 경로를 인자로 입력하고
		 * mp3파일을 로드 함
		 */
		mp3FileLoad("./musiclist");
//		mp3FileLoad("./test");
	}

	public List<Music> getAllList() {
		return allList;
	}

	public void setAllList(List<Music> allList) {
		this.allList = allList;
	}
	
	public void settings() {
		System.out.println("┌──────────────────────────────────────────────────────────┐"); 
		System.out.println("┃ 1.페이지출력 수 조정 ┃ 2.볼륨 변동값 설정 ┃   0.뒤로     ┃");
		System.out.println("└──────────────────────────────────────────────────────────┘");
		int input = nextIntFromTo("	   >>>>  ", 0, 2);
		
		switch (input) {
		case 1:
			System.out.println("┌──────────────────────────────────────────────────────────┐"); 
			System.out.printf("┃     한번에 출력할 페이지 수를 입력하세요. (현재  %3d   ) ┃%n", pv.getPrintNum());
			System.out.println("└──────────────────────────────────────────────────────────┘");
			int pageNum = nextIntFromTo("	>>>>", 0, 100); 
			pv.setPrintNum(pageNum);
			break;
		case 2:
			System.out.println("┌──────────────────────────────────────────────────────────┐"); 
			System.out.printf("┃       한번에 조절할 볼륨 값을 입력하세요. (현재 %2d )     ┃%n", mpc.getVolumeValue());
			System.out.println("└──────────────────────────────────────────────────────────┘");
			int volValue = nextIntFromTo("	>>>>", 1, 50);
			mpc.setVolumeValue(volValue);
		case 0:
			break;
		default:
			break;
		}
	}

	/**allList 구현 후 1차 수정. 
	 * 음악의 모든 목록을 페이지를 나눠서 출력. 페이지 이동, 곡선택 구현.
	 * @author 김문수 
	 * 2021-07-29 
	 */
	public void allList() {
		pv.display("[Top100Music]", allList);
	}
	
	/**
	 * @author 김문수
	 *  2021-08-02
	 *  MyList 이름으로 키값에 대입할 계획 수정중
	 *  08-03
	 *  user에서 맵 생성으로 대입 가능
	 */
	public String createMyList() {
		while(true) {
			boolean doubleCheck = true;
			
			System.out.println("┌───────────────────────────────────────────────────────────────┐"); 
			System.out.println("┃         새로만들 재생목록의 이름을 지어주세요. (취소-> 0 )    ┃");
			System.out.println("└───────────────────────────────────────────────────────────────┘");
			String name = nextLine("	>>>> ");
			if(name.equals("0")) {
				return null;
			}
			
			for(String s : myListKey()) {
				if(s.equals(name)) {
					System.out.println("┌──────────────────────────────────────────────────────────────┐"); 
					System.out.println("┃            이미 같은이름의 재생목록이 존재합니다!            ┃");
					System.out.println("└──────────────────────────────────────────────────────────────┘");
					doubleCheck = false;
					break;
				}
			}
			
			// 재생목록 중복확인이 된 경우에만 재생목록 생성
			if(doubleCheck) {
				user.getPlayList().put(name, new ArrayList<Music>());
				pv.setIsNewMyList(true);
				pv.setMyListName(name);
				
				//새로운 플레이리스트를 생성했을시 userDb를 세이브합니다.
				us.saveDb();
				return name;
			}
		}
	}
	
	public void deleteMyList() {
		System.out.println("┌──────────────────────────────────────────────────────────┐"); 
		System.out.println("┃                    리스트의 삭제입니다.                  ┃");
		System.out.println("└──────────────────────────────────────────────────────────┘");
		user.getPlayList().remove(pv.choiceMyList(myListKey()));
		pv.setIsNewMyList(true);
		us.saveDb();
	}
	
	public void deleteMusicIn(List<Music> list) {
		List<Music> playList = list;
		
		while(true) {
			System.out.println("┌──────────────────────────────────────────────────────────┐"); 
			System.out.println("┃            삭제할 곡의 번호를 입력하세요. (취소 -> 0.)   ┃");
			System.out.println("└──────────────────────────────────────────────────────────┘");
			int input = nextIntFromTo("		>>>> ", 0, pv.getPrintNum());
			
			if(input > 0 && input <= playList.size()) {
				playList.remove(input - 1);
				pv.setMessageState(PageViewer.DELETE_MUSIC);
				us.saveDb();
				return;
			}
			else if(input == 0){
				return;
			}
			else {
				System.out.println("┌──────────────────────────────────────────────────────────┐"); 
				System.out.println("┃            잘못된 선택입니다. 다시 입력해주세요.         ┃");
				System.out.println("└──────────────────────────────────────────────────────────┘");
			}
		}
	}
	
	/**
	 * @author 김문수
	 *  2021-08-02
	 *  MyList 목록 출력, MyList가 없을 시 createMyList를 호출
	 *  08-03
	 *  2차 수정
	 *  맵의 키값을 마이리스트의 이름으로 사용하고 따로 저장을 위해 리스트를 사용
	 */
	public void myList() {
		pv.setCurPage(1);
		if(user == null) {
			messageState = GUEST_ERROR;
			return;
		}
		
		while(true) {
			manyLines(60);
			System.out.printf("%n [ 내 재생목록 ] %n");
			
			if(user.getPlayList().isEmpty()) {
				pv.setMessageState(INIT);
				System.out.println("┌──────────────────────────────────────────────────────────┐"); 
				System.out.println("┃                  재생목록이 없습니다.                    ┃");
				System.out.println("└──────────────────────────────────────────────────────────┘");
				System.out.println("┌──────────────────────────────────────────────────────────┐"); 
				System.out.println("┃         1.새로만들기        ┃           0.뒤로           ┃");
				System.out.println("└──────────────────────────────────────────────────────────┘");
				int input = nextIntFromTo("	>>>> ", 0, 1);
				
				switch (input) {
				case 1:
					createMyList();
					break;
				case 0:
					return;
				}
				
			}
			else {
				pv.display("[ 내 재생목록 ]", myListKey());
				// 재생목록을 모두 삭제한경우 return하지 않고 다시 while문을 돈다.
				if(myListKey().isEmpty()) continue;
				return;
			}
		}
		
	}
	
	/**
	 *  마이리스트(user의 hashMap key값)를 메서드화 해서 따로 분리했습니다.
	 * @author 김문수
	 *  2021-08-08
	 */
	public List<String> myListKey() {
		List<String> list = new ArrayList();
		Iterator<String> iterator = user.getPlayList().keySet().iterator();
		for(int i = 0; iterator.hasNext();) {
			list.add(iterator.next());
		}
		return list;
	}
	
	/**
	 *  즐겨찾기 음악을 출력
	 * @author 김문수
	 *  2021-08-02
	 */
	public void favoritList() {
		pv.setCurPage(1);
		if(user == null) {
			messageState = GUEST_ERROR;
			return;
		}
		pv.display("즐겨찾기", user.getFavoritList());
	}
	
	/**파라메터로 받은List의 내에 검색한 단어가 존재할 경우 해당 배열 값 출력
	 * @author 김기락 
	 * 210801
	 */
	public void search(List<?> musicList) {
		/**
		 * @author 문현석 조언 스트링 컨테인스 메서드를 이용해서 해당하는 뮤직객체를 추출 (뮤직객체의 타이틀, 아티스트명, 앨범명 등을
		 *         검색어와 컨테인스) 하시고 임시 리스트에 추가해줍니다 그리고 리스트뷰어 메서드에 임시메서드를 인자로 넣어주세요.
		 * 
		 */
		
		List<Music> tempList = null;
		System.out.println("┌──────────────────────────────────────────────────────────┐"); 
		System.out.println("┃  1.곡명   ┃ 2.가수명  ┃  3.앨범명  ┃  4.장르  ┃  0.뒤로  ┃");
		System.out.println("└──────────────────────────────────────────────────────────┘");
		int input = nextIntFromTo("	>>>> ", 0, 4);
		String listName = "검색 리스트";
		
		switch (input) {
		case 1:
			listName = "곡 제목 검색 리스트";
			tempList = searchBy(TITLE, musicList);
			break;
		case 2:
			listName = "가수명 검색 리스트";
			tempList = searchBy(ARTIST, musicList);
			break;
		case 3:
			listName = "앨범명 검색 리스트";
			tempList = searchBy(ALBUM, musicList);
			break;
		case 4:
			listName = "장르명 검색 리스트";
			tempList = searchBy(GENRE, musicList);
			break;
		case 0:
			return;
		default:
			break;
		}
		
		if(tempList == null) { 
			search(musicList);
			return;
		}
		else {
			pv.setCurPage(1);
			pv.display(listName, tempList);
		}
	}

	/**
	 * 
	 * @author 김기락
	 * 210807 
	 * 리스트 전용 검색 메서드
	 */
	public void searchList(List<?> musicList) {
		List<String> list = new ArrayList();
		Iterator<String> iterator = user.getPlayList().keySet().iterator();
		for(int i = 0; iterator.hasNext();) {
			list.add(iterator.next());
		}
		System.out.println("┌──────────────────────────────────────────────────────────┐"); 
		System.out.println("┃               검색할 폴더 이름을 입력 하세요.            ┃");
		System.out.println("└──────────────────────────────────────────────────────────┘");
		String input = nextLine("	>>>> ");
		
		List<String> mylist = new ArrayList();
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i).toUpperCase().contains(input)) {
				mylist.add(list.get(i));
			}
		}
		if(mylist == null) {
			System.out.println("┌──────────────────────────────────────────────────────────┐"); 
			System.out.println("┃                    검색 결과가 없습니다.                 ┃");
			System.out.println("└──────────────────────────────────────────────────────────┘");
		}
		pv.setCurPage(1);
		pv.display("검색된 리스트", mylist);
	}

	/**
	 * 검색어와 음악의 태그 타입들의 이름과 비교하여 검색된 항목들의 리스트를 반환합니다.
	 * @author 문현석
	 * 2021-08-05
	 * @param tagType(검색할 세부항목)
	 * @param musicList (검색할 리스트)
	 * @return
	 */
	private List<Music> searchBy(String tagType, List<?> musicList) {
		manyLines(60);
		System.out.println("┌──────────────────────────────────────────────────────────┐"); 
		System.out.printf("┃             검색할 %10s 명을 입력하세요.           ┃%n", tagType);
		System.out.println("└──────────────────────────────────────────────────────────┘");
		String input = nextLine("	>>>> ");
		
		List<Music> tempList = new ArrayList<Music>();

		for (int i = 0; i < musicList.size(); i++) {

			Music index = (Music) musicList.get(i);

			// 비교하는 문자열을 대문자로 만들고 검색하는 문자열도 대문자로 만들어서 문자열이 있는지 확인
			if (index.getTag(tagType).toUpperCase().contains(input.toUpperCase())) {
				tempList.add(index);
			}
		}
		if (tempList.isEmpty()) {
			System.out.println("┌──────────────────────────────────────────────────────────┐"); 
			System.out.println("┃             검색하신 항목이 리스트에 없습니다.           ┃");
			System.out.println("└──────────────────────────────────────────────────────────┘");
			tempList = null;
			return null; //오류가 발생할수있는 부분
		} else {
			return tempList;
		}
	}
	
	/**리스트에서 음악을 선택했을때 해야할 기능메서드 입니다
	 * @author 문현석
	 * 2021-08-07
	 * @param music
	 */
	public void pickMusic(Music music) {
		while(true) {
			
			manyLines(60);
			this.message();
			asc.consoleUp();
			System.out.println("\n" + music.getTag() + "\n");
			asc.consoleDown();
			
			boolean isGuest = false;
			if(this.user == null) isGuest = true;
			
			System.out.println("┌─────────────────────────────────────────────────────────────────────────────────────────┐"); 
			System.out.println("┃ 1. 재생  ┃ 2.내 재생목록에 추가  ┃ 3.즐겨찾기추가 ┃ 4.현재 재생목록 끝에 추가  ┃ 0.뒤로 ┃");
			System.out.println("└─────────────────────────────────────────────────────────────────────────────────────────┘");
			int input = nextIntFromTo("   >>>> ", 0,4);
			switch(input) {
			case 1:
				// 선택곡 즉시 재생
				mpc.instantPlay(music);
				break;
			case 2:
				if(isGuest) {
					manyLines(60);
					asc.panelArtU();
					System.out.println("		|게스트는 이용할 수 없는 컨텐츠 입니다.|");
					asc.panelArtD();
					return;
				}
				// 내 재생목록에 추가하는 메서드 작성필요 > 2021-08-08 김문수 완료
				else {
					pv.setAddMusic(true);
					addMyList(music);
				}
				break;
			case 3:
				if(isGuest) {
					manyLines(60);
					asc.panelArtU();
					System.out.println("		|게스트는 이용할 수 없는 컨텐츠 입니다.|");
					asc.panelArtD();
					return;
				}
				// 즐겨찾기에 추가하는 메서드 작성필요 > 2021-08-08 김문수 완료
				else {
					user.getFavoritList().add(0, music);
					//즐겨찾기 목록에 새 노래를 추가했을 시 userDb를 세이브 합니다.
					us.saveDb();
					pv.setMessageState(PageViewer.ADD_FAVORITLIST);
					
//					pv.display("즐겨찾기", user.getFavoritList());
				}
				break;
			case 4:
				mpc.addNowPlayList(music);
				break;
			case 0:
				return;
			}
			
		}
	}
	
	/**음악추가를 위해 사전에 경로를 지정해주는 메서드 입니다.
	 * @author 김문수
	 * 2021-08-08
	 * @param music
	 */
	public void addMyList(Music music) {
		if(user.getPlayList().isEmpty()) {
			manyLines(60);
			asc.panelArtU();
			System.out.println("		|           재생목록이 없습니다.       |");
			asc.panelArtD();
			
			// 새로만든 재생목록에 노래 추가 ( 중간에 노래추가 안할시 이전 스택으로 빠짐)
			if(addMusic(music, createMyList())) {
				pv.setCurPage(1);
				pv.setMusic(music);
				pv.display("[ 내 재생목록에 <"+ music.getTag(TITLE) +"> 곡 추가하기 ]", myListKey());				
			}
			
		}
		else {
			pv.setCurPage(1);
			pv.setMusic(music);
			pv.display("[ 내 재생목록에 <"+ music.getTag(TITLE) +"> 곡 추가하기 ]", myListKey());
		}
	}
	
	/**실질적으로 음악을 추가해주는 메서드 입니다.
	 * @author 김문수
	 * 2021-08-08
	 * @param music
	 */
	public boolean addMusic(Music music, String listName) {
		if(listName == null) return false;
		
		if(!music.equals(null)) {
			System.out.println("┌──────────────────────────────────────────────────────────┐"); 
			System.out.println("┃                  음악을 추가 하시겠습니까?               ┃");
			System.out.println("┃                  YES : 1            NO : 2               ┃");
			System.out.println("└──────────────────────────────────────────────────────────┘");
			int input = nextIntFromTo("	>>>> ", 1, 2);
			if(input == 1) {
				user.getPlayList().get(listName).add(music);
				
				// 플레이리스트에 음악을 추가했을경우 userDb를 세이브합니다
				us.saveDb();
				
				// 성공적으로 곡을 추가 했을시 상태메세지 출력
				pv.setMessageState(PageViewer.SUCCESS_ADD_MUSIC);
				return true;
			}
		}
		return false;
	}
	
	/**마이리스트 선택용 메서드, 선택해서 user의 playList에 접근합니다.
	 * @author 김문수
	 * 2021-08-08
	 * @param name
	 */
	public void pickList(String name) { 
		if(pv.isAddMusic()) {
			addMusic(pv.getMusic(), name);
		}
		else {
			pv.setCurPage(1);
			pv.display("["+ name + "]", user.getPlayList().get(name));
		}
	}
	
	/**
	 * 재생목록 전체를 재생시키는 메서드 입니다.
	 * @author 문현석
	 * 2021-08-09
	 * @param musicList
	 */
	public void listPlay(List<Music> musicList, String listName) {
		mpc.setListName(listName);
		mpc.listPlay(musicList);
	}
	
	/**
	 * 음악폴더를 지정하여 로딩하는 과정입니다.
	 * @author 문현석
	 * 2021-07-31
	 */
	private void mp3FileLoad(String directory) {
		Mp3PathParser mp3PathParser = new Mp3PathParser(directory);
		LogManager.getLogManager().reset();
		for (String filePath : mp3PathParser.getPathList()) {
			Music music = new Music(filePath);

			if (music.getTag().isTrash())
				continue;
			allList.add(music);
		}
	}
	
	/**
	 * 기능 테스트를 위한 임시 main메서드 입니다.
	 * @author 문현석
	 * 2021-07-30
	 */
	public static void main(String[] args) {
		// 임플리먼트 타입으로 인스턴스를 받아야 임플리먼트에 있는 모든 메소드를 호출 가능해집니다.
		//MusicPlayerServiceImpl mps = new MusicPlayerServiceImpl();

		// 실제 Ex에서는 이렇게 생성할겁니다. (인터페이스에 정의돼있지 않은 메소드는 호출 불가입니다.)
		//MusicPlayerService mps2 = new MusicPlayerServiceimpl();
	}
	
	public void musicController() {
		mpc.musicController();
	}
	
	//프로그램 구동시 로그인하는 부분입니다.
	public void login() {
		us.errorMessage();
		asc.menu();
		int input = nextIntFromTo("   >>>> ", 0, 3);
		
		switch (input) {
		case 1:
			this.user = us.login(); // <- 이런식으로 뮤직서비스에서 사용될 유저인스턴스를 참조한다
			if(this.user == null) break; //로그인에 실패했을때 null이 반환 되고 다시 메서드가 실행되어야 함.
			else {
				mpc.setGuest(false);
				return; //로그인에 성공했을때 로그인 메서드를 빠져나간다.
			}
		case 2:
			us.addUser();
			break;
		case 3:
			this.user = us.guestUser();
			//게스트 입장시 뮤직플레이어 컨트롤러에게 알립니다.(1분듣기 제한때문)
			mpc.setGuest(true);
			return;
		case 0:
			asc.byebye();
			System.exit(0);
		}
		
		login();
	}

	//로그아웃 했을때 재생컨트롤러 쓰레드를 종료 시킵니다.
	//그리고 현재 유저의 데이터를 저장합니다.
	public void logout() {
		mpc.closeThread();
		us.saveDb();
	}

	public void message() {
		switch (messageState) {
		case GUEST_ERROR:
			asc.panelArtU();
			System.out.println("		|  게스트는 이용할 수 없는 컨텐츠 입니다.  |");
			asc.panelArtD();
			messageState = INIT;
			break;
		default:
			break;
		}
		pv.message();
		mpc.message();
	}
	
	public void fromUserMessage() {
		us.toMainMessage();
	}
}
