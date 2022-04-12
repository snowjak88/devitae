create table `Scopes` (
    id int auto_increment primary key,
    version int not null,
    name nvarchar(255) not null unique
);

create table `Users` (
    id int auto_increment primary key,
    version int not null,
    username nvarchar(255) not null unique,
    password nvarchar(255) not null,
    created datetime not null
);

create table `User_Scopes` (
    `userID` int not null,
    scopeID int not null,
    foreign key ( userID ) references `Users` ( id ),
    foreign key ( scopeID ) references `Scopes` ( id )
);

insert into `Scopes` ( version, name ) values ( 1, 'browse' );
insert into `Scopes` ( version, name ) values ( 1, 'write' );
insert into `Scopes` ( version, name ) values ( 1, 'delete' );
insert into `Scopes` ( version, name ) values ( 1, 'browse-all' );
insert into `Scopes` ( version, name ) values ( 1, 'write-all' );
insert into `Scopes` ( version, name ) values ( 1, 'delete-all' );

--
-- Admin user.
-- While the local environment uses unencrypted passwords, all other environments
-- use passwords encrypted using PBKDF2:
--   * salt: c.f. PASSWORD_SALT, should provide as an environment variable
--   * iterations: 50,000
--   * hash-width: 256
--   * Cipher: PBKDF2WithHmacSHA1
insert into `Users` ( version, username, password, created ) values ( 1, 'admin', 'd59d1e06c29548daeb5661b94f25d15cc1d23e4d43c913a5b98a881b70e1b3f520141efadc1e8eef', now() );
insert into `User_Scopes` ( userID, scopeID )
select u.id, s.id
    from `Users` u
    cross join `Scopes` s
    where u.username = 'admin'
        and s.name in ( 'browse', 'write', 'delete', 'browse-all', 'write-all', 'delete-all' );
