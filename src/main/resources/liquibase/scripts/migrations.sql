create table notifications
(
    id SERIAL NOT NULL PRIMARY KEY,
    chatId bigint NOT NULL,
    notification_date timestamp NOT NULL,
    message text NOT NULL,
    status varchar(255) NOT NULL DEFAULT 'SCHEDULED',
    notification_sent timestamp
    );
    CREATE INDEX notification_date_index ON notifications (notification_date) WHERE status = 'SCHEDULED';