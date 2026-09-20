-- The `roles` table was originally created by Hibernate's ddl-auto=update, which generated a
-- CHECK constraint listing only the ERole values that existed at the time (ROLE_ADMIN,
-- ROLE_TEACHER, ROLE_STUDENT, ROLE_PARENT). ddl-auto=update never widens an existing check
-- constraint when new enum values are added in code, so inserting ROLE_ACCOUNTANT /
-- ROLE_PRINCIPAL / ROLE_DEPUTY_PRINCIPAL fails at startup with:
--   ERROR: new row for relation "roles" violates check constraint "roles_name_check"
--
-- The Java ERole enum already guarantees only valid values are ever inserted from application
-- code, so the DB-level constraint is redundant going forward (now that schema changes go
-- through reviewed migrations instead of silent auto-update) — drop it rather than trying to
-- keep it manually in sync with ERole on every future role addition.
ALTER TABLE roles DROP CONSTRAINT IF EXISTS roles_name_check;
ALTER TABLE roles ALTER COLUMN name TYPE varchar(30);
