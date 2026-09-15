create table payments (
  id bigserial primary key,
  order_id bigint not null references orders(id) on delete cascade,
  method varchar(30) not null,
  status varchar(30) not null,
  gateway varchar(40),
  gateway_order_id varchar(160),
  gateway_payment_id varchar(160),
  amount numeric(12,2) not null,
  currency varchar(3) not null default 'INR',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint uq_payment_gateway_payment unique(gateway_payment_id)
);
create index idx_payments_order on payments(order_id);
