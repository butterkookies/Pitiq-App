import { Suspense } from 'react'
import { createAdminClient } from '@/utils/supabase/admin'

interface StorageUrls {
  thermal: string
  color: string
  gif: string
}

interface SessionRow {
  session_id: string
  expires_at: string
  purged: boolean
  storage_urls: StorageUrls
}

interface SignedUrls {
  thermal: string
  color: string
  gif: string
}

async function getSignedUrls(sessionId: string): Promise<SignedUrls | null> {
  const supabase = createAdminClient()

  const { data: session, error } = await supabase
    .from('sessions')
    .select('session_id, expires_at, purged, storage_urls')
    .eq('session_id', sessionId)
    .single<SessionRow>()

  if (error || !session) return null
  if (session.purged || new Date() > new Date(session.expires_at)) return null

  const bucket = supabase.storage.from('sessions')
  const [thermal, color, gif] = await Promise.all([
    bucket.createSignedUrl(`${sessionId}/thermal.png`, 1800),
    bucket.createSignedUrl(`${sessionId}/color.png`, 1800),
    bucket.createSignedUrl(`${sessionId}/session.gif`, 1800),
  ])

  if (!thermal.data || !color.data || !gif.data) return null

  return {
    thermal: thermal.data.signedUrl,
    color: color.data.signedUrl,
    gif: gif.data.signedUrl,
  }
}

function ExpiredPage() {
  return (
    <main className="min-h-screen bg-gray-950 flex flex-col items-center justify-center px-6 gap-6">
      <span className="text-2xl font-bold text-white tracking-widest lowercase">pitiq</span>
      <div className="flex flex-col items-center gap-3 text-center">
        <svg className="w-12 h-12 text-gray-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M12 6v6l4 2m6-2a10 10 0 11-20 0 10 10 0 0120 0z" />
        </svg>
        <p className="text-white text-lg font-medium">This link has expired</p>
        <p className="text-gray-500 text-sm max-w-xs">
          Photo links are only available for 24 hours after your session.
        </p>
      </div>
    </main>
  )
}

function LoadingPage() {
  return (
    <main className="min-h-screen bg-gray-950 flex flex-col items-center justify-center px-6 gap-4">
      <span className="text-2xl font-bold text-white tracking-widest lowercase">pitiq</span>
      <div className="w-6 h-6 border-2 border-gray-700 border-t-white rounded-full animate-spin" />
    </main>
  )
}

function DownloadButton({
  href,
  filename,
  label,
  sub,
}: {
  href: string
  filename: string
  label: string
  sub: string
}) {
  return (
    <a
      href={href}
      download={filename}
      className="flex items-center gap-4 w-full max-w-sm bg-gray-900 hover:bg-gray-800 active:bg-gray-700 border border-gray-800 rounded-2xl px-5 py-4 transition-colors"
    >
      <svg className="w-5 h-5 text-gray-400 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
      </svg>
      <div className="flex flex-col gap-0.5 text-left">
        <span className="text-white text-sm font-medium">{label}</span>
        <span className="text-gray-500 text-xs">{sub}</span>
      </div>
    </a>
  )
}

function DownloadPage({ urls }: { urls: SignedUrls }) {
  return (
    <main className="min-h-screen bg-gray-950 flex flex-col items-center px-6 py-10 gap-8">
      <span className="text-2xl font-bold text-white tracking-widest lowercase">pitiq</span>

      <div className="w-full max-w-sm rounded-2xl overflow-hidden border border-gray-800 bg-gray-900">
        {/* eslint-disable-next-line @next/next/no-img-element */}
        <img
          src={urls.thermal}
          alt="Your photo strip"
          className="w-full object-contain"
        />
      </div>

      <div className="flex flex-col gap-3 w-full items-center">
        <p className="text-gray-400 text-xs uppercase tracking-wider mb-1">Download</p>
        <DownloadButton
          href={urls.color}
          filename="pitiq-color.png"
          label="Original (Color)"
          sub="High-res color strip"
        />
        <DownloadButton
          href={urls.thermal}
          filename="pitiq-strip.png"
          label="Strip (Monochrome)"
          sub="Matches your printed copy"
        />
        <DownloadButton
          href={urls.gif}
          filename="pitiq-animation.gif"
          label="Animated GIF"
          sub="All your frames"
        />
      </div>

      <p className="text-gray-700 text-xs text-center mt-auto">
        Links expire 24 hours after your session
      </p>
    </main>
  )
}

async function SessionContent({ params }: { params: Promise<{ sessionId: string }> }) {
  const { sessionId } = await params
  const urls = await getSignedUrls(sessionId)
  if (!urls) return <ExpiredPage />
  return <DownloadPage urls={urls} />
}

export default function SessionSharePage({
  params,
}: {
  params: Promise<{ sessionId: string }>
}) {
  return (
    <Suspense fallback={<LoadingPage />}>
      <SessionContent params={params} />
    </Suspense>
  )
}
