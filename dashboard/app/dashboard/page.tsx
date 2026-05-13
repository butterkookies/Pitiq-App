import { createClient } from '@/utils/supabase/server'

interface SessionRow {
  session_id: string
  created_at: string
  location_id: string
  coins_inserted: number
  upload_status: string
  print_failed: boolean
  purged: boolean
}

function statusBadge(row: SessionRow) {
  if (row.purged) return <span className="text-xs px-2 py-0.5 rounded-full bg-gray-800 text-gray-500">purged</span>
  if (row.print_failed) return <span className="text-xs px-2 py-0.5 rounded-full bg-red-950 text-red-400">print failed</span>
  if (row.upload_status === 'uploaded') return <span className="text-xs px-2 py-0.5 rounded-full bg-green-950 text-green-400">uploaded</span>
  if (row.upload_status === 'failed') return <span className="text-xs px-2 py-0.5 rounded-full bg-red-950 text-red-400">upload failed</span>
  return <span className="text-xs px-2 py-0.5 rounded-full bg-yellow-950 text-yellow-400">pending</span>
}

function formatDate(iso: string) {
  return new Date(iso).toLocaleString('en-PH', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

export default async function DashboardPage() {
  const supabase = await createClient()

  const now = new Date()
  const todayStart = new Date(now.getFullYear(), now.getMonth(), now.getDate()).toISOString()
  const weekStart = new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString()
  const monthStart = new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString()

  const [
    { count: todayCount },
    { count: weekCount },
    { count: monthCount },
    { data: sessions },
  ] = await Promise.all([
    supabase.from('sessions').select('*', { count: 'exact', head: true }).gte('created_at', todayStart),
    supabase.from('sessions').select('*', { count: 'exact', head: true }).gte('created_at', weekStart),
    supabase.from('sessions').select('*', { count: 'exact', head: true }).gte('created_at', monthStart),
    supabase
      .from('sessions')
      .select('session_id, created_at, location_id, coins_inserted, upload_status, print_failed, purged')
      .order('created_at', { ascending: false })
      .limit(30),
  ])

  const stats = [
    { label: 'Today', value: todayCount ?? 0, revenue: `₱${(todayCount ?? 0) * 40}` },
    { label: 'This Week', value: weekCount ?? 0, revenue: `₱${(weekCount ?? 0) * 40}` },
    { label: 'This Month', value: monthCount ?? 0, revenue: `₱${(monthCount ?? 0) * 40}` },
  ]

  return (
    <div className="p-8 flex flex-col gap-8">
      <div>
        <h1 className="text-xl font-semibold text-white">Session Overview</h1>
        <p className="text-sm text-gray-500 mt-1">All locations · ₱40 per session</p>
      </div>

      {/* Stat cards */}
      <div className="grid grid-cols-3 gap-4">
        {stats.map(({ label, value, revenue }) => (
          <div key={label} className="bg-gray-900 border border-gray-800 rounded-xl p-5 flex flex-col gap-3">
            <p className="text-xs text-gray-500 uppercase tracking-wider">{label}</p>
            <p className="text-3xl font-bold text-white">{value}</p>
            <p className="text-sm text-amber-400 font-medium">{revenue}</p>
          </div>
        ))}
      </div>

      {/* Recent sessions table */}
      <div className="bg-gray-900 border border-gray-800 rounded-xl overflow-hidden">
        <div className="px-5 py-4 border-b border-gray-800">
          <h2 className="text-sm font-medium text-white">Recent Sessions</h2>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-gray-800">
                <th className="text-left text-xs text-gray-500 uppercase tracking-wider px-5 py-3 font-medium">Session ID</th>
                <th className="text-left text-xs text-gray-500 uppercase tracking-wider px-5 py-3 font-medium">Location</th>
                <th className="text-left text-xs text-gray-500 uppercase tracking-wider px-5 py-3 font-medium">Date</th>
                <th className="text-left text-xs text-gray-500 uppercase tracking-wider px-5 py-3 font-medium">Coins</th>
                <th className="text-left text-xs text-gray-500 uppercase tracking-wider px-5 py-3 font-medium">Status</th>
              </tr>
            </thead>
            <tbody>
              {(sessions ?? []).map((row) => (
                <tr key={row.session_id} className="border-b border-gray-800/50 hover:bg-gray-800/30 transition-colors">
                  <td className="px-5 py-3 text-gray-400 font-mono text-xs">{row.session_id.slice(0, 8)}…</td>
                  <td className="px-5 py-3 text-gray-300">{row.location_id}</td>
                  <td className="px-5 py-3 text-gray-400">{formatDate(row.created_at)}</td>
                  <td className="px-5 py-3 text-gray-300">₱{row.coins_inserted}</td>
                  <td className="px-5 py-3">{statusBadge(row)}</td>
                </tr>
              ))}
              {!(sessions ?? []).length && (
                <tr>
                  <td colSpan={5} className="px-5 py-8 text-center text-gray-600 text-sm">
                    No sessions yet
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
