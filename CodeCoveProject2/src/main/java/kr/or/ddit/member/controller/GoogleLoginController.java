package kr.or.ddit.member.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import kr.or.ddit.ServiceResult;
import kr.or.ddit.member.service.IMemberService;
import kr.or.ddit.member.vo.MemberVO;

@Controller
public class GoogleLoginController {

	@Inject
	private IMemberService memService;

	@RequestMapping(value = "/googleLogin.do")
	public String googleLogin(@RequestParam("code") String code, ModelMap model, HttpSession session, HttpServletRequest req) {
		String goPage = "";
		String access_token = getAccessToken(code);
		Map<String, Object> userInfo = getUserInfo(access_token);
		System.out.println("LOGIN CONTROLLER : " + userInfo);
		MemberVO member = new MemberVO();

		if (userInfo.get("email") != null) {
			ServiceResult result = memService.idCheck(userInfo.get("email").toString());
			if (result.equals(ServiceResult.EXIST)) {
				// memberVO.setMem_snsflag("google");
				// memberVO.setMemProfile(userInfo.get("picture").toString());
				System.out.println("회원정보가 존재 : " + result);

				member.setMemId(userInfo.get("email").toString());
				MemberVO memberVO = memService.loginCheck(member);
				if (memberVO != null) { // 로그인 성공, 세션처리
					session = req.getSession();
					session.setAttribute("SessionInfo", memberVO);
					goPage = "redirect:/coco/loginSuccess";
				}
				
//				goPage = "member/login";
			} else {
				member.setMemMail(userInfo.get("email").toString());
				member.setMemProfile(userInfo.get("picture").toString());
				member.setMemId(userInfo.get("email").toString());
				model.addAttribute("message", "회원정보가 존재하지 않습니다. 가입 후 이용해주세요.");
				model.addAttribute("member", member);
				model.addAttribute("status", "g");
				System.out.println("회원정보가 존재하지 않습니다 : " + result);
				goPage = "member/register";
			}
		} else {
			model.addAttribute("message", "구글 계정이 존재하지 않는것 같습니다. 확인부탁드립니다.");
			goPage = "login/sns";
		}
		return goPage;
	}

	public String getAccessToken(String authorize_code) {
		String access_token = "";
		String basicUrl = "https://accounts.google.com/o/oauth2/token";

		try {
			URL url = new URL(basicUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
			StringBuilder sb = new StringBuilder();
			sb.append("grant_type=authorization_code");
			sb.append("&client_id=636511148424-mghktber42ms2j1nu54v79m3r9de7cnk.apps.googleusercontent.com");
			sb.append("&client_secret=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");
			sb.append("&redirect_uri=http://localhost/googleLogin.do");
			sb.append("&code=" + authorize_code);
			bw.write(sb.toString());
			bw.flush();

			int resCode = conn.getResponseCode();
			System.out.println("SUCCESS CODE : " + resCode);

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = "";
			String result = "";

			while ((line = br.readLine()) != null) {
				result += line;
			}
			System.out.println("RESPONSE BODY : " + result);

			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(result);
			access_token = element.getAsJsonObject().get("access_token").getAsString();
			System.out.println("access_token : " + access_token);

			br.close();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return access_token;
	}

	public Map<String, Object> getUserInfo(String access_token) {
		Map<String, Object> userInfo = new HashMap<String, Object>();
		String basicUrl = "https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + access_token;
		try {
			URL url = new URL(basicUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			int resCode = conn.getResponseCode();
			System.out.println("RESPONSE CODE : " + resCode);

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = "";
			String result = "";

			while ((line = br.readLine()) != null) {
				result += line;
			}
			System.out.println("RESPONSE BODY : " + result);

			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(result);
			String email = element.getAsJsonObject().get("email").getAsString();
			String picture = element.getAsJsonObject().get("picture").getAsString();
			userInfo.put("email", email);
			userInfo.put("picture", picture);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return userInfo;
	}
}
