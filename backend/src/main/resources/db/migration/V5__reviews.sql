create table reviews (
  id bigserial primary key,
  product_id bigint not null references products(id) on delete cascade,
  user_id bigint not null references users(id) on delete cascade,
  rating integer not null check (rating between 1 and 5),
  title varchar(160),
  body varchar(2000),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint uq_review_product_user unique(product_id,user_id)
);

create index idx_reviews_product_created on reviews(product_id,created_at desc);
create index idx_reviews_user on reviews(user_id);
