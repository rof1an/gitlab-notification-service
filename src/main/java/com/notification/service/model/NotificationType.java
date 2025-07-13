package com.notification.service.model;

// Правило наименования типов уведомлений:
// - Уведомления, которые требуют подтверждения от пользователя, включают в своё название "REQUEST_MESSAGE".
// - Уведомления, которые не требуют подтверждения, не содержат в своём названии "_REQUEST".
// - Названия уведомлений указывают на роль пользователя, которому оно в итоге отправляется
public enum NotificationType {
    SEND_REVIEWER_NEW_MR_MESSAGE,
    SEND_DEVELOPER_NEW_MR_REQUEST_MESSAGE,
    SEND_DEVELOPER_MERGED_MR_MESSAGE,
    SEND_REVIEWER_THRESHOLD_REQUEST_MESSAGE,
    SEND_DEVELOPER_THRESHOLD_FIX_REQUEST_MESSAGE,
    REMIND_REVIEWER_UNCHECKED_MR
}
