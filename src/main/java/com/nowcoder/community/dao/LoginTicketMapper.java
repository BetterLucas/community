package com.nowcoder.community.dao;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginTicketMapper {

    LoginTicket getTicket(String ticket);

    int insertTicket(LoginTicket loginTicket);

    int updateStatus(String ticket, int status);
}
