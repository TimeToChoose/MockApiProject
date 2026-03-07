package com.shineofeidos.mockapiproject.event;

public class AppEvent extends BusEvent {
    public boolean isSuccess;
    public EventTypeEnum type;
    public Long playletId;

    public AppEvent(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public AppEvent(EventTypeEnum type) {
        this.type = type;
    }

    public AppEvent(boolean isSuccess, EventTypeEnum type) {
        this.isSuccess = isSuccess;
        this.type = type;
    }

    public enum EventTypeEnum {
        DEMO_LOGIN ("登录事件"), DEMO_LOGOUT ("登出事件"), DEMO_UPDATE_USER ("更新用户信息"), DEMO_SHOW_TOAST ("显示提示")
        , DEMO_LOAD_DATA ("加载数据"), DEMO_REFRESH_UI ("刷新界面"), DEMO_ERROR ("错误事件"), DEMO_SUCCESS ("成功事件");

        private String name;

        private int code;

        EventTypeEnum(String name) {
            this.name = name;
        }

        EventTypeEnum(String name, int code) {
            this.name = name;
            this.code = code;
        }
    }

}