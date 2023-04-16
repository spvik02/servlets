create table if not exists discount_card(
	discount_card_id bigint,
	percentage_of_discount numeric(5, 2) not null,
	date_of_registration date not null,
	constraint discount_card_pk primary key (discount_card_id)
);

create table if not exists product(
	product_id bigint,
	product_name text not null,
	price numeric(8, 2) not null,
	is_at_discount BOOLEAN not null DEFAULT true,
	constraint product_pk primary key (product_id)
);

create table if not exists receipt(
	receipt_id bigint,
	discount_card_id bigint,
	total_price numeric(8, 2) not null,
	total_price_with_discount numeric(8, 2) not null,
	cashier int not null,
	receipt_date date not null,
	receipt_time time not null,
	constraint receipt_pk primary key (receipt_id),
	constraint discount_card_fk foreign key (discount_card_id)
		references discount_card(discount_card_id)
);

create table if not exists product_in_receipt(
	uid SERIAL,
	receipt_id bigint,
	product_id bigint,
	quantity int not null,
	price numeric(8, 2) not null,
	total_price numeric(8, 2) not null,
	constraint pir_pk primary key (uid),
	constraint receipt_fk foreign key (receipt_id)
		references receipt(receipt_id) on delete cascade,
	constraint product_fk foreign key (product_id)
		references product(product_id)
);

create table if not exists stock(
	stock_id bigint,
	quantity int not null,
	sale numeric(5, 2) not null,
	description text not null,
	constraint stock_pk primary key (stock_id)
);

create table if not exists product_in_stock(
	uid SERIAL,
	stock_id bigint,
	product_id bigint,
	constraint pis_pk primary key (uid),
	constraint stock_fk foreign key (stock_id)
		references stock(stock_id) on delete cascade,
	constraint product_fk foreign key (product_id)
		references product(product_id)
);