create table payment_webhook_events (
  id bigserial primary key,
  event_id varchar(160) not null unique,
  event_type varchar(100) not null,
  processed_at timestamptz not null default now()
);
