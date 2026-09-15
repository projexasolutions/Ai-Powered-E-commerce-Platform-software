create table addresses (
  id bigserial primary key,
  user_id bigint not null references users(id) on delete cascade,
  full_name varchar(160) not null,
  phone varchar(30) not null,
  line1 varchar(200) not null,
  line2 varchar(200),
  city varchar(100) not null,
  state varchar(100) not null,
  postal_code varchar(20) not null,
  country varchar(100) not null default 'India',
  is_default boolean not null default false,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);
create index idx_addresses_user on addresses(user_id);
create unique index uq_addresses_default_user on addresses(user_id) where is_default=true;
