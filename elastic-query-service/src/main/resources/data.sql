CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


INSERT INTO public.users(
	id, username, firstname, lastname)
	VALUES ('f39e912b-42ab-48b2-93ab-13ae8fbfd471', 'app_user', 'Standard', 'User');
INSERT INTO public.users(
	id, username, firstname, lastname)
	VALUES ('95f8ecf3-a71c-4726-9679-0e1d57778c73', 'app_admin_user', 'Admin', 'User');
INSERT INTO public.users(
	id, username, firstname, lastname)
	VALUES ('6112a640-09aa-4c39-b027-ee0d9e020a28', 'app_super_user', 'Super', 'User');


insert into documents(id, document_id)
values ('c1df7d01-4bd7-40b6-86da-7e2ffabf37f7', 1627795912272539649);
insert into documents(id, document_id)
values ('f2b2d644-3a08-4acb-ae07-20569f6f2a01', 1627795908434751488);
insert into documents(id, document_id)
values ('90573d2b-9a5d-409e-bbb6-b94189709a19', 1627795911567876098);

insert into user_permissions(user_permission_id, user_id, document_id, permission_type)
values (uuid_generate_v4(),'f39e912b-42ab-48b2-93ab-13ae8fbfd471', 'c1df7d01-4bd7-40b6-86da-7e2ffabf37f7', 'READ');

insert into user_permissions(user_permission_id, user_id, document_id, permission_type)
values (uuid_generate_v4(),'95f8ecf3-a71c-4726-9679-0e1d57778c73', 'c1df7d01-4bd7-40b6-86da-7e2ffabf37f7', 'READ');

insert into user_permissions(user_permission_id, user_id, document_id, permission_type)
values (uuid_generate_v4(),'95f8ecf3-a71c-4726-9679-0e1d57778c73', 'f2b2d644-3a08-4acb-ae07-20569f6f2a01', 'READ');

insert into user_permissions(user_permission_id, user_id, document_id, permission_type)
values (uuid_generate_v4(), '95f8ecf3-a71c-4726-9679-0e1d57778c73', '90573d2b-9a5d-409e-bbb6-b94189709a19', 'READ');

insert into user_permissions(user_permission_id, user_id, document_id, permission_type)
values (uuid_generate_v4(), '6112a640-09aa-4c39-b027-ee0d9e020a28', 'c1df7d01-4bd7-40b6-86da-7e2ffabf37f7', 'READ');


