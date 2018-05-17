package com.intrasoft.csp.regrep.commons.model;

import java.util.Map;

public class Mail {

    private String from;
    private String senderName;
    private String senderEmail;
    private String to;
    private String[] toArr;
    private String subject;
    private String content;
    private Map<String, Object> model;

    public Mail() {
    }

    public Mail(String from, String to, String subject, String content) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.content = content;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map getModel() {
        return model;
    }

    public void setModel(Map model) {
        this.model = model;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String[] getToArr() {
        return toArr;
    }

    public void setToArr(String[] toArr) {
        this.toArr = toArr;
    }

    @Override
    public String toString() {
        return "Mail{" +
                "from='" + from + '\'' +
                ", senderName='" + senderName + '\'' +
                ", senderEmail='" + senderEmail + '\'' +
                ", to='" + to + '\'' +
                ", toArr='" + toArr + '\'' +
                ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                ", model=" + model +
                '}';
    }
}
