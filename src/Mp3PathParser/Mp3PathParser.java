package Mp3PathParser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import static MusicPlayerUtil.MusicPlayerCommon.*;
/**지정된 경로에 있는 mp3파일의 경로를 찾아 저장, 반환할수 있습니다.
 * @author 현석
 * 2021-07-29
 */
public class Mp3PathParser {
	
	private List<String> mp3Path;
	
	public Mp3PathParser() {}
	
	public Mp3PathParser(String path) {
		File directory = new File(path);
		
		/**
		 * 음악 폴더 경로를 잘못 입력받았을때  
		 * 다시 경로를 받게하거나 프로그램을 종료할수있게 합니다.
		 * @author 문현석
		 * 2021-08-02
		 */
		while(true) {
			try {
				this.mp3Path = new ArrayList<>();
				this.allPathParse(directory);
				break;
			} catch (Exception e) {
				String input = nextLine("음악폴더 경로를 정확히 입력해주세요, 0.프로그램 종료.");
				if(input.equals("0"))System.exit(0);
				
				this.mp3Path = null;
				directory = null;
				directory = new File(input);
			}
		}
	}
	
	/**
	 * @author 현석
	 * 음악폴더의 경로를 인자로 받아 해당 폴더의 파일경로를 
	 * list에 저장합니다
	 * mp3확장자인 파일만 추가합니다
	 * @param path(음악폴더 경로)
	 */
	public void allPathParse(File directory) {
		for(File file : directory.listFiles()) {
			if(!file.isDirectory()) {
				if(file.getName().toUpperCase().contains(".mp3".toUpperCase()))
					mp3Path.add(file.getPath());
			}
			else {
				allPathParse(file);
			}
		}
	}
	
	/**
	 * @author 현석
	 * 2021-07-29
	 * @return 생성할때 만들어진 음악파일경로 리스트를 반환합니다
	 */
	public List<String> getPathList(){
		return this.mp3Path;
	}
}
