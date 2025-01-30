"use client";

import { useState } from "react";
import { ChevronRight, GitBranch } from "lucide-react";

const apiDocs = [
  {
    name: "Authentication",
    endpoint: "/api/auth",
    method: "POST",
    description: "Authenticate users and generate JWT tokens",
  },
  {
    name: "Data Processing",
    endpoint: "/api/process",
    method: "POST",
    description: "Process and analyze incoming data streams",
  },
  {
    name: "File Storage",
    endpoint: "/api/storage",
    method: "PUT",
    description: "Store and retrieve files securely in the cloud",
  },
  {
    name: "Analytics",
    endpoint: "/api/analytics",
    method: "GET",
    description: "Retrieve detailed analytics and insights",
  },
];

export default function ApiList() {
  const [selectedApi, setSelectedApi] = useState(apiDocs[0]);

  return (
    <div className="bg-[#161b22] border border-[#30363d] rounded-md">
      <ul className="divide-y divide-[#30363d]">
        {apiDocs.map((api, index) => (
          <li
            key={index}
            className={`p-4 cursor-pointer hover:bg-[#1f2937] ${
              selectedApi.name === api.name ? "bg-[#1f2937]" : ""
            }`}
            onClick={() => setSelectedApi(api)}
          >
            <div className="flex items-center justify-between">
              <div>
                <h3 className="text-lg font-semibold text-[#58a6ff]">
                  {api.name}
                </h3>
                <p className="text-sm text-[#8b949e] mt-1">{api.description}</p>
              </div>
              <ChevronRight className="text-[#8b949e]" />
            </div>
            {selectedApi.name === api.name && (
              <div className="mt-4 bg-[#0d1117] p-4 rounded-md">
                <p>
                  <span className="text-[#8b949e]">Endpoint:</span>{" "}
                  {api.endpoint}
                </p>
                <p>
                  <span className="text-[#8b949e]">Method:</span> {api.method}
                </p>
                <div className="mt-2 flex items-center text-sm text-[#8b949e]">
                  <GitBranch className="mr-2" size={16} />
                  <span>main</span>
                </div>
              </div>
            )}
          </li>
        ))}
      </ul>
    </div>
  );
}
