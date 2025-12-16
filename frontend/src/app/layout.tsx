import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "Ops Requests Dashboard",
  description: "Manage operational requests efficiently",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body>
        {children}
      </body>
    </html>
  );
}
