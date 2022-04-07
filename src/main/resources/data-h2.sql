insert into Scopes ( version, name ) values ( 1, 'browse' );
insert into Scopes ( version, name ) values ( 1, 'write' );
insert into Scopes ( version, name ) values ( 1, 'delete' );
insert into Scopes ( version, name ) values ( 1, 'browse-all' );
insert into Scopes ( version, name ) values ( 1, 'write-all' );
insert into Scopes ( version, name ) values ( 1, 'delete-all' );

insert into Users ( version, username, password, created ) values ( 1, 'user', 'password', CURRENT_DATE() );
insert into Users ( version, username, password, created ) values ( 1, 'admin', 'admin', CURRENT_DATE() );

insert into User_Scopes ( userID, scopeID ) values ( 1, 1 );
insert into User_Scopes ( userID, scopeID ) values ( 1, 2 );
insert into User_Scopes ( userID, scopeID ) values ( 1, 3 );

insert into User_Scopes ( userID, scopeID ) values ( 2, 1 );
insert into User_Scopes ( userID, scopeID ) values ( 2, 2 );
insert into User_Scopes ( userID, scopeID ) values ( 2, 3 );
insert into User_Scopes ( userID, scopeID ) values ( 2, 4 );
insert into User_Scopes ( userID, scopeID ) values ( 2, 5 );
insert into User_Scopes ( userID, scopeID ) values ( 2, 6 );