package cn.ljaer.ssm.service.impl;

import cn.ljaer.ssm.mapper.UserMapper;
import cn.ljaer.ssm.po.User;
import cn.ljaer.ssm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by jaer on 2016/9/12.
 */
public class UserServiceImpl implements UserService{

    @Autowired
    private UserMapper userMapper;

    @Override
    public User selectByPrimaryKey(Integer id) throws Exception {
        return userMapper.selectByPrimaryKey(id);
    }
}
