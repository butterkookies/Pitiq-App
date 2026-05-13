-- ============================================================
-- PITIQ — Migration 002: Storage buckets + RLS
-- Run in Supabase SQL editor (Dashboard → SQL Editor → Run)
-- ============================================================

-- ──────────────────────────────────────────────────────────
-- Buckets
-- ──────────────────────────────────────────────────────────

-- Private bucket for per-session files (thermal.png, color.png, session.gif).
-- Signed URLs (30-min expiry) issued server-side; raw paths never returned to clients.
INSERT INTO storage.buckets (id, name, public)
VALUES ('sessions', 'sessions', FALSE)
ON CONFLICT (id) DO NOTHING;

-- Public-read bucket for layout frame/preview assets.
INSERT INTO storage.buckets (id, name, public)
VALUES ('layouts', 'layouts', TRUE)
ON CONFLICT (id) DO NOTHING;

-- ──────────────────────────────────────────────────────────
-- sessions bucket RLS
-- ──────────────────────────────────────────────────────────

-- Devices (anon) may upload their own session files
CREATE POLICY "sessions_storage_anon_insert"
    ON storage.objects FOR INSERT
    WITH CHECK (bucket_id = 'sessions');

-- Only the server (service_role) issues signed URLs; anon SELECT is blocked.
-- Authenticated operators may read all session files.
CREATE POLICY "sessions_storage_auth_select"
    ON storage.objects FOR SELECT
    USING (bucket_id = 'sessions' AND auth.role() = 'authenticated');

-- Purge edge function (service_role) deletes expired files
CREATE POLICY "sessions_storage_auth_delete"
    ON storage.objects FOR DELETE
    USING (bucket_id = 'sessions' AND auth.role() = 'authenticated');

-- ──────────────────────────────────────────────────────────
-- layouts bucket RLS
-- ──────────────────────────────────────────────────────────

-- Public read (bucket is public, but explicit policy for clarity)
CREATE POLICY "layouts_storage_public_select"
    ON storage.objects FOR SELECT
    USING (bucket_id = 'layouts');

-- Only operators may upload/update/delete layout assets
CREATE POLICY "layouts_storage_auth_insert"
    ON storage.objects FOR INSERT
    WITH CHECK (bucket_id = 'layouts' AND auth.role() = 'authenticated');

CREATE POLICY "layouts_storage_auth_update"
    ON storage.objects FOR UPDATE
    USING (bucket_id = 'layouts' AND auth.role() = 'authenticated');

CREATE POLICY "layouts_storage_auth_delete"
    ON storage.objects FOR DELETE
    USING (bucket_id = 'layouts' AND auth.role() = 'authenticated');
