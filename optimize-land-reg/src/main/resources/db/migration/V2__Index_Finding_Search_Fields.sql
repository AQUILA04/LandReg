-- =====================================================
-- Script d'ajout d'index pour l'API de recherche des constatations
-- Version: V2__Index_Finding_Search_Fields.sql
-- =====================================================

CREATE INDEX IF NOT EXISTS idx_finding_region ON finding(region);
CREATE INDEX IF NOT EXISTS idx_finding_prefecture ON finding(prefecture);
CREATE INDEX IF NOT EXISTS idx_finding_commune ON finding(commune);
CREATE INDEX IF NOT EXISTS idx_finding_canton ON finding(canton);
CREATE INDEX IF NOT EXISTS idx_finding_locality ON finding(locality);
CREATE INDEX IF NOT EXISTS idx_finding_surface ON finding(surface);
CREATE INDEX IF NOT EXISTS idx_finding_land_form ON finding(land_form);
