package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    //查询当前用户会话的列表，针对每个会话只返回最新私信
    List<Message> selectConversations(int userId, int offset, int limit);

    //查询当前用户的会话数量
    int selectConversationsCount(int userId);

    //查询某个会话的私信列表
    List<Message> selectLetters(String conversationId, int offset, int limit);

    //查询某个会话私信数量
    int selectLetterCount(String conversationId);

    //查询未读消息数量(包括当前用户的所有未读消息或会话的未读消息)
    int selectLetterUnreadCount(int userId, String conversationId);

    //新增私信
    int insertMessage(Message message);

    //更改私信的状态
    int updateStatus(List<Integer> ids, int status);

}
