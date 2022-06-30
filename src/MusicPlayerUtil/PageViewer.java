package MusicPlayerUtil;

import static MusicPlayerUtil.MusicPlayerCommon.*;
import MusicPlayerService.*;

import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import MusicVo.Music;
import UserVo.*;
import UserService.*;

/**
 * @author 김문수
 * 2021-08-05
 * MusicPlayerServiceimpl에 있는 리스트 출력용 메서드를 클래스로 생성.
 * 메서드 여러개로 나눔.
 */

public class PageViewer {
	ArrayList<String> list = new ArrayList<String>();
	List<Music> musicList = new ArrayList<Music>();
	MusicPlayerCommon util = new MusicPlayerCommon();
	AsciiArt asc = new AsciiArt();
	User user = new User();
	Music music = new Music();
	MusicPlayerServiceImpl mps;
	private int printNum=10;  // 표시될 음악 갯수
	private int curPage = 1;  // 현재페이지
	private int totalPage;	  // 전체페이지	
	private int startNum;	  // 리스트 출력 시작 넘버
	private int endNum;       // 리스트 출력 끝 넘버 ( 리스트를 출력하는 for문의 제한수 )
	private int musicNum;     // 음악 총합
	private boolean isAddMusic = false;		// 현재 페이지에서 곡을 추가 할것인지 제어
	private boolean isExit = false;			// 현재 페이지에서 나갈것인지 제어
	private boolean isNewMyList = false; 	// 재생목록을 새로 만들었을때 새로추가된 목록도 보이게 하기위한 플래그 입니다.
	private boolean isStart = false;		// 전체 페이지의 시작이면 true
	private boolean isEnd = false;       	// 전체 페이지의 끝이면 true ( 버튼 입력시 보이지 않는 버튼의 입력을 막기위한 플래그 입니다. )
	private String myListName = "";
	
	private int messageState = INIT;				// 기능 조작후 상태에 알맞은 메세지 출력을 위한 state 변수
	public static final int WRONG_LIST = 1;			// 잘못된 리스트 출력시
	public static final int NO_LIST = 2;			// 비어있는 리스트 출력시
	public static final int WRONG_INPUT = 3;		// 잘못된 입력시
	public static final int DELETE_LIST = 4;		// 리스트 삭제시
	public static final int DELETE_MUSIC = 5; 		// 곡 삭제시
	public static final int SUCCESS_ADD_MUSIC = 6;	// 재생목록에 성공적으로 곡을 추가 했을시
	public static final int ADD_FAVORITLIST = 7;	// 즐겨찾기에 성공적으로 곡을 추가 했을시

	public PageViewer() {}
	
	public PageViewer(MusicPlayerServiceImpl mps) {
		super();
		this.mps = mps;
	}
	
	public void message() {
		String bgStr ="|                                          |"; // 패널아트의 배경라인
		
		switch (messageState) {
		case WRONG_LIST:
			asc.panelArtU();
			System.out.println("		|	    	  잘못된 리스트입니다.         |");
			asc.panelArtD();
			break;
			
		case NO_LIST:
			asc.panelArtU();
			System.out.println("		|         리스트내에 음악이 없습니다.      |");
			asc.panelArtD();
			break;
			
		case WRONG_INPUT:
			asc.panelArtU();
			System.out.println("		|             잘못된 입력입니다.           |");
			System.out.println("		|       없는 버튼인데 누르지 마세요.       |");
			asc.panelArtD();
			break;
			
		case DELETE_LIST:
			asc.panelArtU();
			System.out.println("		|           리스트를 삭제했습니다.         |");
			asc.panelArtD();
			break;
			
		case DELETE_MUSIC:
			asc.panelArtU();
			System.out.println("		|           선택한곡을 삭제했습니다.       |");
			asc.panelArtD();
			break;
		case SUCCESS_ADD_MUSIC:
			asc.panelArtU();
			System.out.println("		|                 재생목록                 |");
			System.out.println("		" + getBackgroundMacthString(bgStr, "< "+getShortString(myListName, 30)+" >에"));
			System.out.println("		" + getBackgroundMacthString(bgStr, "< "+getShortString(music.getTag(TITLE), 30)+" >곡을"));
			System.out.println("		|               추가했습니다.              |");
			asc.panelArtD();
			break;
			
		case ADD_FAVORITLIST:
			asc.panelArtU();
			System.out.println("		|                즐겨찾기에                |");
			System.out.println("		" + getBackgroundMacthString(bgStr, "< "+getShortString(music.getTag(TITLE), 30)+" >곡을"));
			System.out.println("		|               추가했습니다.              |");
			asc.panelArtD();
			break;
			
		default:
			break;
		}
		messageState = INIT;
	}
	
	/**
	 * 통합 출력용 메서드. 리스트를 보여주고, 선택용 버튼을 구현.
	 * @author 김문수
	 * 2021-08-06
	 */
	public void display(String listName, List<?> list) {
		while(isExit == false) {
			if(isNewMyList) { //재생목록을 새로 만들었을때 새로운 목록도 함께 출력되게 위한 플래그 조건입니다.
				isNewMyList = false;
				display(listName, mps.myListKey());
				return;
			}
			
			if(list == null) {
				messageState = WRONG_LIST;
				return;
			}
			else if(list.isEmpty()) {
				messageState = NO_LIST;
				return;
			}
			else if(isExit) {
				return;
			}
			
			musicNum = list.size();
			
			allListViewer(listName, list);
			getPageElements(listName, list);
			pageButton(listName, list);
		}
		
		isExit = false;
		curPage = 1;
	}
	
	/**
	 * 리스트를 출력할때 선택할 버튼들을 보여줍니다.
	 * @author 김문수
	 * 2021-08-06
	 */
	public void getPageElements(String listName, List<?> list) {
		totalPage = getTotalPage();
		boolean prevPage = curPage > 1;
		boolean nextPage = curPage < totalPage;
		if(curPage == 1) isStart = true;
		else {
			isStart = false;
		}
		if(curPage >= totalPage) isEnd = true;
		else {
			isEnd = false;
		}
		System.out.print("\n" + " Pages : " + curPage + " / " + totalPage + "\n");
		
		if(!listName.equals("[Top100Music]") && list.get(0) instanceof Music) {
			if(prevPage && !nextPage) {
				asc.playList_prev();
			}else if(!prevPage && nextPage){
				asc.playList_next();
			}else if(curPage == 1 && totalPage == 1) {
				asc.playList_none();
			}else {
				asc.playList_all();
			}
		}
		else if(list.get(0) instanceof Music) {
//			return "\n" + " Pages : " + curPage + " / " + totalPage + "\n" + (prevPage ? "1.이전    " : "") + (nextPage ? "2.다음    " : "") +
//				"3.선택    4.검색    5.전체재생    0.뒤로";
			if(prevPage && !nextPage) {
				asc.prev();
			}else if(!prevPage && nextPage){
				asc.next();
			}else if(curPage == 1 && totalPage == 1) {
				asc.none();
			}else {
				asc.all();
			}
		}else {
			if(prevPage && !nextPage) {
				asc.prevString();
			}else if(!prevPage && nextPage){
				asc.nextString();
			}else if(curPage == 1 && totalPage == 1) {
				asc.noneString();
			}else {
				asc.allString();
			}
		}
	}
	
	public int getTotalPage() {
		return (int)Math.ceil(musicNum/(double) printNum);
	}
	/**
	 * 리스트를 불러와 번호와 함께 출력합니다 .
	 * @author 김문수
	 * 2021-08-06
	 */
	public void allListViewer(String listName, List<?> list) {
		startNum = (curPage -1) * printNum;
		endNum = startNum + printNum;
		int listNum = startNum;
		if (list.isEmpty()) {
			asc.panelArtU();
			System.out.println("		|          리스트가 비었습니다.        |");
			asc.panelArtD();
			return;
		}else {
			try {
			int musicNum = list.size();
			printAttribute(listName, list);
			for (int i = startNum; i < endNum; i++) {
				if(i > musicNum) break;
				
				listNum += 1;
				int viewNum = listNum;
				
				if(viewNum < 10) {
					System.out.printf("% d. %s%n", viewNum, list.get(i));
				}
				else {
					System.out.printf("%d. %s%n", viewNum, list.get(i));
				}
				
			}
			}catch(IndexOutOfBoundsException e) {}
			System.out.println("========================================================================================================");
		}
	}
	
	/**
	 * 리스트가 출력되면 선택버튼을 입력받아 상호 작용 합니다 .
	 * @author 김문수
	 * 2021-08-06
	 */
	public void pageButton(String listName, List<?> list) {
		int input = nextIntFromTo("	>>>>", 0, 6);
		switch(input) {
		case 1:
			if(isStart) {
				messageState = WRONG_INPUT;
				isStart = false;
			}
			else {
				if(1 < curPage) curPage -= 1;
			}
			break;
			
		case 2:
			if(isEnd) {
				messageState = WRONG_INPUT;
				isEnd = false;
			}
			else {
				if(curPage < totalPage) curPage +=1;
			}
			break;
			
		case 3:
			//곡선택
			choiceOne(listName, list);
			break;
			
		case 4:
			//검색
			if(list.get(0) instanceof Music) {
				mps.search(list);
			}
			else if(list.get(0) instanceof String) {
				mps.searchList(list);
			}
			break;
			
		case 5:
			// 전체 재생
			if(list.get(0) instanceof Music) {
				mps.listPlay((List<Music>) list, listName);
				
			}
			// 리스트 삭제
			else if(list.get(0) instanceof String){
				messageState = DELETE_LIST;
				mps.deleteMyList();
			}
			break;
			
		case 6:
			// 내 재생목록에서 곡 삭제하기
			if(!listName.equals("[Top100Music]") && list.get(0) instanceof Music) {
				mps.deleteMusicIn((List<Music>) list);
			}
			// 내 재생목록에서 재생목록 추가하기
			else if(list.get(0) instanceof String) {
				mps.createMyList();
			}
			break;
			
		case 0:
			//종료
			isExit = true;
			isAddMusic = false;
			break;
		}
	}

	/**
	 * MyList와 Music의 속성이 달라 따로 구분해서 출력하기 위한 매서드 입니다.
	 * @author 김문수
	 * 2021-08-07
	 */
	public void printAttribute(String listName, List<?> list) {
		manyLines(60);
		mps.message();
		if(list.get(0) instanceof Music) {
			System.out.println("\n" + listName);
			System.out.println("========================================================================================================");
			System.out.println("              곡명                           가수                       앨범                장르        ");
			System.out.println("========================================================================================================");
		}else if(list.get(0) instanceof String){
			System.out.println("\n" + listName);
//			asc.meunArt();
			System.out.println("========================================================================================================");
		}
	}
	
	/**
	 * MyList인지 Music인지 판단해서 각기 다른 메서드를 호출해 pageButton으로 보내줍니다. 
	 * @author 김문수
	 * 2021-08-08
	 */
	public void choiceOne(String listName, List<?> list) {
		int input;
		if(list.get(0) instanceof Music) {
			System.out.println("┌──────────────────────────────────────────────────────────┐"); 
			System.out.println("┃            음악의 번호를 고르세요.    ( 뒤로 > 0 )       ┃");
			System.out.println("└──────────────────────────────────────────────────────────┘");
			input = nextIntFromTo("	>>>> ", 0, endNum);
			if(input == 0) {
				return;
			}else {
			music = (Music) list.get(input -1);
			mps.pickMusic(music);
			}
		}
		else if(list.get(0) instanceof String) {
			mps.pickList(choiceMyList(list));
		}
	}
	
	/**
	 * MyList용 리스트뷰어 메서드입니다. 
	 * @author 김문수
	 * 2021-08-08
	 * @return 
	 */
	public String choiceMyList(List<?> list) {
		System.out.println("┌──────────────────────────────────────────────────────────┐"); 
		System.out.println("┃                 재생목록의 번호를 고르세요.              ┃");
		System.out.println("└──────────────────────────────────────────────────────────┘");
		int input = nextIntFromTo("	>>>> ", 1, endNum);
		myListName = (String) list.get(curPage * input -1);
		return myListName;
	}

	public int getPrintNum() {
		return printNum;
	}

	public void setPrintNum(int printNum) {
		this.printNum = printNum;
	}

	public int getMusicNum() {
		return musicNum;
	}

	public void setMusicNum(int musicNum) {
		this.musicNum = musicNum;
	}

	public int getCurPage() {
		return curPage;
	}

	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}
	
	public boolean isAddMusic() {
		return isAddMusic;
	}

	public void setAddMusic(boolean isAddMusic) {
		this.isAddMusic = isAddMusic;
	}

	public String getMyListName() {
		return myListName;
	}

	public void setMyListName(String myListName) {
		this.myListName = myListName;
	}

	public boolean isStart() {
		return isStart;
	}

	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}

	public boolean isEnd() {
		return isEnd;
	}

	public void setEnd(boolean isEnd) {
		this.isEnd = isEnd;
	}
	
	public boolean isNewMyList() {
		return isNewMyList;
	}
	
	public void setIsNewMyList(boolean isNewMyList) {
		this.isNewMyList = isNewMyList;
	}

	public int getMessageState() {
		return messageState;
	}

	public void setMessageState(int messageState) {
		this.messageState = messageState;
	}

	public Music getMusic() {
		return music;
	}

	public void setMusic(Music music) {
		this.music = music;
	}

	public boolean isExit() {
		return isExit;
	}

	public void setExit(boolean isExit) {
		this.isExit = isExit;
	}
	
}
