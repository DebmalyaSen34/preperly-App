'use client'

import { useState, useEffect } from 'react'
import { motion } from 'framer-motion'
import { ChevronRight, Code, Zap, Shield } from 'lucide-react'

export default function Component() {
  const [mounted, setMounted] = useState(false)

  useEffect(() => {
    setMounted(true)
  }, [])

  if (!mounted) return null

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 to-gray-800 text-white">
      <header className="container mx-auto px-4 py-6">
        <nav className="flex justify-between items-center">
          <motion.div
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.5 }}
            className="text-2xl font-bold"
          >
            Preperly API
          </motion.div>
          <motion.div
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.5, delay: 0.2 }}
            className="space-x-4"
          >
            <a href="#" className="hover:text-blue-400 transition-colors">
              Docs
            </a>
            <a href="#" className="hover:text-blue-400 transition-colors">
              Pricing
            </a>
            <a href="#" className="hover:text-blue-400 transition-colors">
              Contact
            </a>
          </motion.div>
        </nav>
      </header>

      <main className="container mx-auto px-4 py-16">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8 }}
          className="text-center mb-16"
        >
          <h1 className="text-5xl font-bold mb-6">Welcome to Preperly API</h1>
          <p className="text-xl text-gray-300 mb-8">
            Discover and integrate powerful APIs to supercharge your applications
          </p>
          <motion.button
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            className="bg-blue-500 hover:bg-blue-600 text-white font-bold py-3 px-6 rounded-full inline-flex items-center"
          >
            Get Started
            <ChevronRight className="ml-2" />
          </motion.button>
        </motion.div>

        <div className="grid md:grid-cols-3 gap-8">
          {[
            { icon: Code, title: 'Easy Integration', description: 'Simple and straightforward API integration process' },
            { icon: Zap, title: 'Lightning Fast', description: 'High-performance APIs with minimal latency' },
            { icon: Shield, title: 'Secure by Design', description: 'Built-in security features to protect your data' },
          ].map((feature, index) => (
            <motion.div
              key={index}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.5, delay: index * 0.2 }}
              className="bg-gray-800 p-6 rounded-lg shadow-lg"
            >
              <feature.icon className="w-12 h-12 mb-4 text-blue-400" />
              <h2 className="text-xl font-semibold mb-2">{feature.title}</h2>
              <p className="text-gray-400">{feature.description}</p>
            </motion.div>
          ))}
        </div>

        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 1, delay: 1 }}
          className="mt-16 text-center"
        >
          <h2 className="text-3xl font-bold mb-4">Ready to get started?</h2>
          <p className="text-xl text-gray-300 mb-8">
            Explore our comprehensive API documentation and start building today!
          </p>
          <motion.button
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            className="bg-green-500 hover:bg-green-600 text-white font-bold py-3 px-6 rounded-full inline-flex items-center"
          >
            View Documentation
            <ChevronRight className="ml-2" />
          </motion.button>
        </motion.div>
      </main>

      <footer className="container mx-auto px-4 py-8 mt-16 border-t border-gray-800">
        <div className="flex justify-between items-center">
          <p className="text-gray-500">&copy; 2023 Preperly. All rights reserved.</p>
          <div className="space-x-4">
            <a href="#" className="text-gray-500 hover:text-blue-400 transition-colors">
              Terms
            </a>
            <a href="#" className="text-gray-500 hover:text-blue-400 transition-colors">
              Privacy
            </a>
          </div>
        </div>
      </footer>
    </div>
  )
}