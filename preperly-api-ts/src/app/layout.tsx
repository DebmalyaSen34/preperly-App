import "./globals.css";
import { Inter } from "next/font/google";
import type React from "react";
import Header from "./components/Header";

const inter = Inter({ subsets: ["latin"] });

export const metadata = {
  title: "API Hub",
  description: "GitHub-inspired Backend Services Dashboard",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en" className="bg-[#0d1117] text-[#c9d1d9]">
      <body className={inter.className}>
        <Header />
        <div className="min-h-screen">{children}</div>
      </body>
    </html>
  );
}
