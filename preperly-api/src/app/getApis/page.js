'use client'

import { useState } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { Search, Copy, ChevronDown, ChevronUp } from 'lucide-react'

const apis = [
    {
        id: 1,
        name: 'User Authentication API',
        description: 'Secure user authentication and authorization endpoints.',
        endpoints: [
            {
                method: 'POST',
                path: '/api/auth/register',
                description: 'User registration',
                requestBody: [
                    { name: 'restaurantName', type: 'string', required: true },
                    { name: 'restaurantAddress', type: 'string', required: true },
                    { name: 'phoneNumber', type: 'string', required: true },
                    { name: 'email', type: 'string', required: true },
                    { name: 'password', type: 'string', required: true },
                    { name: 'ownerName', type: 'string', required: true },
                    { name: 'ownerPhoneNumber', type: 'string', required: true },
                    { name: 'ownerEmail', type: 'string', required: true }
                ],
                successResponse: {
                    status: 201,
                    body: {
                        message: 'User is successfully registered!',
                    },
                },
                errorResponse: {
                    status: 400,
                    body: { error: 'User already exists!' },
                },
            },
        ],
    },
    {
        id: 2,
        name: 'User Verification API',
        description: 'Verify the user after successful registration using OTP.',
        endpoints: [
            {
                method: 'POST',
                path: '/api/verification/sendOtp',
                description: 'Sends OTP to the mobile number provided by the user.',
                requestBody: [
                    { name: "mobileNumber", type: 'string', required: true },
                ],
                successResponse: {
                    status: 200,
                    body:
                    {
                        success: true,
                        message: 'OTP sent successfully!',
                        data: '...'
                    },
                },
                errorResponse: {
                    status: 500,
                    body: {
                        success: false,
                        message: 'Failed to send OTP',
                        data: '...'
                    },
                },
            },
            {
                method: 'POST',
                path: '/api/verification/verifyOtp',
                description: 'Verify the OTP provided by the user with that of in the server.',
                requestBody: [
                    { name: "mobileNumber", type: 'string', required: true },
                    { name: "userOtp", type: 'string', required: true },
                ],
                successResponse: {
                    status: 200,
                    body: {
                        success: 'string',
                        message: 'OTP verified successfully'
                    },
                },
                errorResponse: {
                    status: 404,
                    body: {
                        success: false,
                        message: 'Invalid OTP'
                    },
                },
            },
        ],
    },
]

export default function EnhancedApiShowcase() {
    const [searchTerm, setSearchTerm] = useState('')
    const [expandedApi, setExpandedApi] = useState(null)
    const [expandedEndpoint, setExpandedEndpoint] = useState(null)

    const filteredApis = apis.filter(api =>
        api.name.toLowerCase().includes(searchTerm.toLowerCase())
    )

    const handleCopy = (text) => {
        navigator.clipboard.writeText(text)
        alert('Copied to clipboard!')
    }

    const renderJsonBlock = (json) => (
        <pre className="bg-gray-900 p-4 rounded-lg overflow-x-auto">
            <code className="text-sm text-green-400">
                {JSON.stringify(json, null, 2)}
            </code>
        </pre>
    )

    return (
        <div className="min-h-screen bg-gradient-to-br from-gray-900 to-gray-800 text-white">
            <header className="container mx-auto px-4 py-6">
                <h1 className="text-3xl font-bold mb-4">API Showcase</h1>
                <div className="relative">
                    <input
                        type="text"
                        placeholder="Search APIs..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="w-full bg-gray-700 text-white px-4 py-2 rounded-lg pl-10"
                    />
                    <Search className="absolute left-3 top-2.5 text-gray-400" />
                </div>
            </header>

            <main className="container mx-auto px-4 py-8">
                {filteredApis.map((api) => (
                    <motion.div
                        key={api.id}
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ duration: 0.5 }}
                        className="bg-gray-800 rounded-lg shadow-lg mb-6 overflow-hidden"
                    >
                        <div
                            className="p-6 cursor-pointer"
                            onClick={() => setExpandedApi(expandedApi === api.id ? null : api.id)}
                        >
                            <div className="flex justify-between items-center">
                                <h2 className="text-2xl font-semibold">{api.name}</h2>
                                {expandedApi === api.id ? (
                                    <ChevronUp className="text-gray-400" />
                                ) : (
                                    <ChevronDown className="text-gray-400" />
                                )}
                            </div>
                            <p className="text-gray-400 mt-2">{api.description}</p>
                        </div>
                        <AnimatePresence>
                            {expandedApi === api.id && (
                                <motion.div
                                    initial={{ opacity: 0, height: 0 }}
                                    animate={{ opacity: 1, height: 'auto' }}
                                    exit={{ opacity: 0, height: 0 }}
                                    transition={{ duration: 0.3 }}
                                >
                                    <div className="px-6 pb-6">
                                        <h3 className="text-xl font-semibold mb-4">Endpoints</h3>
                                        {api.endpoints.map((endpoint, index) => (
                                            <div key={index} className="bg-gray-700 rounded-lg p-4 mb-4">
                                                <div
                                                    className="flex justify-between items-center mb-2 cursor-pointer"
                                                    onClick={() => setExpandedEndpoint(expandedEndpoint === `${api.id}-${index}` ? null : `${api.id}-${index}`)}
                                                >
                                                    <div className="flex items-center">
                                                        <span className={`text-sm font-semibold px-2 py-1 rounded mr-2 ${endpoint.method === 'GET' ? 'bg-green-600' :
                                                            endpoint.method === 'POST' ? 'bg-blue-600' :
                                                                endpoint.method === 'PUT' ? 'bg-yellow-600' :
                                                                    'bg-red-600'
                                                            }`}>
                                                            {endpoint.method}
                                                        </span>
                                                        <span className="font-mono text-sm">{endpoint.path}</span>
                                                    </div>
                                                    <div className="flex items-center">
                                                        <button
                                                            onClick={(e) => {
                                                                e.stopPropagation()
                                                                handleCopy(endpoint.path)
                                                            }}
                                                            className="text-gray-400 hover:text-white transition-colors mr-2"
                                                            aria-label="Copy endpoint path"
                                                        >
                                                            <Copy size={18} />
                                                        </button>
                                                        {expandedEndpoint === `${api.id}-${index}` ? (
                                                            <ChevronUp className="text-gray-400" />
                                                        ) : (
                                                            <ChevronDown className="text-gray-400" />
                                                        )}
                                                    </div>
                                                </div>
                                                <p className="text-gray-400 text-sm mb-2">{endpoint.description}</p>
                                                <AnimatePresence>
                                                    {expandedEndpoint === `${api.id}-${index}` && (
                                                        <motion.div
                                                            initial={{ opacity: 0, height: 0 }}
                                                            animate={{ opacity: 1, height: 'auto' }}
                                                            exit={{ opacity: 0, height: 0 }}
                                                            transition={{ duration: 0.3 }}
                                                        >
                                                            <div className="mt-4">
                                                                <h4 className="text-lg font-semibold mb-2">Request Body</h4>
                                                                {endpoint.requestBody.length > 0 ? (
                                                                    <ul className="list-disc list-inside">
                                                                        {endpoint.requestBody.map((field, fieldIndex) => (
                                                                            <li key={fieldIndex} className="text-sm text-gray-300">
                                                                                <span className="font-semibold">{field.name}</span>
                                                                                <span className="text-gray-400"> ({field.type})</span>
                                                                                {field.required && <span className="text-red-400"> *</span>}
                                                                            </li>
                                                                        ))}
                                                                    </ul>
                                                                ) : (
                                                                    <p className="text-sm text-gray-400">No request body required</p>
                                                                )}
                                                            </div>
                                                            <div className="mt-4">
                                                                <h4 className="text-lg font-semibold mb-2">Success Response (Status: {endpoint.successResponse.status})</h4>
                                                                {renderJsonBlock(endpoint.successResponse.body)}
                                                            </div>
                                                            <div className="mt-4">
                                                                <h4 className="text-lg font-semibold mb-2">Error Response (Status: {endpoint.errorResponse.status})</h4>
                                                                {renderJsonBlock(endpoint.errorResponse.body)}
                                                            </div>
                                                        </motion.div>
                                                    )}
                                                </AnimatePresence>
                                            </div>
                                        ))}
                                    </div>
                                </motion.div>
                            )}
                        </AnimatePresence>
                    </motion.div>
                ))}
            </main>
        </div>
    )
}