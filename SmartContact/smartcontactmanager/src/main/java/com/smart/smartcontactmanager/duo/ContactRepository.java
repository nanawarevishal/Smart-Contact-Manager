package com.smart.smartcontactmanager.duo;



import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.smartcontactmanager.Entities.Contact;
import com.smart.smartcontactmanager.Entities.User;

public interface ContactRepository extends JpaRepository<Contact,Integer>{

    // Pagination... method implementation

    @Query("from Contact as c where c.user.id=:userId")
    public Page<Contact>findContactsByUser(@Param("userId")int userId,Pageable pageable);

    // Pageable containes current page
    // and contact per page in the html file

    //Search
    public List<Contact> findByNameContainingAndUser(String keywords,User user);
}