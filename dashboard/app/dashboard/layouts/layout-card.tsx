'use client'

import { useTransition } from 'react'
import { toggleLayoutActive, deleteLayout, moveLayoutOrder } from './actions'

interface Layout {
  id: string
  name: string
  slot_count: number
  sort_order: number
  active: boolean
  preview_url: string | null
  version: number
}

export function LayoutCard({ layout, isFirst, isLast }: { layout: Layout; isFirst: boolean; isLast: boolean }) {
  const [isPending, startTransition] = useTransition()

  function run(fn: () => Promise<void>) {
    startTransition(fn)
  }

  return (
    <div className={`bg-gray-900 border rounded-xl overflow-hidden flex flex-col transition-opacity ${isPending ? 'opacity-50 pointer-events-none' : ''} ${layout.active ? 'border-gray-700' : 'border-gray-800'}`}>
      {/* Preview */}
      <div className="bg-gray-800 aspect-[3/4] flex items-center justify-center">
        {layout.preview_url ? (
          // eslint-disable-next-line @next/next/no-img-element
          <img src={layout.preview_url} alt={layout.name} className="w-full h-full object-contain" />
        ) : (
          <span className="text-gray-600 text-xs">No preview</span>
        )}
      </div>

      {/* Info */}
      <div className="p-4 flex flex-col gap-3">
        <div className="flex items-start justify-between gap-2">
          <div>
            <p className="text-sm font-medium text-white">{layout.name}</p>
            <p className="text-xs text-gray-500 mt-0.5">{layout.slot_count} slots · v{layout.version}</p>
          </div>
          <span className={`text-xs px-2 py-0.5 rounded-full shrink-0 ${layout.active ? 'bg-green-950 text-green-400' : 'bg-gray-800 text-gray-500'}`}>
            {layout.active ? 'active' : 'inactive'}
          </span>
        </div>

        {/* Actions */}
        <div className="flex items-center gap-2 flex-wrap">
          <button
            onClick={() => run(() => toggleLayoutActive(layout.id, layout.active))}
            className="text-xs px-3 py-1.5 rounded-lg bg-gray-800 hover:bg-gray-700 text-gray-300 transition-colors"
          >
            {layout.active ? 'Deactivate' : 'Activate'}
          </button>

          <div className="flex gap-1 ml-auto">
            <button
              disabled={isFirst}
              onClick={() => run(() => moveLayoutOrder(layout.id, layout.sort_order, 'up'))}
              className="text-xs px-2 py-1.5 rounded-lg bg-gray-800 hover:bg-gray-700 text-gray-400 disabled:opacity-30 disabled:cursor-not-allowed transition-colors"
              title="Move up"
            >
              ↑
            </button>
            <button
              disabled={isLast}
              onClick={() => run(() => moveLayoutOrder(layout.id, layout.sort_order, 'down'))}
              className="text-xs px-2 py-1.5 rounded-lg bg-gray-800 hover:bg-gray-700 text-gray-400 disabled:opacity-30 disabled:cursor-not-allowed transition-colors"
              title="Move down"
            >
              ↓
            </button>
            <button
              onClick={() => {
                if (confirm(`Delete layout "${layout.name}"? This cannot be undone.`)) {
                  run(() => deleteLayout(layout.id))
                }
              }}
              className="text-xs px-2 py-1.5 rounded-lg bg-gray-800 hover:bg-red-900 text-gray-400 hover:text-red-400 transition-colors"
              title="Delete"
            >
              ✕
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}
