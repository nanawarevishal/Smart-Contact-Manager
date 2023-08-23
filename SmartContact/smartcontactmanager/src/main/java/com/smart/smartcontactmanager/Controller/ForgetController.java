package com.smart.smartcontactmanager.Controller;

import java.security.Principal;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.smartcontactmanager.Entities.User;
import com.smart.smartcontactmanager.Helper.Message;
import com.smart.smartcontactmanager.Service.EmailService;
import com.smart.smartcontactmanager.duo.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgetController {
    
    
    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired 
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    
    // email id open handler
    
    @RequestMapping("/forget")
    public String openEmailForm(){
        return "forget_email_form";
    }
    
    @PostMapping("/send-otp")
    public String sendOTP(@RequestParam("email")String email,Model model,HttpSession session){
        
        // System.out.println("Email: "+email);

        // generating otp

        Random random = new Random();
        int OTP = 100_000 + random.nextInt(900_000); // Generate a random 6-digit number
        

        String subject = "OTP from iCoderSCM";
        String message = "<h1>OTP: "+OTP+"</h1> "+"\n Please verify your OTP";
        String to = email;

        User user = this.userRepository.getUserByUserName(email);

        boolean flag = this.emailService.sendEmail(subject,message , to);

        if(flag){

            model.addAttribute("message", new Message("OTP Sent on your Email", "alert-success"));
            session.setAttribute("email", email);
            session.setAttribute("myOTP", OTP);
        }

        if(!flag || user==null){

            model.addAttribute("message", new Message("Check your mail ID....! Your does not exist...!", "alert-danger"));
            return "forget_email_form";
        }

        return "verify_otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOTP(@RequestParam("OTP")String OTP,HttpSession session,Model model){

        try {
            
            int userOTP = Integer.parseInt(OTP);
            int myotp =(int) session.getAttribute("myOTP");
            String email = (String) session.getAttribute("email");

            // System.out.println("Otp: "+myotp);
            // System.out.println("Email: "+email);

            if(myotp == userOTP){

                return "password_change_form";
            }
            else{

                model.addAttribute("message", new Message("You have entered wrong OTP", "alert-danger"));
                return "verify_otp";
            }


        } catch (RuntimeException e) {
            
            model.addAttribute("message", new Message("You have entered wrong OTP", "alert-danger"));
            return "verify_otp";
        }
    }

    @PostMapping("/save-password")
    public String changePassword(@RequestParam("password")String password,Principal principal,HttpSession session){

        User user = this.userRepository.getUserByUserName((String)session.getAttribute("email"));

        user.setPassword(bCryptPasswordEncoder.encode(password));
        this.userRepository.save(user);

        return "";
    }
}
