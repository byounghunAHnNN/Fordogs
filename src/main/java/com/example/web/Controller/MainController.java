package com.example.web.Controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.web.service.BoardService;

@Controller
public class MainController {

	private static final Logger log = LoggerFactory.getLogger(MainController.class);
	private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

	@Autowired
	private BoardService boardService;

	@RequestMapping("/")
	public String index() {
		System.out.println("메인 화면 진입");
		return "index";
	}

	@GetMapping({"/board"})
	public String board(Model model,
	 @PageableDefault(size=4,sort="id",direction=Sort.Direction.DESC) Pageable pageable){

		log.info("게시판 등록 페이지 진입");
		int startPage = ((pageable.getPageNumber()-1) / 10) * 10 + 1;
        pageable.getPageSize();
        int endPage = startPage + 10 - 1  > pageable.getPageSize() ? pageable.getPageSize() : startPage + 10 - 1;
        model.addAttribute("startPageNo", startPage);
        model.addAttribute("endPageNo", endPage);


		model.addAttribute("boards", boardService.글목록(pageable));
		return "/board/boardList"; //viewResolver 작동! 위 모델의 정보를 가지고 홈으로 간
	}

	@GetMapping("/board/{id}")
	public String findById(@PathVariable int id, Model model) {
		model.addAttribute("board", boardService.글상세보기(id));
		return "board/detail";
	}

	//user권한 필요한 게시판..
	@GetMapping("/board/writeForm")
	public String saveForm() {

		return "board/writeForm";
	}
	
	@GetMapping("/board/{id}/modifyForm")
	public String updateForm(@PathVariable int id, Model model) {
		model.addAttribute("board",boardService.글상세보기(id));
		return "board/modifyForm";
	}

}
