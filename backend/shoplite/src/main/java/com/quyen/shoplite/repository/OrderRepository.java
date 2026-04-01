package com.quyen.shoplite.repository;

import com.quyen.shoplite.domain.Order;
import com.quyen.shoplite.util.constant.StatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    Optional<Order> findByCode(String code);
    boolean existsByCode(String code);
    List<Order> findAllByStatus(StatusEnum status);
    List<Order> findAllByUserId(Integer userId);
}
