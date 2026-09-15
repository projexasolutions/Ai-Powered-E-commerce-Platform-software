create table orders (
  id bigserial primary key,
  user_id bigint not null references users(id),
  address_id bigint references addresses(id),
  status varchar(30) not null,
  subtotal numeric(12,2) not null,
  shipping_amount numeric(12,2) not null,
  total_amount numeric(12,2) not null,
  currency varchar(3) not null default 'INR',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);
create table order_items (
  id bigserial primary key,
  order_id bigint not null references orders(id) on delete cascade,
  variant_id bigint not null references product_variants(id),
  product_name varchar(160) not null,
  sku varchar(100) not null,
  size varchar(40) not null,
  color varchar(60) not null,
  quantity integer not null check (quantity > 0),
  unit_price numeric(12,2) not null,
  line_total numeric(12,2) not null
);
create index idx_orders_user_created on orders(user_id,created_at desc);
create index idx_order_items_order on order_items(order_id);
