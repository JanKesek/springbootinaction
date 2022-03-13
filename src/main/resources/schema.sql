create table if not exists Taco_Order (
id SERIAL PRIMARY KEY,
delivery_Name varchar(50),
delivery_Street varchar(50),
delivery_City varchar(50),
delivery_State varchar(2) ,
delivery_Zip varchar(10),
cc_number varchar(16),
cc_expiration varchar(5),
cc_cvv varchar(3),
placed_at timestamp
);
create table if not exists Taco (
id SERIAL PRIMARY KEY,
name varchar(50) not null,
taco_order bigint not null,
taco_order_key bigint not null,
created_at timestamp not null
);
create table if not exists Ingredient_Ref (
ingredient varchar(4) not null,
taco bigint not null,
taco_key bigint not null
);
create table if not exists Ingredient (
id varchar(4) not null,
name varchar(25) not null,
type varchar(10) not null
);
alter table Taco
add foreign key (taco_order) references Taco_Order(id);
alter table Ingredient_Ref
add foreign key (ingredient) references Ingredient(id);