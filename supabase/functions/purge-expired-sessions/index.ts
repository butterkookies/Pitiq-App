import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

const supabase = createClient(
  Deno.env.get("SUPABASE_URL")!,
  Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!,
);

Deno.serve(async () => {
  // Fetch sessions whose 24-hour TTL has passed and have not been purged yet
  const { data: expired, error: fetchError } = await supabase
    .from("sessions")
    .select("session_id, storage_urls")
    .lt("expires_at", new Date().toISOString())
    .eq("purged", false);

  if (fetchError) {
    console.error("fetch error:", fetchError.message);
    return new Response(JSON.stringify({ error: fetchError.message }), { status: 500 });
  }

  if (!expired || expired.length === 0) {
    return new Response(JSON.stringify({ purged: 0 }), { status: 200 });
  }

  let purgedCount = 0;

  for (const session of expired) {
    const sessionId: string = session.session_id;

    // List all files under sessions/<session_id>/
    const { data: files } = await supabase.storage
      .from("sessions")
      .list(sessionId);

    if (files && files.length > 0) {
      const paths = files.map((f: { name: string }) => `${sessionId}/${f.name}`);
      const { error: removeError } = await supabase.storage
        .from("sessions")
        .remove(paths);
      if (removeError) {
        console.error(`storage remove error for ${sessionId}:`, removeError.message);
      }
    }

    // Mark purged regardless of whether storage delete succeeded
    const { error: updateError } = await supabase
      .from("sessions")
      .update({ purged: true })
      .eq("session_id", sessionId);

    if (updateError) {
      console.error(`update error for ${sessionId}:`, updateError.message);
    } else {
      purgedCount++;
    }
  }

  return new Response(JSON.stringify({ purged: purgedCount }), { status: 200 });
});
