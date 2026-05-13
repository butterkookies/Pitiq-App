-- ============================================================
-- PITIQ — Migration 001: Core tables + RLS
-- Run in Supabase SQL editor (Dashboard → SQL Editor → Run)
-- ============================================================

-- ──────────────────────────────────────────────────────────
-- locations
-- ──────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS locations (
    location_id  TEXT PRIMARY KEY,
    name         TEXT NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

ALTER TABLE locations ENABLE ROW LEVEL SECURITY;

-- Operators can manage locations; anon can read (device needs to validate its location_id)
CREATE POLICY "locations_anon_select"  ON locations FOR SELECT USING (true);
CREATE POLICY "locations_auth_all"     ON locations FOR ALL   USING (auth.role() = 'authenticated');

-- ──────────────────────────────────────────────────────────
-- layouts
-- ──────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS layouts (
    id           UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    name         TEXT        NOT NULL,
    slot_count   INT         NOT NULL CHECK (slot_count BETWEEN 1 AND 12),
    text_fields  JSONB       NOT NULL DEFAULT '[]',
    sort_order   INT         NOT NULL DEFAULT 0,
    active       BOOLEAN     NOT NULL DEFAULT TRUE,
    version      INT         NOT NULL DEFAULT 1,
    frame_url    TEXT,
    preview_url  TEXT,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

ALTER TABLE layouts ENABLE ROW LEVEL SECURITY;

-- Devices sync layouts anonymously; operators manage them
CREATE POLICY "layouts_anon_select"  ON layouts FOR SELECT USING (true);
CREATE POLICY "layouts_auth_all"     ON layouts FOR ALL   USING (auth.role() = 'authenticated');

-- ──────────────────────────────────────────────────────────
-- sessions
-- ──────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS sessions (
    session_id           UUID        PRIMARY KEY,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at           TIMESTAMPTZ GENERATED ALWAYS AS (created_at + INTERVAL '24 hours') STORED,
    storage_urls         JSONB       NOT NULL DEFAULT '{}',
    location_id          TEXT        NOT NULL,
    coins_inserted       INT         NOT NULL,
    printed              BOOLEAN     NOT NULL DEFAULT FALSE,
    print_failed         BOOLEAN     NOT NULL DEFAULT FALSE,
    error_log            JSONB       NOT NULL DEFAULT '[]',
    upload_status        TEXT        NOT NULL DEFAULT 'pending'
                             CHECK (upload_status IN ('pending', 'uploaded', 'failed')),
    upload_attempted_at  TIMESTAMPTZ,
    purged               BOOLEAN     NOT NULL DEFAULT FALSE
);

ALTER TABLE sessions ENABLE ROW LEVEL SECURITY;

-- Devices insert their own sessions; operators read all sessions
CREATE POLICY "sessions_anon_insert"  ON sessions FOR INSERT WITH CHECK (true);
CREATE POLICY "sessions_auth_select"  ON sessions FOR SELECT USING (auth.role() = 'authenticated');
CREATE POLICY "sessions_auth_update"  ON sessions FOR UPDATE USING (auth.role() = 'authenticated');
CREATE POLICY "sessions_auth_delete"  ON sessions FOR DELETE USING (auth.role() = 'authenticated');

-- Anon users must NOT be able to read other sessions' storage_urls
-- (the share page uses the service_role key server-side — never the anon key)

-- ──────────────────────────────────────────────────────────
-- Indexes
-- ──────────────────────────────────────────────────────────
CREATE INDEX IF NOT EXISTS idx_sessions_location_id    ON sessions (location_id);
CREATE INDEX IF NOT EXISTS idx_sessions_upload_status  ON sessions (upload_status);
CREATE INDEX IF NOT EXISTS idx_sessions_expires_at     ON sessions (expires_at);
CREATE INDEX IF NOT EXISTS idx_sessions_purged         ON sessions (purged);
