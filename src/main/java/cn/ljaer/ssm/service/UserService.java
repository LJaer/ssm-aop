package cn.ljaer.ssm.service;

import cn.ljaer.ssm.po.User;

/**
 * Created by jaer on 2016/9/12.
 */
public interface UserService {
    //查询用户
    User selectByPrimaryKey(Integer id) throws Exception;
}
