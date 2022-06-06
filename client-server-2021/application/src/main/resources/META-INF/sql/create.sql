CREATE TABLE account (id  SERIAL NOT NULL, created_at TIMESTAMP NOT NULL, email VARCHAR(255) NOT NULL, firstname VARCHAR(255) NOT NULL, lastname VARCHAR(255) NOT NULL, password VARCHAR(255) NOT NULL, public_profile BOOLEAN NOT NULL, updated_at TIMESTAMP NOT NULL, username VARCHAR(255) NOT NULL, PRIMARY KEY (id))
CREATE TABLE post (id  SERIAL NOT NULL, account_id BIGINT NOT NULL, content VARCHAR(255) NOT NULL, created_at TIMESTAMP NOT NULL, PRIMARY KEY (id))
CREATE TABLE account_follows (account_id BIGINT NOT NULL, follows_id BIGINT NOT NULL, PRIMARY KEY (account_id, follows_id))
ALTER TABLE post ADD CONSTRAINT FK_post_account_id FOREIGN KEY (account_id) REFERENCES account (id)
