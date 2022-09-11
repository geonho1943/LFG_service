package com.geonho1943.LFG.controller;

import com.geonho1943.LFG.model.UserModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {

    UserModel userModel = new UserModel();

    @GetMapping("/test")
    @ResponseBody
    public String test(){
        return "test_ok";
    }


    @GetMapping("/user_lookup")
    @ResponseBody
    public String user_lookup(){
        return userModel.lookup();
    }
    @GetMapping("/user_test")
    @ResponseBody
    public String url_test(@RequestParam("test")String test){
        UserModel userModel = new UserModel();
        return userModel.test(test);
    }


    @GetMapping("/user_join")
    public String user_join(@RequestParam("name")String name, @RequestParam("pw")String pw){
        UserModel userModel = new UserModel();
        return userModel.join(name,pw);
    }

}
