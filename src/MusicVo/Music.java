package MusicVo;

import Mp3tagParser.Mp3tagParser;
import static MusicPlayerUtil.MusicPlayerCommon.*;

import java.io.Serializable;

public class Music implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String path;
	private Mp3tagParser tag;
	
	public Music(){}
	
	public Music(String path){
		this.setTag(new Mp3tagParser(path));
		this.setPath(path);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Mp3tagParser getTag() {
		return tag;
	}

	public void setTag(Mp3tagParser tag) {
		this.tag = tag;
	}
	
	/**
	 * @author 현석
	 * 인자로 입력한 TITLE ~ LENGTH 중 하나를
	 * 반환합니다.
	 * @param TITLE, ARTIST, ALBUM, GENRE, LENGTH
	 * @return 파라미터에 해당하는 tag의 데이터 스트링타입
	 */
	public String getTag(String tagType) {
		
		if(tagType.equals(TITLE)) {
			return this.getTag().getTitle();
		}
		else if(tagType.equals(ARTIST)) {
			return this.getTag().getArtist();
		}
		else if(tagType.equals(ALBUM)) {
			return this.getTag().getAlbum();
		}
		else if(tagType.equals(GENRE)) {
			return this.getTag().getGenre();
		}
		else if(tagType.equals(LENGTH_STR)) {
			return this.getTag().getLMStr() + this.getTag().getLSStr();
		}
		else {
			return "";
		}
	}
	
	/**
	 * @author 문현석
	 * 2021-08-02
	 * @return 노래길이 int타입 (1초 == int 1)
	 */
	public int getLength() {
		return this.getTag().getLength();
	}

	@Override
	public String toString() {
		
		//             getSlString인자: 문자열,  자를글자수, 글자 제한범위, 공백포함 길이 //
		String title = getSlString(this.getTag().getTitle(), 20, 32);
		String artist=  getSlString(this.getTag().getArtist(),20, 30);
		String album = getSlString(this.getTag().getAlbum(), 15, 24);
		String genre = getSlString(this.getTag().getGenre(), 12, 12);
		
		String str = 	title + artist + album + genre;
		return str;
	}
	
	

}
