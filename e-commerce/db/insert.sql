-- Inserting roles
INSERT INTO role (name) VALUES ('USER');
INSERT INTO role (name) VALUES ('ADMIN');
INSERT INTO role (name) VALUES ('MANAGER');

-- Inserting permissions
INSERT INTO permission (name) VALUES ('admin:read');
INSERT INTO permission (name) VALUES ('admin:update');
INSERT INTO permission (name) VALUES ('admin:create');
INSERT INTO permission (name) VALUES ('admin:delete');
INSERT INTO permission (name) VALUES ('management:read');
INSERT INTO permission (name) VALUES ('management:update');
INSERT INTO permission (name) VALUES ('management:create');
INSERT INTO permission (name) VALUES ('management:delete');

-- admin role permission
INSERT INTO role_permission (role_id, permission_id)
VALUES ((SELECT id FROM role WHERE name = 'ADMIN'), (SELECT id FROM permission WHERE name = 'admin:read'));

INSERT INTO role_permission (role_id, permission_id)
VALUES ((SELECT id FROM role WHERE name = 'ADMIN'), (SELECT id FROM permission WHERE name = 'admin:update'));

INSERT INTO role_permission (role_id, permission_id)
VALUES ((SELECT id FROM role WHERE name = 'ADMIN'), (SELECT id FROM permission WHERE name = 'admin:create'));

INSERT INTO role_permission (role_id, permission_id)
VALUES ((SELECT id FROM role WHERE name = 'ADMIN'), (SELECT id FROM permission WHERE name = 'admin:delete'));

-- manager role permission
INSERT INTO role_permission (role_id, permission_id)
VALUES ((SELECT id FROM role WHERE name = 'MANAGER'), (SELECT id FROM permission WHERE name = 'management:read'));

INSERT INTO role_permission (role_id, permission_id)
VALUES ((SELECT id FROM role WHERE name = 'MANAGER'), (SELECT id FROM permission WHERE name = 'management:update'));

INSERT INTO role_permission (role_id, permission_id)
VALUES ((SELECT id FROM role WHERE name = 'MANAGER'), (SELECT id FROM permission WHERE name = 'management:create'));

INSERT INTO role_permission (role_id, permission_id)
VALUES ((SELECT id FROM role WHERE name = 'MANAGER'), (SELECT id FROM permission WHERE name = 'management:delete'));


