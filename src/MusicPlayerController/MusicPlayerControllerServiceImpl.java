package MusicPlayerController;

import static MusicPlayerUtil.MusicPlayerCommon.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import MusicPlayerUtil.AsciiArt;
import MusicPlayerUtil.PageViewer;
import MusicVo.Music;

/**
 * 뮤직플레이어 컨트롤러 클래스를 이용한 다양한 뮤직컨트롤 메서드를 제공합니다
 * @author 문현석
 * 2021-08-02
 *
 */
public class MusicPlayerControllerServiceImpl extends Thread implements MusicPlayerControllerService{
	
	private MusicPlayerController mpc = new MusicPlayerController();
	private List<Music> playList = new ArrayList<>();
	private AsciiArt asc = new AsciiArt();
	private PageViewer pv = null;
	
	private int playIndex 			= 0; 			// 현재 재생순번을 기록하는 변수
	private int volGain				= 5;			// 볼륨 조절 값입니다.
	private Thread thread       	= null;
	private boolean isLoad			= false;		// 쓰레드를 이용하여 다음곡 로드를 할것인지 선택하는 flag
	private boolean isRun			= true;			// 쓰레드를 정지시킬때 false값을 가지게 합니다.
	private boolean isLastSong		= false;		// 리스트끝까지 플레이 됐을때 true값을 가제 됩니다.
	private boolean isSetVoluem 	= false;		// 볼륨 조절을 했을때 디스플레이에 출력되게 하기위한 flag
	private boolean isGuest			= false;		// 게스트인 경우 음악재생은 1분으로 제한하기위한 flag
	private boolean isControlView 	= false;		// 컨트롤러 화면상에서만 true값을 가집니다.
	private boolean isClearMSG		= false;		// 연속입력 일시 false
	private boolean autoClear		= false;		// 일정시간후 메세지 자동삭제 flag
	private int savePlayTime 		= 0;			// 오디오 디바이스가 재생한 시간을 저장합니다.
	private String musicName		= "";			// 재생중인 노래제목
	private String listName			= "";			// 재생중인 재생목록의 이름
	private String playSetting		= "리스트 한번재생";
	private Date beforeInputTime = new SimpleDateFormat("HH:mm:ss", Locale.KOREA).getCalendar().getTime();

	private int playState				= ALL_ONCE;
	private static final int ONCE 		= 0;	// 한곡만 재생
	private static final int ONCE_LOOP 	= 1;	// 한곡만 반복재생
	private static final int ALL_ONCE 	= 2;	// 전체곡 한번만 재생	//*기본값
	private static final int ALL_LOOP	= 3;	// 전체곡 반복재생
	
	private int messageState				= INIT;
	private static final int NO_LIST_ERROR 	= 1;
	public static final int INSTANT_PLAY = 2;
	public static final int ALL_PLAY = 3;
	public static final int ADD_MUSIC = 4;
	public static final int PLAY_ONCE = 5;
	public static final int PLAY_ONCE_LOOP = 6;
	public static final int PLAY_ALL_ONCE = 7;
	public static final int PLAY_ALL_LOOP = 8;
	public static final int PLAY_LIST_SHUFFLE = 9;
	public static final int PLAY_PUASE = 10;
	public static final int PLAY_GO = 11;
	public static final int PLAY_STOP = 12;
	public static final int PLAY_PREV = 13;
	public static final int PLAY_NEXT = 14;
	public static final int PLAY_RESTART = 15;
	
	public MusicPlayerControllerServiceImpl() {}
	
	public MusicPlayerControllerServiceImpl(PageViewer pv){
		this.pv = pv;
	}
	
	/**
	 * 외부에서 뮤직리스트를 이용해 생성할때 플레이인덱스 또한 0으로 초기화 해줍니다.
	 * 또한기존리스트의 인스턴스와 연결을 끊습니다.
	 * @author 문현석
	 * 2021-08-03
	 */
	public void setPlayList(List<Music> playList) {
		this.playList = null;
		this.playList = new ArrayList<>(playList);
		this.playIndex = 0;
	}
	
	public List<Music> getPlayList() {
		return this.playList;
	}
	
	public boolean isGuest() {
		return isGuest;
	}

	/**
	 * 현재 프로그램 이용자가 게스트인지 아닌지 알려주기위한 setter입니다.
	 * @author 문현석
	 * 2021-08-11
	 */
	public void setGuest(boolean isGuest) {
		this.isGuest = isGuest;
	}
	
	/**
	 * 외부에서 선택한곡을 바로 재생하길 원할때 호출되는 메서드입니다.
	 */
	public void instantPlay(Music music) {
		musicName = music.getTag(TITLE);
		messageState = INSTANT_PLAY;
		//현재 리스트가 비어있으면 리스트에 추가하고 바로 재생
		if(playList.isEmpty()) { 
			playList.add(music);
			mpc.setState(STARTED);
			playSequence(NOTHING,false);
		}
		//현재 리스트에 노래가있을경우, 바로 재생할 music을 리스트 다음 인덱스에 추가하고
		//플레이인덱스를 증가시켜 방금 추가한 노래를 바로 재생시킴.
		else {
			//다음 인덱스가 범위를 벗어난 경우 바로 추가하고 재생시킨다.
			int nextIndex = playIndex + 1;
			if(nextIndex >= playList.size()) {
				playList.add(music);
			}
			else {
				playList.add(nextIndex, music);
			}
			mpc.setState(STARTED);
			playSequence(NEXT, true);
		}
		if(mpc.getThread() == null) mpc.start();
		if(thread == null) start();
	}

	/**
	 * 파라메터로받은 리스트 전체를 재생합니다.
	 * @author 문현석
	 * 2021-08-09
	 */
	public void listPlay(List<Music> musicList) {
		setPlayList(musicList);
		playSequence(NOTHING, true);
		mpc.setState(STARTED);
		messageState = ALL_PLAY;
		
		if(mpc.getThread() == null) mpc.start();
		if(thread == null) start();
		
		manyLines(60);
		this.message();
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 현재 재생목록에 노래를 추가하는 메서드(외부에서 사용됩니다)
	 * @author 문현석
	 * 2021-08-11
	 */
	public void addNowPlayList(Music music) {
		messageState = ADD_MUSIC;
		playList.add(music);
		musicName = music.getTag(TITLE);
	}
	
	/**
	 * 입력된 키값에 따라 컨트롤러를 조작합니다.
	 * synchronized 하지 않으면 수많은 버그초래
	 * @author 문현석
	 * 2021-08-03
	 */
	public void musicController() {
		//컨트롤러 화면상에서만 true값을 가집니다.
		isControlView = true;
		
		if(playList == null) {
			messageState = NO_LIST_ERROR;
			return;
		}
		else if(playList.isEmpty()){
			messageState = NO_LIST_ERROR;
			return;
		}
		
		if(mpc.getThread() == null) mpc.start();
		if(thread == null) start();
		
		beforeInputTime = new SimpleDateFormat("HH:mm:ss", Locale.KOREA).getCalendar().getTime();

		while(true) {
			playInfoView(); //컨트롤러 출력부분 메서드
			int input = nextIntFromTo("   >>>> ", 0, 8, true);
			
			// 입력시간 저장
			beforeInputTime = new SimpleDateFormat("HH:mm:ss", Locale.KOREA).getCalendar().getTime();
			
			// 입력값에 따라 기능 실행
			switch (input) {
			case 1: //.재생
				play(mpc.getState());
				break;
				
			case 2: //.정지
				mpc.setState(STOPPED);
				messageState = PLAY_STOP;
				break;
				
			case 3: //.리스트의 이전곡 선택 공통된 코드를 따로 만들것
				playSequence(PREV,true);
				savePlayTime = mpc.getPosition();
				messageState = PLAY_PREV;
				break;
				
			case 4: //.리스트의 다음곡 선택
				playSequence(NEXT,true);
				savePlayTime = mpc.getPosition();
				messageState = PLAY_NEXT;
				break;
				
			case 5: //. 재생환경 설정
				isControlView = false;
				playSettings();
				isControlView = true;
				break;
				
			case 6: //. 볼륨 증가 
				//볼륨범위 백분율에 대한 1%값이 0.86이므로 volGain(int형)값을 통해
				//조절 해야 백분율 출력시 의도한 조절 값에따른 결과를 얻을수 있습니다.
				// ex) volGain == 2일때 볼륨값을 백분율하여 출력시 보여지는 값또한 2증가한 값을 보여줄수있음 
				setVolume((float)(-volGain * 0.86));
				isSetVoluem = true;
				break;
				
			case 7: //. 볼륨 감소
				setVolume((float)(volGain * 0.86));
				isSetVoluem = true;
				break;
				
			case 8: //. 현재 재생목록 보기
				isControlView = false;
				this.pv.display("♩♪ Now PlayList ♬♩", playList);
				isControlView = true;
				break;
				
			case 0: 
				isControlView = false;
				return;
				
			default:
				break;
			}
		}
	}
	
	/**
	 * 뮤직컨트롤 쓰레드의 재생상태에따라서 재생할지 일시정지 할지 결정합니다.
	 * @author 문현석
	 * 2021-08-04
	 * @param musicState
	 */
	private void play(int musicState) {
		if(musicState == INIT) {
			mpc.setState(STARTED);
			playSequence(NOTHING,false);
		}
		else if(musicState == STARTED) {
			mpc.setState(SUSPENDED);
			messageState = PLAY_PUASE;
		}
		else if(musicState == SUSPENDED) {
			mpc.setState(STARTED);
			messageState = PLAY_GO;
		}
		else{
			mpc.setState(STARTED);
			messageState = PLAY_RESTART;
		}
	}
	
	/**
	 * 다음곡, 이전곡 기능을 구현한 메서드입니다.
	 * @author 문현석
	 * 2021-08-08(수정)파라메터 변경
	 * @param sequence (다음곡, 이전곡에 대한 상수(NEXT, PREV, STAY)를 인자로 받습니다.)
	 * @param isSkip (메서드가 호출됐을때 현재 재생중인 곡을 스킵할것인지 말것인지 선택합니다)
	 */
	private void playSequence(int sequence,boolean isSkip) {
		if(sequence == NEXT) playIndex++;
		else if(sequence == PREV) playIndex--;
		
		// 플레이 인덱스가 범위를 넘어갔을때 안전하게 처리 합니다.
		safeIndexing();
		
		// 디코딩 도중 사용자가 다음곡,이전곡 으로 전환할시 디코딩을 스킵한다.
		if(mpc.isDecoding()) mpc.setDecodeSkip(true);
		
		//재생도중 곡을 변경하면 재생로직을 빠져나간다.
		mpc.setSkip(isSkip); 	
		
		//재생중이던 곡의 재생관련 데이터를 초기화 한다.
		mpc.close();	
		
		//playIndex에 해당하는 곡을 로드합니다.
		isLoad = true;
	}
	
	/**
	 * 뮤직리스트의 인덱스가 범위를 넘어갔을때 안전하게 처리함.
	 */
	private void safeIndexing() {
		if(playList == null) return;
		if(playIndex < 0 ) {
			playIndex = playList.size() -1;
		}
		else if(playIndex >= playList.size()) { 
			playIndex = 0;
		}
	}
	
	/**
	 * 멀티쓰레드를 이용해서 파일을 로드하는 동시에 메인쓰레드는 정보를 출력하게했음.
	 * @author 문현석
	 * 2021-08-07
	 * @param musicState
	 */
	private void preLoad() {
		if(!isLoad) return;
		mpc.open(playList.get(playIndex));
		
		if(this.mpc.getState() == FINISHED) this.mpc.setState(STARTED);
		
		isLoad = false;
	}
	
	/**
	 * 한곡이 끝났을때 재생환경설정 값에따라 
	 * 재생설정이 바뀌는 부분을 담당하는 메서드입니다.
	 * @author 문현석
	 * 2021-08-05
	 */
	private void autoPlay() {
		//현재곡 다음 인덱스가 리스트 사이즈와 같을때 현재곡이 마지막곡인것을 알수있음
		if(playList != null && !playList.isEmpty())
			if(playIndex + 1 >= playList.size()) isLastSong = true;
		
		//게스트 유저가 이용시 재생 시간을 1분으로 제한합니다.
		if(isGuest) {
			int playTime = mpc.getPosition() - savePlayTime;
			if(playTime/1000 > 60) {
				mpc.setState(FINISHED);
			}
		}
		
		// 한곡이 끝났을때 재생환경설정값에 따라 switch문이 작동합니다.
		if(mpc.getState() == FINISHED) {
			
			//오디오 디바이스의 재생 시간을 저장해줍니다.
			savePlayTime = mpc.getPosition();
			
			switch (playState) {
			case ONCE: 		//한곡만 한번 재생일때 더이상재생 시키지 않음.
				mpc.setState(INIT);
				break;
				
			case ONCE_LOOP:	//한곡만 반복 재생일때 
				playSequence(NOTHING,false);
				break;
				
			case ALL_ONCE:	//리스트 전체 한번만 재생
				if(isLastSong) { 
					//리스트 끝까지 재생됐을때 정지 시킵니다.
					mpc.setState(INIT);
					isLastSong = false;
				}
				else { 
					//리스트 끝이 아닌경우 다음곡을 재생시킵니다.
					playSequence(NEXT, false);
					if(isControlView) {
						playInfoView();
						System.out.print("   >>>> ");
					}
				}
				break;
				
			case ALL_LOOP: // 리스트 전체 반복재생일때 계속해서 다음곡을 재생시킵니다.
				playSequence(NEXT, false);
				if(isControlView) {
					playInfoView();
					System.out.print("   >>>> ");
				}
				break;
				
			default:
				break;
			}
		}
	}
	
	/**
	 * 재생환경 설정에관한 메서드입니다.
	 * @author 문현석
	 * 2021-08-10
	 */
	private void playSettings() {
		isControlView = false;
		int input = ALL_ONCE;
		
		while(true) {
			manyLines(60);
			this.message();
			asc.consoleUp();
			System.out.println();
			System.out.println("♬ 재생환경 설정 메뉴 입니다 ♬	  현재 : " + playSetting);	
			System.out.println();
			asc.consoleDown();
			
			System.out.println("┌──────────────────────────────────────────────────────────────────────────────────────────────────────┐");
			System.out.println("┃ 1.한곡 한번재생 ┃ 2.한곡 반복재생 ┃ 3.리스트 한번재생 ┃ 4.리스트 반복재생 ┃ 5.재생목록 셔플 ┃ 0.뒤로 ┃");
			System.out.println("└──────────────────────────────────────────────────────────────────────────────────────────────────────┘");
			input = nextIntFromTo("   >>>> ", 0, 5);
			switch (input) {
			case 1:
				playState = ONCE;
				messageState = PLAY_ONCE;
				playSetting = "한곡 한번재생";
				break;
				
			case 2:
				playState = ONCE_LOOP;
				messageState = PLAY_ONCE_LOOP;
				playSetting = "한곡 반복재생";
				break;
				
			case 3:
				playState = ALL_ONCE;
				messageState = PLAY_ALL_ONCE;
				playSetting = "리스트 한번재생";
				break;
				
			case 4:
				playState = ALL_LOOP;
				messageState = PLAY_ALL_LOOP;
				playSetting = "리스트 반복재생";
				break;
				
			case 5:
				//재생목록 셔플 (현재 재생중인 곡을 제외하고 셔플 해준다.)
				Music beforeTemp = playList.get(playIndex);
				Collections.shuffle(playList);
				playList.set(playList.indexOf(beforeTemp), playList.get(playIndex));
				playList.set(playIndex, beforeTemp);
				messageState = PLAY_LIST_SHUFFLE;
				break;
				
			case 0:
				//메서드를 빠져나감
				isControlView = true;
				return;
				
			default:
				break;
			}
		}
	}
	
	/**
	 * 볼륨을 조절하는 메서드입니다.
	 * @author 문현석
	 * 2021-08-10
	 */
	private void setVolume(float gain) {
		mpc.setVolume(gain);
	}
	
	/**
	 * 컨트롤러의 뷰어부분입니다.
	 * @author 문현석
	 * 2021-08-05
	 */
	private void playInfoView() {
		manyLines(60);
		//MsicPlayerContorller클래스의 오류 또는 특정상황에 대한 메세지를 출력
		mpc.message();
		this.message();
		
		//비회원이 컨트롤러에 진입했을 경우 나오는 안내문구입니다.
		if(isGuest) System.out.println("\n비회원은 1분 미리듣기만 가능합니다.\n");
		
		asc.consoleUp();
		//다음 플레이 인덱스가 리스트 끝인경우 출력
		if( playIndex + 1 >= playList.size()) {
			System.out.println("\n다음 곡명 : 리스트 마지막 입니다.\n");
		}
		//다음곡명을 출력
		else {
			System.out.println("\n다음 곡명 : " + playList.get(playIndex + 1).getTag(TITLE) + "\n");
		}
		
		// 현재 재생중인 곡명을 출력(리스트를 셔플해도 지금 재생중인 노래가 제목이 출력됨
//		System.out.println("현재 곡명 : " + mpc.getSaveMusicTiltle());
		System.out.println("현재 곡명 : " + playList.get(playIndex).getTag(TITLE) + "\n");
		asc.consoleDown();
		
		//볼륨 조절시 화면에 나타나게 됩니다.
		String volumeStr = "";
		if(isSetVoluem) {
			String str = String.format("< Volume: %-3s> ", mpc.getVolume());
			volumeStr = str;
			isSetVoluem = false;
		}
		else {
			volumeStr = "               ";
		}
		
		//버튼 출력문입니다.
		System.out.println("┌────────────────────────────────────────────────────────────────────────────────┐");
		System.out.println("┃  1.재생/일시정지   2.정지    3.이전곡    4.다음곡                              ┃");
		System.out.println("┃                           " + volumeStr+"                                      ┃");
		System.out.println("┃  5.재생환경설정    6.볼륨 다운    7.볼륨 업   8.현재 재생목록 보기   0.뒤로    ┃");
		System.out.println("└────────────────────────────────────────────────────────────────────────────────┘");
	}
	
	/**
	 * 왜 this의 new Thread로 생성하지 않으면 interrupt가 작동하지 않을까? 
	 * -> 잘못 이해했다 쓰레드의 interrupt는 즉시 실행되는게 아니라 쓰레드가
	 * 일시 정지 상태가 되면  interruptException이 발생하고 이때 interrupt가 실행된다. 
	 * @author 문현석
	 * 2021-08-05
	 */
	public void start() {
		synchronized(this) {
			if(thread == null) {
				thread = new Thread(this);
			}
			thread.start();
		}
	}
	
	/**
	 * autoPlay와 preLoad메서드를 멀티쓰레드로 작동시킴
	 * 이유: 출력부분과 메인쓰레이드에서 실행되는 로직과 별개로 작동시키기 위해서
	 * @author 문현석
	 * 2021-08-05
	 */
	public void run() {
		while(thread.isAlive()) { //while문의 조건으로 isRun을 줘버렸는데 쓰레드가 일을 안한다.. 뭐지? 
			if(!isRun) break;
			autoPlay();
			safeIndexing(); //preLoad보다 우선순위가 빨라야 함.
			preLoad();
			
			// 마지막 입력시간이 흐른지 1.5초후에 메세지를 지움
			Long diff = new SimpleDateFormat("HH:mm:ss", Locale.KOREA).getCalendar().getTime().getTime() - beforeInputTime.getTime();
			if(autoClear) {
				if(diff > 1500) {
					clearMessage(true, 1L);
					autoClear = false;
				}
			}
			
			try {
				thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		isRun = true;
		thread = null;
	}
	
	/**
	 * 외부에서 쓰레드를 종료시킬때 호출하는 메서드입니다.
	 * 재생관련 멤버를 모두 초기화 해줍니다.
	 * @author 문현석
	 * 2021-08-06
	 */
	public void closeThread() {
		isRun = false;
		playIndex = 0;
		savePlayTime = 0;
		playList.clear();
		mpc.setState(INIT);
		mpc.closeThread();
	}

	public void message() {
		String bgStr ="|                                          |"; // 패널아트의 배경라인
		switch (messageState) {
		case NO_LIST_ERROR:
			System.out.println("     ┌──────────────────────────────────────────────────────────┐"); 
			System.out.println("     ┃                  재생할 리스트가 없습니다.               ┃");
			System.out.println("     └──────────────────────────────────────────────────────────┘");
			messageState = INIT;
			break;
		
		case INSTANT_PLAY:
			asc.panelArtU();
			System.out.println("		" + getBackgroundMacthString(bgStr, "< "+getShortString(musicName, 30)+" >곡을"));
			System.out.println("		|       재생목록에 추가후 재생합니다.      |");
			asc.panelArtD();
			musicName = "";
			break;
			
		case ALL_PLAY:
			asc.panelArtU();
			System.out.println("		|                  재생목록                |");
			System.out.println("		" + getBackgroundMacthString(bgStr, "< "+getShortString(listName, 30)+" >의"));
			System.out.println("		|           전체 곡들을 재생합니다.        |");
			asc.panelArtD();
			System.out.println("\n\n\n\n");
			listName = "";
			break;
			
		case ADD_MUSIC:
			asc.panelArtU();
			System.out.println("		|             현재 재생목록 끝에           |");
			System.out.println("		" + getBackgroundMacthString(bgStr, "< "+getShortString(musicName, 30)+" >곡을"));
			System.out.println("		|               추가 했습니다.             |");
			asc.panelArtD();
			musicName = "";
			break;
			
		case PLAY_ONCE:
			asc.panelArtU();
			System.out.println("		|               한곡 한번재생              |");
			System.out.println("		|              설정 되었습니다.            |");
			asc.panelArtD();
			break;
			
		case PLAY_ONCE_LOOP:
			asc.panelArtU();
			System.out.println("		|               한곡 반복재생              |");
			System.out.println("		|              설정 되었습니다.            |");
			asc.panelArtD();
			break;
			
		case PLAY_ALL_ONCE:
			asc.panelArtU();
			System.out.println("		|              리스트 한번재생             |");
			System.out.println("		|              설정 되었습니다.            |");
			asc.panelArtD();
			break;
			
		case PLAY_ALL_LOOP:
			asc.panelArtU();
			System.out.println("		|              리스트 반복재생             |");
			System.out.println("		|              설정 되었습니다.            |");
			asc.panelArtD();
			break;
		case PLAY_LIST_SHUFFLE:
			asc.panelArtU();
			System.out.println("		|         재생목록 순서를 섞었습니다.      |");
			asc.panelArtD();
			break;
			
		case PLAY_PUASE:
			asc.panelArtU();
			System.out.println("		|       재생중인 곡을 일지정지 했습니다.   |");
			asc.panelArtD();
			break;
			
		case PLAY_GO:
			asc.panelArtU();
			System.out.println("		|     일시정지중인 곡을 다시 재생합니다.   |");
			asc.panelArtD();
			break;
			
		case PLAY_STOP:
			asc.panelArtU();
			System.out.println("		|             재생을 정지합니다.           |");
			asc.panelArtD();
			break;
			
		case PLAY_PREV:
			asc.panelArtU();
			System.out.println("		|            이전곡을 선택합니다.          |");
			asc.panelArtD();
			break;
			
		case PLAY_NEXT:
			asc.panelArtU();
			System.out.println("		|            다음곡을 선택합니다.          |");
			asc.panelArtD();
			break;
			
		case PLAY_RESTART:
			asc.panelArtU();
			System.out.println("		|           곡을 다시 재생합니다.          |");
			asc.panelArtD();
			break;
			
		default:
			break;
		}
		
		// 출력된 메세지를 자동으로 지우게하는 flag true로 변경
		// 다른 쓰레드에서 메세지를 지움
		if(messageState > PLAY_PUASE && messageState <= PLAY_RESTART) autoClear = true;
		
		messageState = INIT;
	}

	public void clearMessage(boolean isClear, Long milliSec) {
		if(isClear) {
			new java.util.Timer().schedule(
					new java.util.TimerTask() {
						public void run() {
							manyLines(60);
							playInfoView();
							System.out.print("   >>>> ");
						}
					}
					,milliSec);
		}
	}
	
	@Override
	public void setVolumeValue(int vol) {
		// TODO Auto-generated method stub
		this.volGain = vol;
	}
	
	public int getVolumeValue() {
		return this.volGain;
	}

	public String getListName() {
		return listName;
	}

	public void setListName(String listName) {
		this.listName = listName;
	}
}
