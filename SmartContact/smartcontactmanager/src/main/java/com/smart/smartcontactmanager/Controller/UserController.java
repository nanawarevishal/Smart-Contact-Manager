package com.smart.smartcontactmanager.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;



import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.smartcontactmanager.Entities.Contact;
import com.smart.smartcontactmanager.Entities.User;
import com.smart.smartcontactmanager.Helper.Message;
import com.smart.smartcontactmanager.duo.ContactRepository;
import com.smart.smartcontactmanager.duo.UserRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    // Method to add common data
    @ModelAttribute   
    public void addCommonData(Model model, Principal principal){
        String userName = principal.getName();
        User user = userRepository.getUserByUserName(userName);
        model.addAttribute("user", user);

    }

    // Dashboard Home
    @RequestMapping("/index")
    public String dashBoard(Model model,Principal principal){
        String userName = principal.getName();
        // System.out.println("UserName: "+userName);


        // Get the user using username
        User user = userRepository.getUserByUserName(userName);
        model.addAttribute("user", user);

        model.addAttribute("title",user.getName()+" Dashboard");

        return "normal/user_dashboard";
    }

    // Open add form handler

    @GetMapping("/add-contact")
    public String openAddContactForm(Model model){
        model.addAttribute("title","Add Contact");
        model.addAttribute("contact", new Contact());

        return "normal/add_contact_form";
    }

    // processing add contact form


    @RequestMapping(value = "/process-contact",method = RequestMethod.POST)
    public String saveContact(@Valid @ModelAttribute("contact") Contact contact,BindingResult result,@RequestParam("image") MultipartFile file, Model model,Principal principal){

        // try {

        //     if(result.hasErrors()){
        //         model.addAttribute("contact", contact);
        //         System.out.println("Error: "+result.toString());
        //         return "normal/add_contact_form";
        //     }

        //     Contact contact2 = this.contactRepository.save(contact);
        //     System.out.println("Contact: "+contact2);


        //     model.addAttribute("contact",new Contact());
        //     model.addAttribute("message", new Message("Successfully Saved Contact....!", "alert-success"));
        //     return "normal/add_contact_form";

        // } 

        // catch (Exception e) {
        //     e.printStackTrace();
        //     model.addAttribute("contact",contact);
        //     model.addAttribute("message", new Message("Something Went Wrong...!", "alert-danger"));
        //     return "normal/add_contact_form";
        // }
            try { 

                String username = principal.getName();
                User user = this.userRepository.getUserByUserName(username);

                if(file.isEmpty()){
                    System.out.println("File is empty");
                }
                else{

                    contact.setImage(file.getOriginalFilename());
                    File savefile = new ClassPathResource("static/img").getFile();

                    Path path =  Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                    System.out.println("Image is uploaded");

                }

                contact.setUser(user);
                user.getContacts().add(contact);

                this.userRepository.save(user);

                System.out.println("Data"+contact);
                System.out.println("Added to data");
                model.addAttribute("message", new Message("Successfully Saved Contact....!", "alert-success"));
                return "normal/add_contact_form"; 
            } 
            
            catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("message", new Message("Something Went wrong....!","alert-danger")); 
                return "normal/add_contact_form"; 
            }
       
    }
}
