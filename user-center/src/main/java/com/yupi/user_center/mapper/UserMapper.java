package com.yupi.user_center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yupi.user_center.model.User;

public interface UserMapper extends BaseMapper<User> {

    /**
     * 使用 MyBatis-Plus 提供的通用方法，无需手写 SQL。
     * 作用：可直接调用 selectList(null)、selectById(id) 等方法完成 CRUD。
     * 小白理解：内置“现成的”增删改查按钮，直接用就行。
     */
}
