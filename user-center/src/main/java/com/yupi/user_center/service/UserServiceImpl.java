package com.yupi.user_center.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.user_center.constant.ErrorCode;
import com.yupi.user_center.constant.UserConstant;
import com.yupi.user_center.exception.BusinessException;
import com.yupi.user_center.mapper.UserMapper;
import com.yupi.user_center.model.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;



/**
 *用户服务实现类
 @author yupi
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    /**
     * 加密用盐常量
     * 作用：用于密码加密的固定附加字符串，保证同样的密码生成一致指纹
     * 小白理解：每次给密码“加点固定调料”，提升安全性
     */
    private static final String SALT = "yupi";
    
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        if (userAccount == null || userPassword == null || checkPassword == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        if (userAccount.isBlank() || userPassword.isBlank() || checkPassword.isBlank()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        //密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不一致");
        }
        //账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在");
        }
        //2. 密码加密
        String encryptPassword = org.springframework.util.DigestUtils.md5DigestAsHex((userPassword + SALT).getBytes(StandardCharsets.UTF_8));
       //3. 插入数据
       User user = new User();
       user.setName(userAccount);
       user.setUserAccount(userAccount);
       user.setUserPassword(encryptPassword);
       boolean save = this.save(user);
       if (!save) {
           throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，请稍后再试");
       }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        if (userAccount == null || userPassword == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        if (userAccount.isBlank() || userPassword.isBlank()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        //2. 密码加密
        String encryptPassword = org.springframework.util.DigestUtils.md5DigestAsHex((userPassword + SALT).getBytes(StandardCharsets.UTF_8));
        //查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.getOne(queryWrapper);
        if (user == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "账号或密码错误");
        }
        //3.用户脱敏
        User safetyUser = getSafetyUser(user);
        //4.记录用户登录状态（session等）
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, safetyUser);
        return safetyUser;


    }
    
    
    
    /**
     * 用户脱敏
     * @param originUser 原始用户对象
     * @return 脱敏后的用户对象
     */
   @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setName(originUser.getName());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setRoleId(originUser.getRoleId());
        safetyUser.setRoleName(originUser.getRoleName());
        safetyUser.setRoleKey(originUser.getRoleKey());
        return safetyUser;
    }
}
