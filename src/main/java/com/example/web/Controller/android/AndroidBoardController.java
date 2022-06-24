package com.example.web.Controller.android;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.web.config.auth.PrincipalDetail;
import com.example.web.dto.ResponseDto;
import com.example.web.model.Board;
import com.example.web.model.Reply;
import com.example.web.model.RoleType;
import com.example.web.model.User;
import com.example.web.repository.BoardRepository;
import com.example.web.repository.UserRepository;
import com.example.web.service.BoardService;
import com.example.web.service.UserService;

@RestController
public class AndroidBoardController {
	
	private static final Logger log = LoggerFactory.getLogger(AndroidBoardController.class);
	private static final Logger LOGGER = LoggerFactory.getLogger(AndroidBoardController.class);
	private static final int String = 0;
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BoardRepository boardRepository;

	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private BoardService boardSerivce;

	@PostMapping("/auth/android/boards")
	public Map boardList(){
		  System.out.println("안드로이드 게시판 접속");
		  
		  List<Board> boardlist = boardRepository.findAll();
		  Map result = new HashMap<String,Object>();
		  result.put("board", boardlist);
		return result;
	}
	
	@PostMapping("/auth/android/boardDetail")
	public Map boardDetailList (@RequestParam(required = false) int id){
		  System.out.println("안드로이드 게시판 접속");
		  System.out.println("title:"+id);
		  
		  Board board = boardRepository.findById(id).orElseThrow(()->{
				return new IllegalArgumentException("게시글찾기 실패");
			});
		  Map result = new HashMap<String,Object>();
		  result.put("board", board);
		  System.out.println("result:"+result);
		  return result;
	}
	
	@PostMapping("/auth/android/boardWrite")
	public Map androidBoardWrite(Board newBoard , String usernickname/* @AuthenticationPrincipal PrincipalDetail principal*/) { 
		System.out.println("안드로이드 게시판 글쓰기");
		
		Map result = new HashMap<String,Object>();
		
		System.out.println("board:"+newBoard);
		User user = userService.findByUsernicknameIdfind(usernickname);
		System.out.println("user:"+ user);
		newBoard.setUser(user);
		// 유저가 null이 아니면 게시글 저장을 해보겠다.
		if(user!=null) {
			boardRepository.save(newBoard); // 게시글 저장
			System.out.println("DB에 저장된 BOARD:"+newBoard);			
			List<Board> boardlist = boardRepository.findAll();			
			result.put("board", boardlist);
			System.out.println("result:"+result);
			return result;
		}else {
			
			return result;
		}
	}
	
	@PutMapping("/auth/android/boardModify")
	public Map androidBoardModify(int boardId, Board board /*, String usernickname*/ ){
		System.out.println("board update:"+ boardId);
		System.out.println("board update:"+ board.getTitle());
		System.out.println("board update:"+ board.getContent());
	//	System.out.println("수정하고자 하는 유저:"+usernickname);
		
		Map result = new HashMap<String,Object>();
	//	User user = userRepository.findByUsernickname(usernickname);
		boardSerivce.글수정하기(boardId,board);
		
		result.put("responseCode", "수정이 완료되었습니다.");
	//	List<Board> boardlist = boardRepository.findAll();			
	//	result.put("board", boardlist);
		System.out.println("result:"+result);
		return result;
	}
	
	@DeleteMapping("/auth/android/boardDelete")
	public Map androidBoardDelete(int id, String usernickname){
		
		Map result = new HashMap<String,Object>();
		
		System.out.println("삭제하고자 하는 게시글 번호:" +id+ "번");
		
		Board board = boardRepository.findById(id).orElseThrow(()->{
			return new IllegalArgumentException("게시글찾기 실패");
		});
		User user = userRepository.findByUsernickname(usernickname);
		System.out.println("게시글:"+board);
		String writer = board.getUser().getUsernickname();
		System.out.println("게시글의 작성자:" + writer);
		System.out.println("안드로이드에 로그인된자:" + user);
		
		if(user == null) {
			result.put("responseCode", "삭제할 권한이 없습니다.");
			return result;
		}else{
			boardSerivce.androidBoardDeleteService(id, user);
			result.put("responseCode",  "삭제가 완료되었습니다.");
			return result;
		}
	}
	
	@PostMapping("/auth/android/boardDetail/reply")
	public Map AndroidreplySave(int boardId, Reply reply, String usernickname) { // username, password, email
		
		Board board = boardRepository.findById(boardId).orElseThrow(()->{
			return new IllegalArgumentException("게시글찾기 실패");
		});
		User user = userRepository.findByUsernickname(usernickname);
		System.out.println(board);
		System.out.println(user);
		reply.setUser(user);
		reply.setBoard(board);
		System.out.println(reply);
		boardSerivce.댓글쓰기(user, boardId, reply);
		
		 Map result = new HashMap<String,Object>();
		// result.put("board", board);
		// System.out.println("result:"+result);
		return result; //console(resp) 해보면 resp에 200, data에 1 return 되는걸 확인
	}
	

}