create table wishlists (
  id bigserial primary key,
  user_id bigint not null unique references users(id) on delete cascade,
  created_at timestamptz not null default now()
);
create table wishlist_items (
  wishlist_id bigint not null references wishlists(id) on delete cascade,
  product_id bigint not null references products(id) on delete cascade,
  created_at timestamptz not null default now(),
  primary key(wishlist_id,product_id)
);
create table carts (
  id bigserial primary key,
  user_id bigint not null unique references users(id) on delete cascade,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);
create table cart_items (
  cart_id bigint not null references carts(id) on delete cascade,
  variant_id bigint not null references product_variants(id),
  quantity integer not null check(quantity > 0),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  primary key(cart_id,variant_id)
);
create index idx_wishlist_items_product on wishlist_items(product_id);
create index idx_cart_items_variant on cart_items(variant_id);
