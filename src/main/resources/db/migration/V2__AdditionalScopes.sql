alter table `Scopes`
    add column `isDefault` boolean not null default false;

insert into `Scopes` ( version, name ) values ( 1, 'user_create' );
insert into `Scopes` ( version, name ) values ( 1, 'user_update' );
insert into `Scopes` ( version, name ) values ( 1, 'user_chmod' );
insert into `Scopes` ( version, name ) values ( 1, 'user_viewDetails' );

update `Scopes`
    set isDefault = true
    where name in ( 'browse', 'browse-all', 'write', 'delete' );

insert into `User_Scopes` ( userID, scopeID )
select u.id, s.id
    from `Users` u
    cross join `Scopes` s
    where u.username = 'admin'
        and s.name in ( 'user_create', 'user_update', 'user_chmod', 'user_viewDetails' );