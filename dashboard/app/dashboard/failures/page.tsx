import { createClient } from '@/utils/supabase/server'

interface FailureRow {
  session_id: string
  created_at: string
  location_id: string
  error_log: unknown[]
}

function formatDate(iso: string) {
  return new Date(iso).toLocaleString('en-PH', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

export default async function FailuresPage() {
  const supabase = await createClient()

  const { data: failures } = await supabase
    .from('sessions')
    .select('session_id, created_at, location_id, error_log')
    .eq('print_failed', true)
    .order('created_at', { ascending: false })
    .limit(100)

  const rows = (failures ?? []) as FailureRow[]

  return (
    <div className="p-8 flex flex-col gap-8">
      <div>
        <h1 className="text-xl font-semibold text-white">Print Failure Log</h1>
        <p className="text-sm text-gray-500 mt-1">{rows.length} session{rows.length !== 1 ? 's' : ''} with print failures</p>
      </div>

      <div className="bg-gray-900 border border-gray-800 rounded-xl overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-gray-800">
                <th className="text-left text-xs text-gray-500 uppercase tracking-wider px-5 py-3 font-medium">Session ID</th>
                <th className="text-left text-xs text-gray-500 uppercase tracking-wider px-5 py-3 font-medium">Location</th>
                <th className="text-left text-xs text-gray-500 uppercase tracking-wider px-5 py-3 font-medium">Date</th>
                <th className="text-left text-xs text-gray-500 uppercase tracking-wider px-5 py-3 font-medium">Error Log</th>
              </tr>
            </thead>
            <tbody>
              {rows.map((row) => (
                <tr key={row.session_id} className="border-b border-gray-800/50 align-top">
                  <td className="px-5 py-4 text-gray-400 font-mono text-xs">{row.session_id.slice(0, 8)}…</td>
                  <td className="px-5 py-4 text-gray-300">{row.location_id}</td>
                  <td className="px-5 py-4 text-gray-400 whitespace-nowrap">{formatDate(row.created_at)}</td>
                  <td className="px-5 py-4">
                    {row.error_log && row.error_log.length > 0 ? (
                      <pre className="text-xs text-red-400 bg-red-950/30 border border-red-900/50 rounded-lg p-3 whitespace-pre-wrap max-w-xl overflow-auto">
                        {JSON.stringify(row.error_log, null, 2)}
                      </pre>
                    ) : (
                      <span className="text-gray-600 text-xs">No error data</span>
                    )}
                  </td>
                </tr>
              ))}
              {!rows.length && (
                <tr>
                  <td colSpan={4} className="px-5 py-8 text-center text-gray-600 text-sm">
                    No print failures recorded
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
