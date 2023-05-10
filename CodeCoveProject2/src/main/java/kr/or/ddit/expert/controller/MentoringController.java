package kr.or.ddit.expert.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.or.ddit.common.ReportVO;
import kr.or.ddit.expert.service.IEprodService;
import kr.or.ddit.expert.service.ITemplateReviewService;
import kr.or.ddit.expert.vo.ExpHeartVO;
import kr.or.ddit.expert.vo.ExpertProdVO;
import kr.or.ddit.expert.vo.MentoProfileVO;
import kr.or.ddit.expert.vo.MyExpertVO;
import kr.or.ddit.expert.vo.TemplateReviewVO;
import kr.or.ddit.member.service.IMemberService;
import kr.or.ddit.member.vo.MemberVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/coco/mentoring")
public class MentoringController {

	@Inject
	private IMemberService memberService;
	
	@Inject
	private IEprodService prodService;
	
	@Inject
	private ITemplateReviewService reviewService;
	
	@Resource(name="uploadPath")
	private String resourcePath;

	 	//멘토링 인서트 화면 출력
		@GetMapping("/mentoringInsert")
		public String mentoringInserttForm( HttpServletRequest req, RedirectAttributes ra, Model model){
			
			//로그인세션넘기기
			HttpSession session = req.getSession();
			MemberVO sessionMember = (MemberVO) session.getAttribute("SessionInfo");		
			MemberVO member = memberService.selectMember(sessionMember);
			ExpertProdVO expertProdVO = new ExpertProdVO();
			expertProdVO.setMemId(sessionMember.getMemId());
			model.addAttribute("member", member);
			
			return "solutions/mentoringInsertForm";
		}
		
		//멘토링 인서트 프로세스
		@PostMapping("/mentoringInsert")
		public String mentoringInsert(ExpertProdVO expertProdVO, Model model) throws Exception{
			
			model.addAttribute("expertProdVO", expertProdVO);
			prodService.templateInsert(expertProdVO);
			return  "redirect:/coco/mentoring/category";
		}
		
		
		@GetMapping("/mentoringDetail")
		public String mentoringDetail(String eprodNum, Model model, 
									HttpServletRequest req) {
			//디테일화면 출력
			ExpertProdVO expertProd = prodService.templateDetail(eprodNum);
			log.debug("expertProd에 뭐가 들었어?" + expertProd);
			
			//리뷰 리스트 출력
			List<TemplateReviewVO> templateReviewVO = reviewService.templateReviewList(eprodNum);//템플릿리뷰
			log.debug("templateReviewVO에 멤버프로필들어왔니" + templateReviewVO);
			
			//멘토 프로필 출력
			String expertId =  expertProd.getExpertId();
			MentoProfileVO mentoProfileVO =  prodService.mentoProfile(expertId);
			log.debug("MentoProfileVO에 뭐 들었어?" + mentoProfileVO);

			
			
			//로그인세션넘기기
			HttpSession session = req.getSession();
			MemberVO sessionMember = (MemberVO) session.getAttribute("SessionInfo");		
			MemberVO member = memberService.selectMember(sessionMember);
			ExpertProdVO expertProdVO = new ExpertProdVO();
			expertProdVO.setMemId(sessionMember.getMemId());
			expertProdVO.setMemNick(sessionMember.getMemNick());

			//멤버세션넘기기
			model.addAttribute("member", member);
			//게시글 디테일
			model.addAttribute("expertProd", expertProd);
			//리뷰 리스트
			model.addAttribute("templateReviewVO", templateReviewVO);
			log.debug("templateReviewVO에 뭐 들었어?"+templateReviewVO);
			//멘토 프로필
			model.addAttribute("mentoProfileVO", mentoProfileVO);


			
			return "solutions/mentoringDetail";
		}
		
		
		//구매내역 아작스 인서트 하는 메소드
		@PostMapping(value="/templateDetail",produces = "application/json;charset=utf-8")
		@ResponseBody  // 직접응답, view로 돌리지 않음, 아작스용은 이게 붙어 있어야 함
		public MyExpertVO templatePurchase(@RequestBody Map<String,String> jsonMap) {
			
			MyExpertVO myExpertVO =  new MyExpertVO();
			myExpertVO.setEprodNum(jsonMap.get("eprodNum"));
			myExpertVO.setBuyerId(jsonMap.get("buyerId"));
			myExpertVO.setProgressCode("완료");
			myExpertVO.setEprodPrice(jsonMap.get("eprodPrice"));
			myExpertVO.setEprodName(jsonMap.get("eprodName"));
			myExpertVO.setEprodSummary(jsonMap.get("eprodSummary"));
			myExpertVO.setEprodGrade(jsonMap.get("eprodGrade"));
			
			log.debug("myExpertVO구매" + myExpertVO);
			
			prodService.buyInsert(myExpertVO);
			
			log.debug("jueun babo" + myExpertVO);
			log.debug("넘어온 값 체크: {}", jsonMap);
			return myExpertVO;
			
		}
		
		//템플릿 결제 완료 후 1.결제기록인서트, 2.구매내역 중 결제완료여부 업데이트, 3. 결제완료디테일출력
		@PostMapping("/mentoringReceipt")
		public String mentoringReceipt(MyExpertVO myExpertVO, Model model, RedirectAttributes ra) {

			myExpertVO = prodService.paySuccessInsert(myExpertVO);
			
			model.addAttribute("myExpertVO", myExpertVO);

			log.debug("myEprod : "+myExpertVO.getMyEprod());
			log.debug("eprodPrice: " + myExpertVO.getEprodPrice());
			log.debug("myExpertVO안에 뭐가 들었나요 : " + myExpertVO);
			
			return "/solutions/mentoringReceipt";
			
		}
		



		
//		템플릿 리뷰 인서트용 ajax
		@PostMapping(value="/templateReview",produces = "application/json;charset=utf-8")
		@ResponseBody  // 직접응답, view로 돌리지 않음, 아작스용은 이게 붙어 있어야 함	
		public TemplateReviewVO templateReview(@RequestBody Map<String,String> jsonMap,
									  HttpServletRequest req, Model model
									  ,String eprodNum) {

			System.out.println("확인 >> " + jsonMap.get("reviewContent"));
//			1.로그인세션에 담긴 value를 jsp로 넘기기 여기서는 id랑 닉네임
			HttpSession session = req.getSession();
			MemberVO sessionMember = (MemberVO) session.getAttribute("SessionInfo");		
			MemberVO member = memberService.selectMember(sessionMember);
			ExpertProdVO expertProdVO = new ExpertProdVO();
			expertProdVO.setMemId(sessionMember.getMemId());//세션에서 가져온 아이디 담기
			expertProdVO.setMemNick(sessionMember.getMemNick());//세션에서 가져온 닉네임 담기
			model.addAttribute("member", member);//모델로 닉네임 jsp로 넘겼음
			
			log.debug("나오겡?");
			
//			3.이 부분에 ajax로 넘어온 결과값을 받아서 담아서 인서트 함
			TemplateReviewVO templateReviewVO = new TemplateReviewVO();
			templateReviewVO.setEprodNum(jsonMap.get("eprodNum"));
			templateReviewVO.setReviewContent(jsonMap.get("reviewContent"));
			templateReviewVO.setReviewWriter(jsonMap.get("reviewWriter"));
			templateReviewVO.setReviewStar(Integer.parseInt(jsonMap.get("reviewStar")) );
			reviewService.reviewInsert(templateReviewVO);
			
			
			
			log.debug("templateReviewVO안에 뭐있는데:" +templateReviewVO);
			return templateReviewVO;
		}	
		
		
	//템플릿 찜하기 인서트용 ajax
		@PostMapping(value="/templateHeartInsert",produces = "application/json;charset=utf-8")
		@ResponseBody  // 직접응답, view로 돌리지 않음, 아작스용은 이게 붙어 있어야 함	
		public ExpHeartVO templateHeartInsert(@RequestBody Map<String,String> jsonMap){
//			1.ajax 결과값 insert용
			ExpHeartVO expHeartVO = new ExpHeartVO();
			expHeartVO.setEprodNum(jsonMap.get("eprodNum"));
			expHeartVO.setMemId(jsonMap.get("memId"));
			reviewService.templateHeartInsert(expHeartVO);
		
		return expHeartVO;
		}
		
		
	//템플릿 찜하기 딜리트용 ajax	
		@PostMapping(value="/templateHeartDelete",produces = "application/json;charset=utf-8")
		@ResponseBody  // 직접응답, view로 돌리지 않음, 아작스용은 이게 붙어 있어야 함	
		public ExpHeartVO templateHeartDelete(@RequestBody Map<String,String> jsonMap) {
			
			log.debug("jsonMap: "+jsonMap.get("eprodNum"));
			
			
//			1.ajax 결과값 delete용
			ExpHeartVO expHeartVO = new ExpHeartVO();
			expHeartVO.setEprodNum(jsonMap.get("eprodNum"));
			expHeartVO.setMemId(jsonMap.get("memId"));
			reviewService.templateHeartDelete(expHeartVO);
			
			log.debug("베트맨리턴즈 확인 필요!");
			return expHeartVO;
			
		}
		
		
	//템플릿에 하트 찍었는데 유무 확인용	
		@PostMapping(value="/isHeart",produces = "application/json;charset=utf-8")
		@ResponseBody  // 직접응답, view로 돌리지 않음, 아작스용은 이게 붙어 있어야 함	
		public ExpHeartVO isHeart(@RequestBody Map<String,String> jsonMap) {
			
			log.debug("jsonMap: "+jsonMap.get("eprodNum"));
			
			
//			1.ajax 결과값 하트확인용
			ExpHeartVO expHeartVO = new ExpHeartVO();
			expHeartVO.setEprodNum(jsonMap.get("eprodNum"));
			expHeartVO.setMemId(jsonMap.get("memId"));
			expHeartVO.setHeartCheck(reviewService.isHeart(expHeartVO));
			
			return expHeartVO;
		}
		

		
		
		
	//템플릿에 리뷰 달았는지 유무 확인용
		@PostMapping(value="/isReview",produces = "application/json;charset=utf-8")
		@ResponseBody  // 직접응답, view로 돌리지 않음, 아작스용은 이게 붙어 있어야 함	
		public TemplateReviewVO isReview(@RequestBody Map<String,String> jsonMap) {
			
//			1.ajax 결과값 리뷰확인용
			TemplateReviewVO templateReviewVO =  new TemplateReviewVO();
			templateReviewVO.setEprodNum(jsonMap.get("eprodNum"));
			templateReviewVO.setReviewWriter(jsonMap.get("reviewWriter"));
			templateReviewVO.setReviewCheck(reviewService.isReview(templateReviewVO));
			log.debug("templateReviewVO에 멤버 프로필 들었어?"+templateReviewVO);
			
			return templateReviewVO;
		}

	//신고하기
		@PostMapping("/templateReportInsert")
		public String templateReportInsert(ReportVO reportVO, Model model, String eprodNum) {
			model.addAttribute("reportVO", reportVO);
			//신고하기 인서트
			reviewService.reportInsert(reportVO);
			log.debug("reportVO안에 뭐가 들었어?"+ reportVO);
			
			
			return  "redirect:/coco/solution/templateDetail?eprodNum="+eprodNum;
			
		}
		
	//신고하기 확인용 ajax	
		@PostMapping(value="/isReport",produces = "application/json;charset=utf-8")
		@ResponseBody  // 직접응답, view로 돌리지 않음, 아작스용은 이게 붙어 있어야 함	
		public ReportVO isReport(@RequestBody Map<String,String> jsonMap) {
			
//			1.ajax 결과값 신고확인용
			ReportVO reportVO =  new ReportVO();
			reportVO.setTargetId(jsonMap.get("eprodNum"));
			reportVO.setMemId(jsonMap.get("memId"));
			log.debug("reportVO아작스에 뭐가 들었는데?"+reportVO);
			reportVO.setIsReport(reviewService.isReport(reportVO));
			
			return reportVO;
		}
		
	//신고하기 확인용 ajax	
		@PostMapping(value="/starAvgAjax",produces = "application/json;charset=utf-8")
		@ResponseBody  // 직접응답, view로 돌리지 않음, 아작스용은 이게 붙어 있어야 함	
		public ExpertProdVO starAvgAjax(@RequestBody Map<String,String> jsonMap) {
			
//			1.ajax 결과값 별점평균 확인용
			ExpertProdVO expertProdVO = new ExpertProdVO();
			expertProdVO.setEprodNum(jsonMap.get("eprodNum"));
			expertProdVO.setStarAvg(reviewService.starAvgAjax(expertProdVO));
			
			return expertProdVO;
		}
		
	//리뷰 카운트용 ajax	
		@PostMapping(value="/countReview",produces = "application/json;charset=utf-8")
		@ResponseBody  // 직접응답, view로 돌리지 않음, 아작스용은 이게 붙어 있어야 함	
		public TemplateReviewVO countReview(@RequestBody Map<String,String> jsonMap) {
			
//			1.ajax 결과값 별점평균 확인용
			TemplateReviewVO templateReviewVO =  new TemplateReviewVO();
			templateReviewVO.setEprodNum(jsonMap.get("eprodNum"));
			templateReviewVO.setCountReview(reviewService.countReview(templateReviewVO));
			
			return templateReviewVO;
		}

		
		
		
		
	
}






















