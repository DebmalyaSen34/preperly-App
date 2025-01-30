"use client";

import { useState, useEffect } from "react";

const apiEndpoints = [
  { name: "Authentication", status: "operational" },
  { name: "Data Processing", status: "operational" },
  { name: "File Storage", status: "operational" },
  { name: "Analytics", status: "operational" },
];

export default function ApiStatus() {
  const [endpoints, setEndpoints] = useState(apiEndpoints);

  useEffect(() => {
    const interval = setInterval(() => {
      setEndpoints((prev) =>
        prev.map((endpoint) => ({
          ...endpoint,
          status: Math.random() > 0.9 ? "degraded" : "operational",
        }))
      );
    }, 5000);

    return () => clearInterval(interval);
  }, []);

  return (
    <div className="bg-[#161b22] border border-[#30363d] rounded-md p-4">
      <h2 className="text-xl font-semibold mb-4">API Status</h2>
      <ul className="space-y-2">
        {endpoints.map((endpoint, index) => (
          <li key={index} className="flex justify-between items-center">
            <span>{endpoint.name}</span>
            <span
              className={`px-2 py-1 text-xs font-medium rounded-full ${
                endpoint.status === "operational"
                  ? "bg-[#238636] text-white"
                  : "bg-[#f85149] text-white"
              }`}
            >
              {endpoint.status}
            </span>
          </li>
        ))}
      </ul>
    </div>
  );
}
