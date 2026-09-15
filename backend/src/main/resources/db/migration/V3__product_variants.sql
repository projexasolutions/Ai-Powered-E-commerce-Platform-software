create table product_variants (
  id bigserial primary key,
  product_id bigint not null references products(id) on delete cascade,
  sku varchar(100) not null unique,
  size varchar(40) not null,
  color varchar(60) not null,
  stock_quantity integer not null default 0 check (stock_quantity >= 0),
  price_override numeric(12,2),
  active boolean not null default true,
  created_at timestamptz not null default now(),
  constraint uq_product_variant unique(product_id,size,color)
);
create index idx_product_variants_product on product_variants(product_id);
create index idx_product_variants_active_stock on product_variants(active,stock_quantity);

insert into product_variants(product_id,sku,size,color,stock_quantity)
select p.id, 'GENZ-'||upper(replace(p.slug,'-',''))||'-M-BLK', 'M', 'Black', 25 from products p where p.slug='oversized-graphic-tee';
insert into product_variants(product_id,sku,size,color,stock_quantity)
select p.id, 'GENZ-'||upper(replace(p.slug,'-',''))||'-L-BLK', 'L', 'Black', 18 from products p where p.slug='oversized-graphic-tee';
insert into product_variants(product_id,sku,size,color,stock_quantity)
select p.id, 'GENZ-'||upper(replace(p.slug,'-',''))||'-9-WHT', '9', 'White', 12 from products p where p.slug='street-classic-sneakers';
insert into product_variants(product_id,sku,size,color,stock_quantity)
select p.id, 'GENZ-'||upper(replace(p.slug,'-',''))||'-10-WHT', '10', 'White', 10 from products p where p.slug='street-classic-sneakers';
insert into product_variants(product_id,sku,size,color,stock_quantity)
select p.id, 'GENZ-'||upper(replace(p.slug,'-',''))||'-M-IND', 'M', 'Indigo', 20 from products p where p.slug='baggy-cargo-jeans';
insert into product_variants(product_id,sku,size,color,stock_quantity)
select p.id, 'GENZ-'||upper(replace(p.slug,'-',''))||'-L-IND', 'L', 'Indigo', 14 from products p where p.slug='baggy-cargo-jeans';
insert into product_variants(product_id,sku,size,color,stock_quantity)
select p.id, 'GENZ-'||upper(replace(p.slug,'-',''))||'-M-GRN', 'M', 'Forest Green', 16 from products p where p.slug='minimal-hoodie';
insert into product_variants(product_id,sku,size,color,stock_quantity)
select p.id, 'GENZ-'||upper(replace(p.slug,'-',''))||'-L-GRN', 'L', 'Forest Green', 12 from products p where p.slug='minimal-hoodie';
insert into product_variants(product_id,sku,size,color,stock_quantity)
select p.id, 'GENZ-'||upper(replace(p.slug,'-',''))||'-OS-BLK', 'One Size', 'Black', 30 from products p where p.slug='ny-baseball-cap';
insert into product_variants(product_id,sku,size,color,stock_quantity)
select p.id, 'GENZ-'||upper(replace(p.slug,'-',''))||'-OS-BLK', 'One Size', 'Black', 22 from products p where p.slug='aero-sunglasses';
