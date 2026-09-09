create table categories (
  id bigserial primary key,
  name varchar(80) not null unique,
  slug varchar(100) not null unique
);

create table products (
  id bigserial primary key,
  name varchar(160) not null,
  slug varchar(180) not null unique,
  description varchar(2000) not null,
  price numeric(12,2) not null check (price >= 0),
  rating numeric(3,2) not null check (rating between 0 and 5),
  image_url varchar(500) not null,
  active boolean not null default true,
  category_id bigint not null references categories(id)
);

create index idx_products_category_active on products(category_id, active);
create index idx_products_name on products(name);

insert into categories(name,slug) values
('T-Shirts','t-shirts'),('Hoodies','hoodies'),('Jeans & Pants','jeans-pants'),('Jackets','jackets'),('Sneakers','sneakers'),('Caps & Hats','caps-hats'),('Bags','bags'),('Sunglasses','sunglasses'),('Watches','watches'),('Jewellery','jewellery'),('Accessories','accessories');

insert into products(name,slug,description,price,rating,image_url,category_id) values
('Oversized Graphic Tee','oversized-graphic-tee','Relaxed streetwear fit.',799,4.80,'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=700&q=85',(select id from categories where slug='t-shirts')),
('Street Classic Sneakers','street-classic-sneakers','Everyday street sneakers.',2499,4.70,'https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=700&q=85',(select id from categories where slug='sneakers')),
('Baggy Cargo Jeans','baggy-cargo-jeans','Loose utility cargo silhouette.',1899,4.60,'https://images.unsplash.com/photo-1542272604-787c3835535d?auto=format&fit=crop&w=700&q=85',(select id from categories where slug='jeans-pants')),
('Minimal Hoodie','minimal-hoodie','Clean heavyweight hoodie.',1699,4.80,'https://images.unsplash.com/photo-1556821840-3a63f95609a7?auto=format&fit=crop&w=700&q=85',(select id from categories where slug='hoodies')),
('NY Baseball Cap','ny-baseball-cap','Classic everyday cap.',599,4.50,'https://images.unsplash.com/photo-1588850561407-ed78c282e89b?auto=format&fit=crop&w=700&q=85',(select id from categories where slug='caps-hats')),
('Aero Sunglasses','aero-sunglasses','Minimal statement frames.',999,4.60,'https://images.unsplash.com/photo-1511499767150-a48a237f0083?auto=format&fit=crop&w=700&q=85',(select id from categories where slug='sunglasses'));
