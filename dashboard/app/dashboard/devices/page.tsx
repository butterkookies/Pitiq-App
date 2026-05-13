import { createClient } from '@/utils/supabase/server'

interface SessionRow {
  location_id: string
  created_at: string
  upload_status: string
}

interface DeviceSummary {
  location_id: string
  lastSession: string
  totalSessions: number
  pending: number
  failed: number
  uploaded: number
}

function formatRelative(iso: string) {
  const diff = Date.now() - new Date(iso).getTime()
  const mins = Math.floor(diff / 60000)
  if (mins < 1) return 'just now'
  if (mins < 60) return `${mins}m ago`
  const hrs = Math.floor(mins / 60)
  if (hrs < 24) return `${hrs}h ago`
  return `${Math.floor(hrs / 24)}d ago`
}

export default async function DevicesPage() {
  const supabase = await createClient()

  const { data } = await supabase
    .from('sessions')
    .select('location_id, created_at, upload_status')
    .order('created_at', { ascending: false })

  const sessions = (data ?? []) as SessionRow[]

  const deviceMap = new Map<string, DeviceSummary>()
  for (const s of sessions) {
    if (!deviceMap.has(s.location_id)) {
      deviceMap.set(s.location_id, {
        location_id: s.location_id,
        lastSession: s.created_at,
        totalSessions: 0,
        pending: 0,
        failed: 0,
        uploaded: 0,
      })
    }
    const d = deviceMap.get(s.location_id)!
    d.totalSessions++
    if (s.upload_status === 'pending') d.pending++
    else if (s.upload_status === 'failed') d.failed++
    else if (s.upload_status === 'uploaded') d.uploaded++
  }

  const devices = Array.from(deviceMap.values()).sort((a, b) =>
    new Date(b.lastSession).getTime() - new Date(a.lastSession).getTime()
  )

  return (
    <div className="p-8 flex flex-col gap-8">
      <div>
        <h1 className="text-xl font-semibold text-white">Device Status</h1>
        <p className="text-sm text-gray-500 mt-1">{devices.length} location{devices.length !== 1 ? 's' : ''} active</p>
      </div>

      <div className="bg-gray-900 border border-gray-800 rounded-xl overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-gray-800">
                <th className="text-left text-xs text-gray-500 uppercase tracking-wider px-5 py-3 font-medium">Location ID</th>
                <th className="text-left text-xs text-gray-500 uppercase tracking-wider px-5 py-3 font-medium">Last Session</th>
                <th className="text-left text-xs text-gray-500 uppercase tracking-wider px-5 py-3 font-medium">Total</th>
                <th className="text-left text-xs text-gray-500 uppercase tracking-wider px-5 py-3 font-medium">Uploaded</th>
                <th className="text-left text-xs text-gray-500 uppercase tracking-wider px-5 py-3 font-medium">Pending</th>
                <th className="text-left text-xs text-gray-500 uppercase tracking-wider px-5 py-3 font-medium">Failed</th>
              </tr>
            </thead>
            <tbody>
              {devices.map((d) => (
                <tr key={d.location_id} className="border-b border-gray-800/50 hover:bg-gray-800/30 transition-colors">
                  <td className="px-5 py-3 text-white font-medium">{d.location_id}</td>
                  <td className="px-5 py-3 text-gray-400">{formatRelative(d.lastSession)}</td>
                  <td className="px-5 py-3 text-gray-300">{d.totalSessions}</td>
                  <td className="px-5 py-3 text-green-400">{d.uploaded}</td>
                  <td className="px-5 py-3 text-yellow-400">{d.pending}</td>
                  <td className="px-5 py-3 text-red-400">{d.failed}</td>
                </tr>
              ))}
              {!devices.length && (
                <tr>
                  <td colSpan={6} className="px-5 py-8 text-center text-gray-600 text-sm">
                    No devices have connected yet
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
