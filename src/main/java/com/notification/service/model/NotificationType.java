package com.notification.service.model;

public enum NotificationType {
    // отправка ревьюеру уведомление о новом МР
    SEND_REVIEWER_NEW_MR_MESSAGE,

    // отправка ревьюеру уведомления о созданном МР после подтверждения девелопера
    SEND_DEVELOPER_NEW_MR_REQUEST_MESSAGE,

    // отправка девелоперу уведомления о смерженном МР
    SEND_DEVELOPER_MERGED_MR_MESSAGE,

    // отправка девелоперу уведомления о созданном threshold после подтвереждения ревьюера
    SEND_REVIEWER_THRESHOLD_REQUEST_MESSAGE,

    // отправка ревьюеру уведомления о изменении в threshold после подтверждения девелопера
    SEND_DEVELOPER_THRESHOLD_FIX_REQUEST_MESSAGE
}
