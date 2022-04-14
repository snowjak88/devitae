create table `Scopes` (
    `id` int auto_increment primary key,
    `version` int not null,
    `name` nvarchar(255) not null unique,
    `isDefault` boolean not null default false
);

create table `Users` (
    `id` int auto_increment primary key,
    `version` int not null,
    `username` nvarchar(255) not null unique,
    `password` nvarchar(255) not null,
    `created` datetime not null
);

create table `User_Scopes` (
    `userID` int not null,
    `scopeID` int not null,
    foreign key ( `userID` ) references `Users` ( `id` ),
    foreign key ( `scopeID` ) references `Scopes` ( `id` )
);

insert into `Scopes` ( `version`, `isDefault`, `name` )
    values
    ( 1, true, 'browse' ),
    ( 1, true, 'write' ),
    ( 1, true, 'delete' ),
    ( 1, true, 'browse-all' ),
    ( 1, false, 'write-all' ),
    ( 1, false, 'delete-all' ),
    ( 1, false, 'user_create' ),
    ( 1, false, 'user_update' ),
    ( 1, false, 'user_chmod' ),
    ( 1, false, 'user_viewDetails' ),
    ( 1, false, 'user_delete' );
