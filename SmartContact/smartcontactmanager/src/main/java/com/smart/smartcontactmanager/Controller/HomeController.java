package com.smart.smartcontactmanager.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smart.smartcontactmanager.Entities.User;
import com.smart.smartcontactmanager.Helper.Message;
import com.smart.smartcontactmanager.duo.UserRepository;

import jakarta.validation.Valid;


@Controller
public class HomeController {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    
    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/")
    public String home(){
        return "home";
    }

    @RequestMapping(value = "/about")
    public String about(){
        return "about";
    }

    @RequestMapping(value = "/signup")
    public String signup(Model model){

        model.addAttribute("title","Register-Smart Contact Manager");
        model.addAttribute("user",new User());
        return "signup";
    }

    @RequestMapping(value = "/do_register",method = RequestMethod.POST)
    public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result1,@RequestParam(value = "agreement",defaultValue = "false")boolean agreement, Model model){

       try {

            if(!agreement){

                System.out.println("You have not agree the terms and conditions");
                throw new Exception("You have not agree the terms and conditions");
            }
            
            if(result1.hasErrors()){
                
                model.addAttribute("user", user);
                System.out.println("Error: "+result1.toString());
                return "signup";
            }
            
            
            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("default.png");
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

            User result = this.userRepository.save(user);

            System.out.println("Agreement: "+agreement);
            System.out.println("User: "+user);


            model.addAttribute("user",new User());
            model.addAttribute("message", new Message("Successfully Registered....!", "alert-success"));
            return "signup";
       } 

       catch (Exception e) {

            e.printStackTrace();
            model.addAttribute("user",user);
            System.out.println("catch block");
            model.addAttribute("message", new Message("Something Went Wrong...!", "alert-danger"));
            return "signup";
       }
        
    }


    @RequestMapping(value = "/signin")
    public String customLogin(Model model){
        model.addAttribute("title","Login Page");
        return "login";
    }
}
