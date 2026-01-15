package com.yupi.user_center.mapper;

import org.apache.ibatis.annotations.Select;

public interface CommonMapper {

    /**
     * 查询数据库版本号
     * 作用：执行 SELECT VERSION()，返回 MySQL 版本字符串。
     * 小白理解：问一下数据库“你是哪个版本？”
     */
    @Select("SELECT VERSION()")
    String selectVersion();

    /**
     * 查询当前连接使用的数据库名
     * 作用：执行 SELECT DATABASE()，返回当前库名。
     * 小白理解：确认我们现在连的是哪个库（比如 yupi）。
     */
    @Select("SELECT DATABASE()")
    String selectDatabase();

    /**
     * 查询数据库当前时间
     * 作用：执行 SELECT NOW()，返回当前时间字符串。
     * 小白理解：让数据库报个时钟，证明能正常执行查询。
     */
    @Select("SELECT NOW()")
    String selectNow();
}

