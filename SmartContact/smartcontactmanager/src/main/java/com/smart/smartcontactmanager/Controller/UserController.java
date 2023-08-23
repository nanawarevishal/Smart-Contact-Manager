package com.smart.smartcontactmanager.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import java.util.*;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.smart.smartcontactmanager.Entities.Contact;
import com.smart.smartcontactmanager.Entities.User;
import com.smart.smartcontactmanager.Helper.Message;
import com.smart.smartcontactmanager.duo.ContactRepository;
import com.smart.smartcontactmanager.duo.UserRepository;

import com.razorpay.*;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

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
    public String saveContact(@Valid @ModelAttribute("contact") Contact contact,BindingResult result,@RequestParam("Fileimage") MultipartFile file, Model model,Principal principal){
        try { 

            String username = principal.getName();
            User user = this.userRepository.getUserByUserName(username);

            if(result.hasErrors()){
                model.addAttribute("contact", contact);

                System.out.println("Error oCured");
                System.out.println("Error: "+result.toString());
                return "normal/add_contact_form";
            }

            if(file.isEmpty()){

                System.out.println("File is empty");
                contact.setImage("contact.png");
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

    @RequestMapping(value = "/show-contacts/{page}",method = RequestMethod.GET)
    public String showContacts(@PathVariable("page")Integer page,Model model,Principal principal){

        model.addAttribute("title", "My Contacts");

        // Sending Contacts List from here

        String username = principal.getName();
        User user = this.userRepository.getUserByUserName(username);

        Pageable pageable = PageRequest.of(page, 2);
        Page<Contact>contact= this.contactRepository.findContactsByUser(user.getId(),pageable);

        model.addAttribute("contact", contact);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contact.getTotalPages());
        
        return "normal/show_contacts";
    }

    // showing Specific contact detail

    @RequestMapping(value = "/contact/{cId}")
    public String showContactDetails(@PathVariable("cId")Integer cId,Model model,Principal principal){

        model.addAttribute("title", "User Details");
        System.out.println("cId"+cId);

       Optional<Contact>contactOptional =  this.contactRepository.findById(cId);

       Contact contact = contactOptional.get();

       String userName = principal.getName();

       User user = this.userRepository.getUserByUserName(userName);

       if(user.getId() == contact.getUser().getId()){
           model.addAttribute("contact", contact);
       }

        return "normal/contact_detail";
    }

    // Delete handler in 

    @RequestMapping(value = "/delete/{cId}",method = RequestMethod.GET)
    public String deleteContact(@PathVariable("cId") Integer cId,Model model,Principal principal){

        Optional<Contact>contacOptional = this.contactRepository.findById(cId);
        Contact contact = contacOptional.get();
        
        // check
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);
        
        if(user.getId() == contact.getUser().getId()){
            contact.setUser(null);
            this.contactRepository.delete(contact);
            model.addAttribute("message", new Message("Contact deleted successfully", "alert-success"));
        }

        else{

            System.out.println("Inside user Controller");
            model.addAttribute("message", new Message("Something went wrong...!", "alert-danger"));
        }
        return "redirect:/user/show-contacts/0";
    }


    // update Handler

    @PostMapping("/update-contact/{cId}")
    public String updateContact(@PathVariable("cId") Integer cId,Model model) {

        System.out.println(cId);

        Optional<Contact>optionalContact = this.contactRepository.findById(cId);
        Contact contact = optionalContact.get();
        model.addAttribute("contact", contact);

        model.addAttribute("title", "Update-Contact");
        return "normal/update_contact";
    }

    // update contact form handler

    @RequestMapping(value = "/process-update",method = RequestMethod.POST)
    public String processUpdate(@ModelAttribute Contact contact,@RequestParam("Fileimage")MultipartFile file,Model model,Principal principal){

    try {

        // old contact details

       Contact oldContact= this.contactRepository.findById(contact.getcId()).get();
        
        if(!file.isEmpty()){
            
            // rewrite the file

            // delete old photo

            File deletefile = new ClassPathResource("static/img").getFile();
            File file1 = new File(deletefile, oldContact.getImage());
            file1.delete();


            // update file
            File savefile = new ClassPathResource("static/img").getFile();

            Path path =  Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

           contact.setImage(file.getOriginalFilename());


        }

        else{
            
            contact.setImage(oldContact.getImage());
        }

        String username = principal.getName();
        User user = this.userRepository.getUserByUserName(username);
        contact.setUser(user);

        this.contactRepository.save(contact);

        model.addAttribute("message", new Message("Contact updated Successfully...!", "alert-success"));


    } 
    
    catch (Exception e) {
       e.printStackTrace();
    }

        return "redirect:/user/contact/"+contact.getcId();
    }


    @GetMapping("/profile")
    public String yourProfile(Model model,Principal principal){

        String username = principal.getName();
        User user = this.userRepository.getUserByUserName(username);
        model.addAttribute("user", user);

        model.addAttribute("title", "Profile Page");
        return "normal/profile";
    }

    // open setting handler

    @GetMapping("/settings")
    public String openSettings(Model model,Principal principal){

        User user = this.userRepository.getUserByUserName(principal.getName());
        model.addAttribute("user", user);
        return "normal/settings";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword")String oldPassword,@RequestParam("newPassword")String newPassword,Principal principal,Model model){

        // System.out.println(oldPassword);
        // System.out.println(newPassword);

        String username = principal.getName();
        User user = this.userRepository.getUserByUserName(username);

        // System.out.println(user.getPassword());

        if(this.bCryptPasswordEncoder.matches(oldPassword, user.getPassword())){
            
            // change the passowrd
            user.setPassword(bCryptPasswordEncoder.encode(newPassword));
            this.userRepository.save(user);

            model.addAttribute("message", new Message("Password changed Successfully...!", "alert-success"));
            
        }

        else{

            model.addAttribute("message", new Message("OldPassword is incorrect...!", "alert-danger"));
            return "normal/settings";
        }

        return "redirect:/user/index";
    }


    // creating order for payment

    @PostMapping("/create_order")
    @ResponseBody
    public String createOrder(@RequestBody Map<String,Object>data)throws RazorpayException{

        // System.out.println("Order function executed..!");
        System.out.println(data);

        int amount = Integer.parseInt(data.get("amount").toString());


            
        var client = new RazorpayClient("rzp_test_HEXkABQCzh5zDJ", "nRAweQAuEB5lvCUQts3zRTQo");

        JSONObject ob = new JSONObject();
        ob.put("amount", amount*100);
        ob.put("currency", "INR");
        ob.put("receipt", "txn_235425");

        // creating new order 

        Order order =  client.orders.create(ob);
        // System.out.println(order);

        // if you want you can save in your database order info


        return order.toString();
    }

    
}

