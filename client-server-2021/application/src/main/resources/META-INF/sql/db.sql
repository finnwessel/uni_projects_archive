create table if not exists account
(
    id             bigserial not null primary key,
    username       varchar   not null,
    "password"     varchar   not null,
    email          varchar   not null,
    firstname      varchar   not null,
    lastname       varchar   not null,
    public_profile boolean   not null,
    updated_at     timestamp,
    created_at     timestamp not null
);

create table if not exists post
(
    id         bigserial not null primary key,
    content    text      not null,
    account_id bigserial not null,
    foreign key (account_id) references account (id),
    created_at timestamp not null
);

create table if not exists account_follows
(
    account_id bigserial not null,
    follows_id bigserial not null,
    primary key (account_id, follows_id),
    foreign key (account_id) references account (id),
    foreign key (follows_id) references account (id)
)
