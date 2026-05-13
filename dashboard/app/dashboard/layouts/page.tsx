import { createClient } from '@/utils/supabase/server'
import { LayoutCard } from './layout-card'
import { UploadForm } from './upload-form'

interface Layout {
  id: string
  name: string
  slot_count: number
  sort_order: number
  active: boolean
  preview_url: string | null
  version: number
}

export default async function LayoutsPage() {
  const supabase = await createClient()

  const { data } = await supabase
    .from('layouts')
    .select('id, name, slot_count, sort_order, active, preview_url, version')
    .order('sort_order', { ascending: true })

  const layouts = (data ?? []) as Layout[]
  const nextSortOrder = layouts.length > 0 ? Math.max(...layouts.map((l) => l.sort_order)) + 1 : 0

  return (
    <div className="p-8 flex flex-col gap-8">
      <div>
        <h1 className="text-xl font-semibold text-white">Layout Management</h1>
        <p className="text-sm text-gray-500 mt-1">{layouts.length} layout{layouts.length !== 1 ? 's' : ''} configured</p>
      </div>

      {/* Layout grid */}
      {layouts.length > 0 ? (
        <div className="grid grid-cols-2 gap-4 lg:grid-cols-3 xl:grid-cols-4">
          {layouts.map((layout, i) => (
            <LayoutCard
              key={layout.id}
              layout={layout}
              isFirst={i === 0}
              isLast={i === layouts.length - 1}
            />
          ))}
        </div>
      ) : (
        <div className="bg-gray-900 border border-gray-800 rounded-xl px-5 py-10 text-center text-gray-600 text-sm">
          No layouts yet. Upload one below.
        </div>
      )}

      {/* Upload form */}
      <UploadForm nextSortOrder={nextSortOrder} />
    </div>
  )
}
