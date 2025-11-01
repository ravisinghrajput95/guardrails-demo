CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, bio TEXT);
INSERT INTO users (name, bio) VALUES ('alice', 'developer'), ('bob','ops');
