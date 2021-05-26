package com.topolaris.wmslite.model.event;

import android.os.Bundle;

/**
 * @author toPolaris
 * description EventBus事件类定义
 * @date 2021/5/22 18:20
 */
public class MessageEvent {
    /**
     * 事件类型，详细定义参见MessageType
     */
    private MessageType messageType;
    /**
     * 需要订阅者处理的数据，用Bundle携带
     */
    private Bundle bundle;
    /**
     * 携带的String对象
     */
    private String message;

    private MessageEvent(MessageType messageType, Bundle bundle, String message) {
        this.messageType = messageType;
        this.bundle = bundle;
        this.message = message;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public String getMessage() {
        return message;
    }



    public static class Builder {
        private final MessageType type;
        private Bundle bundle;
        private String message;

        public Builder(MessageType type) {
            this.type = type;
        }

        public Builder setBundle(Bundle bundle) {
            this.bundle = bundle;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public MessageEvent build() {
            return new MessageEvent(type, bundle, message);
        }
    }
}
