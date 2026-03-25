-- Add keycloak_id column to user table
ALTER TABLE user ADD COLUMN keycloak_id VARCHAR(255) UNIQUE;

-- Create index for keycloak_id
CREATE INDEX idx_keycloak_id ON user(keycloak_id);
