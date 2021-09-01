package com.xxl.job.admin.core.alarm.WeiUtils;

public class IacsWeChatDataVo {

    String touser;
    String toparty;
    String msgtype;
    int agentid;
    /**
     * 实际接收Map类型数据
     */
    Object text;
    /**
     * 实际接收Map类型数据
     */
    Object markdown;

    public Object getText() {
        return text;
    }

    public void setText(Object text) {
        this.text = text;
    }

    public Object getMarkdown() {
        return markdown;
    }

    public void setMarkdown(Object markdown) {
        this.markdown = markdown;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public int getAgentid() {
        return agentid;
    }

    public void setAgentid(int agentid) {
        this.agentid = agentid;
    }

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getToparty() {
        return toparty;
    }

    public void setToparty(String toparty) {
        this.toparty = toparty;
    }
}

