package com.dormitory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dormitory.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户数据访问层
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据账号查询用户
     * @param account 账号
     * @return 用户信息
     */
    @Select("SELECT * FROM user WHERE account = #{account}")
    User findByAccount(@Param("account") String account);
}