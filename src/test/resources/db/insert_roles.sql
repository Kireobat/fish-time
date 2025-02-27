INSERT INTO fish_time.roles (id, name, description, created_time, modified_time, created_by, modified_by)
    VALUES (10, 'user', 'standard user role', current_date, null, 0, null);
INSERT INTO fish_time.roles (id, name, description, created_time, modified_time, created_by, modified_by)
    VALUES (11, 'moderator', 'can moderate meetings', current_date, null, 0, null);
INSERT INTO fish_time.roles (id, name, description, created_time, modified_time, created_by, modified_by)
    VALUES (12, 'guest', 'limited access user', current_date, null, 0, null);
INSERT INTO fish_time.roles (id, name, description, created_time, modified_time, created_by, modified_by)
VALUES (13, 'regularUser', 'createdBy a regular user', current_date, null, 10, null);