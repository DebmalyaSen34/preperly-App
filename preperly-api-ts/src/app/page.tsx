import Dashboard from "./components/Dashboard";

export default function Home() {
  return (
    <main className="container mx-auto px-4 py-8">
      <h1 className="text-2xl font-semibold mb-6">API Dashboard</h1>
      <Dashboard />
    </main>
  );
}
