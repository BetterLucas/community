package com.nowcoder.community.dao;

import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
public interface UserMapper {

    User getUserById(int id);
    User getUserByName(String name);
    User getUserByEmail(String email);

    int insertUser(User user);
}
