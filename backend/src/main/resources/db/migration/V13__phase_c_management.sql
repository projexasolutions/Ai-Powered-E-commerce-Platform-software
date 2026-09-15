alter table orders add column stock_released boolean not null default false;
alter table reviews add column status varchar(20) not null default 'APPROVED';
create index idx_reviews_product_status on reviews(product_id,status,created_at desc);
