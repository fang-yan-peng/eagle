package eagle.jfaster.org.controller;

import eagle.jfaster.org.util.AuthenticatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author  fangyanpeng
 */
@Controller
public class ConsoleIndex extends AbstractErrorController {

    @Autowired
    public ConsoleIndex(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    @RequestMapping("/")
    public String index(){
        return "redirect:/index.html";
    }

    @ResponseBody
    @RequestMapping("/error")
    public String error(HttpServletRequest request, HttpServletResponse response){

        HttpStatus status = super.getStatus(request);

        if (status == HttpStatus.NOT_FOUND){
            return "wrong";
        }

        if (status == HttpStatus.FORBIDDEN || status == HttpStatus.UNAUTHORIZED){
            AuthenticatUtil.needAuthenticate(response);
            return "";
        }

        return "wrong";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
