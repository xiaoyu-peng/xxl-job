package com.xxl.job.admin.core.alarm.impl;

import com.xxl.job.admin.core.alarm.JobAlarm;

import com.xxl.job.admin.core.alarm.WeiUtils.IacsUrlDataVo;
import com.xxl.job.admin.core.alarm.WeiUtils.SendWeChatUtils;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.core.biz.model.ReturnT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * job alarm by wei
 *
 * @author 2021-09-01
 */
@Component
public class WeiJobAlarm implements JobAlarm {

    private static Logger logger = LoggerFactory.getLogger(WeiJobAlarm.class);
    private static String token = "";

    /**
     * fail alarm
     *
     * @param jobLog
     */
    @Override
    public boolean doAlarm(XxlJobInfo info, XxlJobLog jobLog) {
        boolean alarmResult = true;

        // send monitor wei
        if (info != null && info.getAlarmWei() != null && info.getAlarmWei().trim().length() > 0) {

            // alarmContent
            String alarmContent = "Alarm Job LogId=" + jobLog.getId();
            if (jobLog.getTriggerCode() != ReturnT.SUCCESS_CODE) {
                alarmContent += "\nTriggerMsg=\n" + jobLog.getTriggerMsg().replaceAll("<br>", "|| ");
            }
            if (jobLog.getHandleCode() > 0 && jobLog.getHandleCode() != ReturnT.SUCCESS_CODE) {
                alarmContent += "\nHandleCode=" + jobLog.getHandleMsg();
            }

            // email info
            XxlJobGroup group = XxlJobAdminConfig.getAdminConfig().getXxlJobGroupDao().load(Integer.valueOf(info.getJobGroup()));
            String content = MessageFormat.format(loadEmailJobAlarmTemplate(),
                    group != null ? group.getTitle() : "null",
                    info.getId(),
                    info.getJobDesc(),
                    alarmContent);

            SendWeChatUtils msgUtils = new SendWeChatUtils();
            try {
                String postData;
                String resp;
                String alarmWei = info.getAlarmWei().trim();
                if (alarmWei.startsWith("https://qyapi.weixin.qq.com/cgi-bin/webhook/send")) {
                    // 当 AlarmWei 为 Webhook地址
                    postData = msgUtils.createPostData(null, null, "markdown", 1000011, "content", content);
                    resp = msgUtils.post("utf-8", SendWeChatUtils.CONTENT_TYPE, alarmWei, postData, SendWeChatUtils.token);
                } else {
                    // 当 AlarmWei 为 "例：user1|user2&&party1|party2，不发给部门省略&&以及后面,多个对象|分隔"
                    String agentid = alarmWei;
                    String toparty = "";
                    if (agentid.contains("&&")) {
                        String str[] = agentid.split("&&");
                        agentid = str[0];
                        if (str.length > 1) {
                            toparty = str[1];
                        }
                    }
                    postData = msgUtils.createPostData(agentid, toparty, "markdown", 1000011, "content", content);
                    resp = msgUtils.post("utf-8", SendWeChatUtils.CONTENT_TYPE, (new IacsUrlDataVo()).getSendMessage_Url(), postData, SendWeChatUtils.token);
                }

                logger.info("请求数据======>{}", postData);
                logger.info("发送微信的响应数据======>{}", resp);
            } catch (IOException e) {
                logger.error(">>>>>>>>>>> xxl-job, job fail alarm wei send error, JobLogId:{}", jobLog.getId(), e);
                alarmResult = false;
            }
        }

        return alarmResult;
    }

    /**
     * load email job alarm template
     *
     * @return
     */
    private static final String loadEmailJobAlarmTemplate() {
        String mailBodyTemplate = "# {2}\n"
                + "> `**事项详情**` \n"
                + "> " + I18nUtil.getString("jobinfo_field_jobgroup") + "：<font color=\"comment\">{0}</font> \n"
                + "> " + I18nUtil.getString("jobinfo_field_id") + "：<font color=\"warning\">{1}</font>\n"
                + "> " + I18nUtil.getString("jobconf_monitor_alarm_title") + "：<font color=\"comment\">"
                + I18nUtil.getString("jobconf_monitor_alarm_type") + "</font>\n"
                + "> " + I18nUtil.getString("jobconf_monitor_alarm_content") + "：\n<font color=\"info\">{3}</font> \n";

        return mailBodyTemplate;
    }

}

