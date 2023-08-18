package com.smart.smartcontactmanager.duo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.smartcontactmanager.Entities.Contact;

public interface ContactRepository extends JpaRepository<Contact,Integer>{
    @Query("select c from Contact c where c.email = :email")
    public Contact getConctByUsername(@Param("email") String email);
}