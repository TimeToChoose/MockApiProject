package com.shineofeidos.mockapiproject.event;

import org.greenrobot.eventbus.EventBus;

/// 官方的 EventBus 实现
public class GreenRobotEvent {
    public boolean isSuccess;
    public EventType type;
    public String message;

    public GreenRobotEvent(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public GreenRobotEvent(EventType type) {
        this.type = type;
    }

    public GreenRobotEvent(boolean isSuccess, EventType type) {
        this.isSuccess = isSuccess;
        this.type = type;
    }

    public GreenRobotEvent(String message) {
        this.message = message;
    }

    public void post() {
        EventBus.getDefault().post(this);
    }

    public void postSticky() {
        EventBus.getDefault().postSticky(this);
    }

    public enum EventType {
        LOGIN, LOGOUT, UPDATE_USER, SHOW_TOAST, LOAD_DATA, REFRESH_UI, ERROR, SUCCESS
    }
}