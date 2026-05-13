-- ============================================================
-- PITIQ — Migration 003: pg_cron schedule for purge job
-- Run in Supabase SQL editor (Dashboard → SQL Editor → Run)
-- NOTE: The purge-expired-sessions edge function uses Deno.cron
--       internally; this migration is a fallback pg_cron trigger
--       in case Deno.cron is unavailable on the Supabase plan.
-- ============================================================

-- Enable pg_cron extension (requires superuser; pre-enabled on Supabase)
CREATE EXTENSION IF NOT EXISTS pg_cron;

-- Schedule purge-expired-sessions edge function to run hourly
SELECT cron.schedule(
    'purge-expired-sessions',               -- job name
    '0 * * * *',                            -- every hour at :00
    $$
    SELECT net.http_post(
        url    := current_setting('app.supabase_url') || '/functions/v1/purge-expired-sessions',
        headers := jsonb_build_object(
            'Authorization', 'Bearer ' || current_setting('app.service_role_key'),
            'Content-Type',  'application/json'
        ),
        body   := '{}'::jsonb
    )
    $$
);
