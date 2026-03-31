package com.dormitory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dormitory.entity.RepairOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;

/**
 * 报修单Mapper接口
 */
@Mapper
public interface RepairOrderMapper extends BaseMapper<RepairOrder> {

    /**
     * 根据学生ID查询报修单
     * @param studentId 学生ID
     * @return 报修单列表
     */
    @Select("SELECT * FROM repair_order WHERE student_id = #{studentId} ORDER BY create_time DESC")
    List<RepairOrder> findByStudentId(@Param("studentId") Long studentId);

    /**
     * 根据状态查询报修单
     * @param status 状态
     * @return 报修单列表
     */
    @Select("SELECT * FROM repair_order WHERE status = #{status} ORDER BY create_time DESC")
    List<RepairOrder> findByStatus(@Param("status") String status);

    /**
     * 更新报修单状态
     * @param id 报修单ID
     * @param status 状态
     * @return 影响行数
     */
    @Update("UPDATE repair_order SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
}