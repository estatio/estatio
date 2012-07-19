drop table customer if exists;
create table customer (
	id         int         not null primary key
   ,first_name varchar(30) not null
   ,initial    varchar(1)  null
   ,last_name  varchar(30) not null
)
