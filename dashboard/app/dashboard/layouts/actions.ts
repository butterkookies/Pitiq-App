'use server'

import { createClient } from '@/utils/supabase/server'
import { revalidatePath } from 'next/cache'

export async function uploadLayout(formData: FormData): Promise<{ error?: string }> {
  const supabase = await createClient()

  const name = (formData.get('name') as string).trim()
  const slotCount = parseInt(formData.get('slot_count') as string, 10)
  const textFieldsRaw = (formData.get('text_fields') as string).trim()
  const sortOrder = parseInt(formData.get('sort_order') as string, 10)
  const active = formData.get('active') === 'true'
  const frameAsset = formData.get('frame_asset') as File
  const preview = formData.get('preview') as File

  if (!name || !frameAsset?.size || !preview?.size) {
    return { error: 'Name, frame asset, and preview are required.' }
  }

  let textFields: unknown[] = []
  if (textFieldsRaw) {
    try {
      textFields = JSON.parse(textFieldsRaw)
    } catch {
      return { error: 'text_fields must be valid JSON (e.g. []).' }
    }
  }

  const id = crypto.randomUUID()
  const frameBytes = await frameAsset.arrayBuffer()
  const previewBytes = await preview.arrayBuffer()

  const { error: frameErr } = await supabase.storage
    .from('layouts')
    .upload(`${id}/frame.png`, frameBytes, { contentType: 'image/png', upsert: false })
  if (frameErr) return { error: `Frame upload failed: ${frameErr.message}` }

  const { error: previewErr } = await supabase.storage
    .from('layouts')
    .upload(`${id}/preview.png`, previewBytes, { contentType: 'image/png', upsert: false })
  if (previewErr) return { error: `Preview upload failed: ${previewErr.message}` }

  const { data: framePublic } = supabase.storage.from('layouts').getPublicUrl(`${id}/frame.png`)
  const { data: previewPublic } = supabase.storage.from('layouts').getPublicUrl(`${id}/preview.png`)

  const { error: dbErr } = await supabase.from('layouts').insert({
    id,
    name,
    slot_count: slotCount,
    text_fields: textFields,
    sort_order: sortOrder,
    active,
    frame_asset_url: framePublic.publicUrl,
    preview_url: previewPublic.publicUrl,
    version: 1,
  })
  if (dbErr) return { error: dbErr.message }

  revalidatePath('/dashboard/layouts')
  return {}
}

export async function toggleLayoutActive(id: string, currentActive: boolean): Promise<void> {
  const supabase = await createClient()
  await supabase.from('layouts').update({ active: !currentActive }).eq('id', id)
  revalidatePath('/dashboard/layouts')
}

export async function deleteLayout(id: string): Promise<void> {
  const supabase = await createClient()
  await supabase.storage.from('layouts').remove([`${id}/frame.png`, `${id}/preview.png`])
  await supabase.from('layouts').delete().eq('id', id)
  revalidatePath('/dashboard/layouts')
}

export async function moveLayoutOrder(id: string, currentOrder: number, direction: 'up' | 'down'): Promise<void> {
  const supabase = await createClient()
  const targetOrder = direction === 'up' ? currentOrder - 1 : currentOrder + 1

  const { data: sibling } = await supabase
    .from('layouts')
    .select('id, sort_order')
    .eq('sort_order', targetOrder)
    .single()

  if (sibling) {
    await supabase.from('layouts').update({ sort_order: currentOrder }).eq('id', sibling.id)
  }
  await supabase.from('layouts').update({ sort_order: targetOrder }).eq('id', id)
  revalidatePath('/dashboard/layouts')
}
