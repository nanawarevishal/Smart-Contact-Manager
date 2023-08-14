package com.smart.smartcontactmanager.duo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smart.smartcontactmanager.Entities.User;

public interface UserRepository extends JpaRepository<User,Integer>{

    

}