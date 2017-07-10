package eu.europa.csp.vcbadmin.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

/**
 * Basic Controller which is called for unhandled errors
 */
@Controller
public class AppErrorController implements ErrorController {

	/**
	 * Error Attributes in the Application
	 */
	private ErrorAttributes errorAttributes;

	private final static String ERROR_PATH = "/error";

	/**
	 * Controller for the Error Controller
	 * 
	 * @param errorAttributes
	 */
	public AppErrorController(ErrorAttributes errorAttributes) {
		this.errorAttributes = errorAttributes;
	}

	/**
	 * Supports the HTML Error View
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = ERROR_PATH, produces = "text/html")
	public ModelAndView errorHtml(HttpServletRequest request) {
		Map<String, Object> error_attributes = getErrorAttributes(request, false);
		if (error_attributes.get("status") != null) {
			if ((Integer) (error_attributes.get("status")) == 404) {
				return new ModelAndView("/errors/404", error_attributes);
			} else if ((Integer) (error_attributes.get("status")) == 400) {
				return new ModelAndView("/errors/400", error_attributes);
			} else if ((Integer) (error_attributes.get("status")) == 500) {
				return new ModelAndView("/errors/500", error_attributes);
			} else if ((Integer) (error_attributes.get("status")) == 401) {
				return new ModelAndView("/errors/401", error_attributes);
			}
		}
		return new ModelAndView("/errors/error", error_attributes);
	}

	/**
	 * Supports other formats like JSON, XML
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = ERROR_PATH)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
		Map<String, Object> body = getErrorAttributes(request, getTraceParameter(request));
		HttpStatus status = getStatus(request);
		return new ResponseEntity<Map<String, Object>>(body, status);
	}

	/**
	 * Returns the path of the error page.
	 *
	 * @return the error path
	 */
	@Override
	public String getErrorPath() {
		return ERROR_PATH;
	}

	private boolean getTraceParameter(HttpServletRequest request) {
		String parameter = request.getParameter("trace");
		if (parameter == null) {
			return false;
		}
		return !"false".equals(parameter.toLowerCase());
	}

	private Map<String, Object> getErrorAttributes(HttpServletRequest request, boolean includeStackTrace) {
		RequestAttributes requestAttributes = new ServletRequestAttributes(request);
		return this.errorAttributes.getErrorAttributes(requestAttributes, includeStackTrace);
	}

	private HttpStatus getStatus(HttpServletRequest request) {
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		if (statusCode != null) {
			try {
				return HttpStatus.valueOf(statusCode);
			} catch (Exception ex) {
			}
		}
		return HttpStatus.INTERNAL_SERVER_ERROR;
	}
}