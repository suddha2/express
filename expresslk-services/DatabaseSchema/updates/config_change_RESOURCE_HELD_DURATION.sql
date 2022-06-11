start transaction;

update config set value=900 where config='RESOURCE_HELD_DURATION';

commit;