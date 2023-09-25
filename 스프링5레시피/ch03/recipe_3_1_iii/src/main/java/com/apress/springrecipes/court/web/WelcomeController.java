package com.apress.springrecipes.court.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
//컨트롤러를 /welcome URL에 바인딩합니다.
//초기에는 기본 GET 메서드가 반환한 이름으로 뷰를 해석합니다.
@RequestMapping("/welcome")
public class WelcomeController {

    // 컨트롤러는 이름과 무관하게 항상 최초 호출할 기본 GET 메서드를 찾습니다.
    // 이 예제서는 알아보기 쉽게 하고자 welcome이라고 메서드를 명명했습니다.
    @RequestMapping(method = RequestMethod.GET)
    // 이 메서드는 Model 객체를 인수로 받아 여기에 오늘 날짜를 설정합니다.
    // (참고: Model은 자바 5부터 추가된 인터페이스로, 예전에는 ModelMap나 ModelAndView를 썼습니다)
    // NOTE: Model is a Java 5 interface -- newer than the older ModelMap and still older ModelAndView
    public String welcome(Model model) {
        Date today = new Date();
        // 오늘 날짜를 모델에 추가해서 뷰 화면에서 표시합니다.
        model.addAttribute("today", today);
        // "welcome" 뷰를 반환하면, 뷰 해석기가 /WEB-INF/jsp/welcome.jsp로 뷰를 매핑합니다.
        return "welcome";
    }

}
