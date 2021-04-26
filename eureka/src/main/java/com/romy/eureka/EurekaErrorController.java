package com.romy.eureka;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class EurekaErrorController implements ErrorController {

	@RequestMapping(value = "/error")
	public ModelAndView handleError(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView mv = new ModelAndView();
		mv.setViewName("error");
		
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		String errorCode = request.getParameter("code");
		if(status == null && errorCode != null) {
			
			mv.addObject("code", errorCode);
			mv.addObject("message", "");
			
		} else {
			Integer code = Integer.valueOf(status.toString());
			HttpStatus httpStatus = HttpStatus.valueOf(code);
			
			mv.addObject("code", code);
			mv.addObject("message", httpStatus.getReasonPhrase());
		}

		return mv;
	}
	
	@Override
	public String getErrorPath() {
		return null;
	}

}
