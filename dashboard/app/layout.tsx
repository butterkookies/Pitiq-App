import type { Metadata } from 'next'
import './globals.css'

export const metadata: Metadata = {
  title: 'Pitiq Dashboard',
  description: 'Operator dashboard for Pitiq photobooth kiosks',
}

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <body className="bg-gray-950 text-white antialiased">{children}</body>
    </html>
  )
}
