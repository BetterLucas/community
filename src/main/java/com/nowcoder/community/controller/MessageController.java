package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder holder;

    @Autowired
    private UserService userService;

    //访问私信列表
    @RequestMapping(path = "/letters/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page){

        //获取当前用户
        User user = holder.getUser();

        //设置分页信息
        page.setLimit(5);
        page.setPath("/letters/list");
        page.setRows(messageService.findConversationsCount(user.getId()));

        //存储会话的列表

        List<Message> conversationList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());

        //由于需要返回多个数据，使用map
        List<Map<String, Object>> conversations = new ArrayList<>();
        //需要查询当前会话的最后一条消息，未读消息的条数，当前会话产生的总条数,显示头像
        if (conversationList != null) {
            for (Message m : conversationList) {
                Map<String, Object> map = new HashMap<>();
                int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), m.getConversationId());
                map.put("unreadCount", letterUnreadCount);
                map.put("message", m);
                map.put("letterCount", messageService.findLetterCount(m.getConversationId()));
                int targetId = user.getId() == m.getFromId() ? m.getToId() : m.getFromId();
                User target = userService.findUserById(targetId);
                map.put("target", target);
                conversations.add(map);
            }
        }

        model.addAttribute("conversations", conversations);

        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        return "/site/letter";
    }

    //查询未读消息的ID信息
    private List<Integer> getUnreadIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();
        if (letterList != null) {
            for (Message m : letterList) {
                //当消息为未读状态，且接收者为当前用户,返回当前用户的未读消息id
                if (m.getStatus() == 0 && m.getToId() == holder.getUser().getId()) {
                    ids.add(m.getId());
                }
            }
        }

        return ids;

    }

    @RequestMapping(path = "/letters/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(Model model, Page page, @PathVariable("conversationId") String conversationId) {

        //设置page
        page.setRows(messageService.findLetterCount(conversationId));
        page.setPath("/letters/detail/" + conversationId);
        page.setLimit(5);

        //查询具体会话的私信列表
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());

        List<Map<String, Object>> letters = new ArrayList<>();

        if (letterList != null) {
            for (Message m : letterList) {
                Map<String, Object> map = new HashMap<>();
                map.put("message", m);
                map.put("fromUser", userService.findUserById(m.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);
        model.addAttribute("target", getTargetUser(conversationId));
        //访问到该页面意味这与该用户的私信消息需要改为已读
        List<Integer> unreadIds = getUnreadIds(letterList);
        if (!unreadIds.isEmpty()) {
            messageService.readMessage(unreadIds);
        }
        return "/site/letter-detail";
    }

    private User getTargetUser(String conversationId){
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        if (id0 == holder.getUser().getId()) {
            return userService.findUserById(id1);
        }
        else {
            return userService.findUserById(id0);
        }
    }

    //发私信
    @RequestMapping(path = "/letters/send", method = RequestMethod.POST)
    @ResponseBody
    private String sendLetter(String content, String toName) {
        User to_user = userService.findUserByName(toName);
        if (to_user == null) {
            return CommunityUtil.getJSONString(1,"目标用户不存在！");
        }
        Message message = new Message();
        message.setContent(content);
        message.setCreateTime(new Date());
        message.setFromId(holder.getUser().getId());
        message.setToId(to_user.getId());
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        }
        else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }

        int i = messageService.addMessage(message);
        if (i == 0) {
            return CommunityUtil.getJSONString(1,"发送失败！");
        }

        return CommunityUtil.getJSONString(0);

    }


}
