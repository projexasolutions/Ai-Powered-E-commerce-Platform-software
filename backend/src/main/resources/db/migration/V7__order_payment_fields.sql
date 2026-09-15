alter table orders add column payment_method varchar(30) not null default 'COD';
alter table orders add column payment_status varchar(30) not null default 'PENDING';
alter table orders add column delivery_charge numeric(12,2) not null default 99.00;
