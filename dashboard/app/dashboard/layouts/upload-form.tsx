'use client'

import { useActionState, useRef } from 'react'
import { uploadLayout } from './actions'

const initialState = { error: undefined as string | undefined }

export function UploadForm({ nextSortOrder }: { nextSortOrder: number }) {
  const [state, action, isPending] = useActionState(
    async (_prev: typeof initialState, formData: FormData) => {
      const result = await uploadLayout(formData)
      return { error: result.error }
    },
    initialState
  )

  const formRef = useRef<HTMLFormElement>(null)

  if (!isPending && !state.error && state !== initialState) {
    formRef.current?.reset()
  }

  return (
    <div className="bg-gray-900 border border-gray-800 rounded-xl p-6 flex flex-col gap-5">
      <h2 className="text-sm font-medium text-white">Upload New Layout</h2>

      <form ref={formRef} action={action} className="flex flex-col gap-4">
        <div className="grid grid-cols-2 gap-4">
          <div className="flex flex-col gap-1.5">
            <label className="text-xs text-gray-500 uppercase tracking-wider">Name *</label>
            <input
              name="name"
              required
              className="bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm text-white placeholder-gray-600 focus:outline-none focus:border-amber-500 transition-colors"
              placeholder="Classic Strip"
            />
          </div>

          <div className="flex flex-col gap-1.5">
            <label className="text-xs text-gray-500 uppercase tracking-wider">Slot Count *</label>
            <input
              name="slot_count"
              type="number"
              min="1"
              max="12"
              required
              defaultValue="2"
              className="bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-amber-500 transition-colors"
            />
          </div>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div className="flex flex-col gap-1.5">
            <label className="text-xs text-gray-500 uppercase tracking-wider">Sort Order</label>
            <input
              name="sort_order"
              type="number"
              min="0"
              required
              defaultValue={nextSortOrder}
              className="bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-amber-500 transition-colors"
            />
          </div>

          <div className="flex flex-col gap-1.5">
            <label className="text-xs text-gray-500 uppercase tracking-wider">Active</label>
            <select
              name="active"
              className="bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-amber-500 transition-colors"
            >
              <option value="true">Yes</option>
              <option value="false">No</option>
            </select>
          </div>
        </div>

        <div className="flex flex-col gap-1.5">
          <label className="text-xs text-gray-500 uppercase tracking-wider">
            Text Fields <span className="normal-case text-gray-600">(JSON array, e.g. [])</span>
          </label>
          <input
            name="text_fields"
            placeholder='[]'
            defaultValue="[]"
            className="bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm text-white font-mono placeholder-gray-600 focus:outline-none focus:border-amber-500 transition-colors"
          />
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div className="flex flex-col gap-1.5">
            <label className="text-xs text-gray-500 uppercase tracking-wider">Frame Asset PNG *</label>
            <input
              name="frame_asset"
              type="file"
              accept="image/png"
              required
              className="text-sm text-gray-400 file:mr-3 file:py-1.5 file:px-3 file:rounded-lg file:border-0 file:text-xs file:bg-gray-700 file:text-gray-300 hover:file:bg-gray-600 file:cursor-pointer"
            />
          </div>

          <div className="flex flex-col gap-1.5">
            <label className="text-xs text-gray-500 uppercase tracking-wider">Preview PNG *</label>
            <input
              name="preview"
              type="file"
              accept="image/png"
              required
              className="text-sm text-gray-400 file:mr-3 file:py-1.5 file:px-3 file:rounded-lg file:border-0 file:text-xs file:bg-gray-700 file:text-gray-300 hover:file:bg-gray-600 file:cursor-pointer"
            />
          </div>
        </div>

        {state.error && (
          <p className="text-red-400 text-sm bg-red-950/40 border border-red-900 rounded-lg px-4 py-3">
            {state.error}
          </p>
        )}

        <button
          type="submit"
          disabled={isPending}
          className="self-start bg-amber-500 hover:bg-amber-400 disabled:opacity-50 disabled:cursor-not-allowed text-gray-950 font-semibold rounded-lg px-5 py-2.5 text-sm transition-colors"
        >
          {isPending ? 'Uploading…' : 'Upload Layout'}
        </button>
      </form>
    </div>
  )
}
