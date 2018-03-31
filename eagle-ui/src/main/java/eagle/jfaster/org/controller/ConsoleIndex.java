/*
 * Copyright 2017 eagle.jfaster.org.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package eagle.jfaster.org.controller;

import eagle.jfaster.org.util.AuthenticatUtil;

import org.assertj.core.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author fangyanpeng
 */
@Controller
public class ConsoleIndex extends AbstractErrorController {

    @Autowired
    public ConsoleIndex(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    @RequestMapping("/")
    public String index() {
        return "redirect:/index.html";
    }

    @ResponseBody
    @RequestMapping("/error")
    public String error(HttpServletRequest request, HttpServletResponse response) {

        HttpStatus status = super.getStatus(request);

        if (status == HttpStatus.NOT_FOUND) {
            return "wrong";
        }

        if (status == HttpStatus.FORBIDDEN || status == HttpStatus.UNAUTHORIZED) {
            AuthenticatUtil.needAuthenticate(response);
            return "";
        }

        return "wrong";
    }

    @ResponseBody
    @RequestMapping(value = "/user", method = RequestMethod.HEAD)
    public void getLoginUser(HttpServletResponse response) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userName = userDetails.getUsername();
        if (Strings.isNullOrEmpty(userName)) {
            AuthenticatUtil.needAuthenticate(response);
            return;
        }
        AuthenticatUtil.authenticateSuccess(response, userName);
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
