package com.gulu.internship07.service;

import com.gulu.internship07.entity.Application;
import com.gulu.internship07.entity.Interview;
import com.gulu.internship07.entity.Offer;
import com.gulu.internship07.repository.ApplicationRepository;
import com.gulu.internship07.repository.InterviewRepository;
import com.gulu.internship07.repository.OfferRepository;
import com.gulu.internship07.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ApplicationStatusService {
    
    private static final Logger logger = LoggerFactory.getLogger(ApplicationStatusService.class);
    
    @Autowired
    private ApplicationRepository applicationRepository;
    
    @Autowired
    private InterviewRepository interviewRepository;
    
    @Autowired
    private OfferRepository offerRepository;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    // 定义状态常量
    public static final String STATUS_SUBMITTED_PENDING = "SUBMITTED_PENDING";       // 已投递待筛选
    public static final String STATUS_SUBMITTED_REJECTED = "SUBMITTED_REJECTED";     // 已投递未通过
    public static final String STATUS_INTERVIEW_PENDING = "INTERVIEW_PENDING";       // 已面试待结果
    public static final String STATUS_INTERVIEW_REJECTED = "INTERVIEW_REJECTED";     // 面试未通过
    public static final String STATUS_OFFER_PENDING = "OFFER_PENDING";               // 面试通过待确认
    public static final String STATUS_INTERVIEW_CANCELLED = "INTERVIEW_CANCELLED";   // 已取消面试
    public static final String STATUS_OFFER_CANCELLED = "OFFER_CANCELLED";           // 取消入职
    public static final String STATUS_INTERVIEW_TIMEOUT = "INTERVIEW_TIMEOUT";       // 超时未确认
    public static final String STATUS_OFFER_TIMEOUT = "OFFER_TIMEOUT";               // 确认入职超时
    public static final String STATUS_EMPLOYED = "EMPLOYED";                         // 已入职
    public static final String STATUS_INTERVIEW_CONFIRM = "INTERVIEW_CONFIRM";       // 待确认面试 - 新增状态
    public static final String STATUS_PROCESSING = "PROCESSING";                     // 处理中（默认状态）
    public static final String STATUS_SUBMITTED_CANCELLED = "SUBMITTED_CANCELLED";       // 已取消投递

    /**
     * 获取申请的状态信息
     */
    public Map<String, Object> getApplicationStatus(Long applicationId) {
        try {
            // 直接从数据库获取状态信息，避免实体类映射问题
            String sql = """
                    SELECT a.status as app_status, a.cancel_reason,
                           i.status as interview_status, i.interview_time,
                           o.status as offer_status, o.accept_deadline
                    FROM application a
                    LEFT JOIN interviews i ON a.id = i.application_id
                    LEFT JOIN offer o ON a.id = o.application_id
                    WHERE a.id = ?
                    """;
            
            Map<String, Object> result = jdbcTemplate.queryForMap(sql, applicationId);
            
            // 判断申请状态
            String statusType = determineStatusType(result);
            
            // 根据状态类型返回对应的状态信息
            return createStatusByType(statusType);
        } catch (Exception e) {
            logger.error("获取申请状态失败", e);
            return createErrorStatus("获取状态失败: " + e.getMessage());
        }
    }

    /**
     * 确定申请的状态类型
     */
    private String determineStatusType(Map<String, Object> data) {
        Integer appStatus = (Integer) data.get("app_status");
        Integer interviewStatus = (Integer) data.get("interview_status");
        Integer offerStatus = (Integer) data.get("offer_status");
        String cancelReason = (String) data.get("cancel_reason");
        
        // 打印状态值，帮助调试
        logger.info("应用状态: appStatus={}, interviewStatus={}, offerStatus={}", 
                appStatus, interviewStatus, offerStatus);
        
        // 11. 待确认面试 - 新增状态
        if (appStatus != null && appStatus == 1 && 
            interviewStatus != null && interviewStatus == 2) {
            return STATUS_INTERVIEW_CONFIRM;
        }
        
        // 1. 已投递未通过
        if (appStatus != null && appStatus == -1) {
            return STATUS_SUBMITTED_REJECTED;
        }
        
        // 2. 已投递待筛选
        if (appStatus != null && appStatus == 0 && interviewStatus == null) {
            return STATUS_SUBMITTED_PENDING;
        }
        
        // 3. 已面试待结果 - 修改判断条件
        if (interviewStatus != null && interviewStatus == 0) {
            return STATUS_INTERVIEW_PENDING;
        }
        
        // 4. 面试未通过
        if (interviewStatus != null && interviewStatus == -1) {
            return STATUS_INTERVIEW_REJECTED;
        }
        
        // 5. 面试通过待确认 - 修正判断条件
        if (interviewStatus != null && interviewStatus == 1 && offerStatus != null && offerStatus == 2) {
            return STATUS_OFFER_PENDING;
        }
        
        // 6. 已取消面试
        if (interviewStatus != null && interviewStatus == -2) {
            return STATUS_INTERVIEW_CANCELLED;
        }
        
        // 7. 取消入职 - 修正判断条件
        if (interviewStatus != null && interviewStatus == 1 && offerStatus != null && offerStatus == -2) {
            return STATUS_OFFER_CANCELLED;
        }
        
        // 8. 超时未确认
        if (interviewStatus != null && interviewStatus == 3) {
            return STATUS_INTERVIEW_TIMEOUT;
        }
        
        // 9. 确认入职超时
        if (interviewStatus != null && interviewStatus == 1 && offerStatus != null && offerStatus == 3) {
            return STATUS_OFFER_TIMEOUT;
        }
        
        // 10. 已入职 - 新增状态
        if (appStatus != null && appStatus == 1 && 
            interviewStatus != null && interviewStatus == 1 && 
            offerStatus != null && offerStatus == 1) {
            return STATUS_EMPLOYED;
        }
        
        // 12. 已取消投递
        if (appStatus != null && appStatus == -2) {
            return STATUS_SUBMITTED_CANCELLED;
        }
        
        // 默认状态
        return STATUS_PROCESSING;
    }

    /**
     * 根据状态类型创建状态信息
     */
    private Map<String, Object> createStatusByType(String statusType) {
        Map<String, Object> status = new HashMap<>();
        status.put("statusType", statusType);
        
        switch (statusType) {
            case STATUS_SUBMITTED_PENDING:
                status.put("text", "待筛选");
                status.put("color", "yellow");
                status.put("actionText", "撤回申请");
                status.put("actionEnabled", true);
                break;
                
            case STATUS_SUBMITTED_REJECTED:
                status.put("text", "简历未通过");
                status.put("color", "gray");
                status.put("actionText", "删除申请");
                status.put("actionEnabled", true);
                break;
                
            case STATUS_INTERVIEW_PENDING:
                status.put("text", "待结果");
                status.put("color", "yellow");
                status.put("actionText", "查看详情");
                status.put("actionEnabled", true);
                break;
                
            case STATUS_INTERVIEW_REJECTED:
                status.put("text", "面试未通过");
                status.put("color", "gray");
                status.put("actionText", "删除申请");
                status.put("actionEnabled", true);
                break;
                
            case STATUS_OFFER_PENDING:
                status.put("text", "待确认");
                status.put("color", "green");
                status.put("actionText", "确认入职");
                status.put("actionEnabled", true);
                break;
                
            case STATUS_INTERVIEW_CANCELLED:
                status.put("text", "已取消");
                status.put("color", "gray");
                status.put("actionText", "删除申请");
                status.put("actionEnabled", true);
                break;
                
            case STATUS_OFFER_CANCELLED:
                status.put("text", "已取消");
                status.put("color", "gray");
                status.put("actionText", "重新申请");
                status.put("actionEnabled", true);
                break;
                
            case STATUS_INTERVIEW_TIMEOUT:
                status.put("text", "面试已超时");
                status.put("color", "gray");
                status.put("actionText", "删除申请");
                status.put("actionEnabled", true);
                break;
                
            case STATUS_OFFER_TIMEOUT:
                status.put("text", "已超时");
                status.put("color", "gray");
                status.put("actionText", "删除申请");
                status.put("actionEnabled", true);
                break;
                
            case STATUS_EMPLOYED:
                status.put("text", "已入职");
                status.put("color", "purple");
                status.put("actionText", "查看详情");
                status.put("actionEnabled", true);
                break;
                
            case STATUS_INTERVIEW_CONFIRM:  // 新增状态
                status.put("text", "待确认面试");
                status.put("color", "green");
                status.put("actionText", "确认面试");
                status.put("actionEnabled", true);
                break;
                
            case STATUS_SUBMITTED_CANCELLED:
                status.put("text", "已取消投递");
                status.put("color", "gray");
                status.put("actionText", "删除申请");
                status.put("actionEnabled", true);
                break;
                
            case STATUS_PROCESSING:
            default:
                status.put("text", "处理中");
                status.put("color", "blue");
                status.put("actionText", "等待处理");
                status.put("actionEnabled", false);
                break;
        }
        
        return status;
    }

    /**
     * 创建错误状态
     */
    private Map<String, Object> createErrorStatus(String errorMessage) {
        Map<String, Object> status = new HashMap<>();
        status.put("error", errorMessage);
        status.put("statusType", STATUS_PROCESSING);
        status.put("text", "处理中");
        status.put("color", "blue");
        status.put("actionText", "等待处理");
        status.put("actionEnabled", false);
        return status;
    }
    
    /**
     * 获取学生的申请分页列表
     */
    public List<Map<String, Object>> getStudentApplications(Long studentId, int size, int offset) {
        String sql = """
                SELECT a.*, j.title as position, c.name as company, c.image as company_logo, c.address as company_address,
                i.interview_time, i.location as interview_location, i.status as interview_status,
                o.status as offer_status, o.accept_deadline
                FROM application a
                LEFT JOIN jobs j ON a.job_id = j.id
                LEFT JOIN company c ON j.company_id = c.id
                LEFT JOIN interviews i ON a.id = i.application_id
                LEFT JOIN offer o ON a.id = o.application_id
                WHERE a.student_id = ?
                ORDER BY a.create_time DESC
                LIMIT ? OFFSET ?
                """;
        
        return jdbcTemplate.queryForList(sql, studentId, size, offset);
    }
} 