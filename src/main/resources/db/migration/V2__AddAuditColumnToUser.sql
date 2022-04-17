alter table `Users`
    add column `lastModified` timestamp not null default now();