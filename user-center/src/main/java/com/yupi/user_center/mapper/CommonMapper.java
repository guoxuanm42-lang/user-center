package com.yupi.user_center.mapper;

import org.apache.ibatis.annotations.Select;

/**
 * 通用 Mapper（提供少量通用 SQL 查询能力）。
 *
 * @author Ethan
 */
public interface CommonMapper {

    /**
     * 查询数据库版本号。
     *
     * @return 数据库版本字符串
     */
    @Select("SELECT VERSION()")
    String selectVersion();

    /**
     * 查询当前连接使用的数据库名。
     *
     * @return 当前数据库名
     */
    @Select("SELECT DATABASE()")
    String selectDatabase();

    /**
     * 查询数据库当前时间。
     *
     * @return 数据库当前时间字符串
     */
    @Select("SELECT NOW()")
    String selectNow();
}
