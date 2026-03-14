-- Index pour la table actor
CREATE INDEX idx_actor_name ON actor (name);
CREATE INDEX idx_actor_phone ON actor (phone);

-- Index pour la table registration (si applicable)
CREATE INDEX idx_registration_name ON registration (name);
CREATE INDEX idx_registration_phone ON registration (phone);
