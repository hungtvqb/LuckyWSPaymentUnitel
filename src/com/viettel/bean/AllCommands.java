/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.bean;

import java.util.Date;

/**
 *
 * @author bacdh
 */
public class AllCommands {

    private String providerId;
    private String command;
    private String serviceId;
    private Date datetime;
    private Long status;
    private String description;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public AllCommands() {
    }
}
