package com.panduoma.cwhospital.controller;

import com.panduoma.cwhospital.common.Result;
import com.panduoma.cwhospital.entity.Announcement;
import com.panduoma.cwhospital.mapper.AnnouncementMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@CrossOrigin("*")
@RestController
@RequestMapping("/announcement")
public class AnnouncementController {

    @Autowired
    private AnnouncementMapper announcementMapper;

    @GetMapping("/enabled")
    public Result getEnabled() {
        try {
            Announcement announcement = announcementMapper.findEnabled();
            return Result.success("200", "查询成功", announcement);
        } catch (Exception e) {
            return Result.error("获取公告失败：" + e.getMessage());
        }
    }

    @GetMapping("/all")
    public Result getAll() {
        try {
            List<Announcement> list = announcementMapper.findAll();
            return Result.success("200", "查询成功", list);
        } catch (Exception e) {
            return Result.error("获取公告列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        try {
            Announcement announcement = announcementMapper.findById(id);
            if (announcement == null) {
                return Result.error("公告不存在");
            }
            return Result.success("200", "查询成功", announcement);
        } catch (Exception e) {
            return Result.error("获取公告失败：" + e.getMessage());
        }
    }

    @PostMapping("/save")
    public Result save(@RequestBody Announcement announcement) {
        try {
            String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date());
            announcement.setUpdateTime(now);

            if (announcement.getEnabled() != null && announcement.getEnabled()) {
                announcementMapper.disableAll();
            }

            if (announcement.getId() != null) {
                announcementMapper.update(announcement);
            } else {
                if (announcement.getEnabled() == null) {
                    announcement.setEnabled(false);
                }
                announcementMapper.insert(announcement);
            }
            return Result.success("200", "保存成功", null);
        } catch (Exception e) {
            return Result.error("保存公告失败：" + e.getMessage());
        }
    }

    @PostMapping("/enable/{id}")
    public Result enable(@PathVariable Integer id) {
        try {
            Announcement announcement = announcementMapper.findById(id);
            if (announcement == null) {
                return Result.error("公告不存在");
            }
            announcementMapper.disableAll();
            announcementMapper.enableById(id);

            String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date());
            Announcement update = new Announcement();
            update.setId(id);
            update.setTitle(announcement.getTitle());
            update.setContent(announcement.getContent());
            update.setEnabled(true);
            update.setUpdateTime(now);
            announcementMapper.update(update);

            return Result.success("200", "已设为展示公告", null);
        } catch (Exception e) {
            return Result.error("设置展示公告失败：" + e.getMessage());
        }
    }

    @PostMapping("/disable/{id}")
    public Result disable(@PathVariable Integer id) {
        try {
            announcementMapper.disableById(id);
            return Result.success("200", "已取消展示", null);
        } catch (Exception e) {
            return Result.error("取消展示失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        try {
            announcementMapper.deleteById(id);
            return Result.success("200", "删除成功", null);
        } catch (Exception e) {
            return Result.error("删除公告失败：" + e.getMessage());
        }
    }
}
