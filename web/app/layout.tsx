import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "pitiq — your photos",
  description: "Download your pitiq photo strip",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
