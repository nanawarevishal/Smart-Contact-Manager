package com.smart.smartcontactmanager.duo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smart.smartcontactmanager.Entities.MyOrder;



public interface MyOrderRepository extends JpaRepository<MyOrder,Long> {
    
    public MyOrder findByOrderId(String orderId);
}
