package com.melody.dubbo.dao;

import com.melody.dubbo.entity.LapTop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDAO extends JpaRepository<LapTop, Integer> {

}
