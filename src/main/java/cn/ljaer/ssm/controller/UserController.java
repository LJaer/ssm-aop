package cn.ljaer.ssm.controller;


import cn.ljaer.ssm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by jaer on 2016/9/12.
 */

@Controller
public class UserController {

    @Autowired
    private UserService userService;
    
    @RequestMapping("selectByPrimaryKey")
    public void selectByPrimaryKey(@RequestParam("id") int id, HttpServletResponse response) throws  Exception{
    	response.getWriter().write(userService.selectByPrimaryKey(id).toString());
    }
}
