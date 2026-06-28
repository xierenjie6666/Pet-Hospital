package com.panduoma.cwhospital.mapper;

import com.panduoma.cwhospital.entity.Announcement;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AnnouncementMapper {

    @Select("SELECT id, title, content, enabled, update_time as updateTime FROM announcement ORDER BY id DESC")
    List<Announcement> findAll();

    @Select("SELECT id, title, content, enabled, update_time as updateTime FROM announcement WHERE enabled = 1 ORDER BY id DESC LIMIT 1")
    Announcement findEnabled();

    @Select("SELECT id, title, content, enabled, update_time as updateTime FROM announcement WHERE id = #{id}")
    Announcement findById(@Param("id") Integer id);

    @Insert("INSERT INTO announcement(title, content, enabled, update_time) VALUES(#{title}, #{content}, #{enabled}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Announcement announcement);

    @Update("UPDATE announcement SET title = #{title}, content = #{content}, enabled = #{enabled}, update_time = #{updateTime} WHERE id = #{id}")
    int update(Announcement announcement);

    @Delete("DELETE FROM announcement WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);

    @Update("UPDATE announcement SET enabled = 0 WHERE enabled = 1")
    int disableAll();

    @Update("UPDATE announcement SET enabled = 1 WHERE id = #{id}")
    int enableById(@Param("id") Integer id);

    @Update("UPDATE announcement SET enabled = 0 WHERE id = #{id}")
    int disableById(@Param("id") Integer id);
}
