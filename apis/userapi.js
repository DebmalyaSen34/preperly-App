// // server.js
// const express = require('express');
// const mongoose = require('mongoose');
// const bodyParser = require('body-parser');
// require('dotenv').config();

// // Create an Express application
// const app = express();
// app.use(express.json());

// // User Model
// const User = require("/models/userdata");

// // Connect to MongoDB using the connection string from environment variables
// mongoose.connect(process.env.MONGODB_URI)
//     .then(() => console.log('MongoDB connected successfully'))
//     .catch(err => console.log('MongoDB connection error:', err));

// // API endpoint to create a user
// app.post('/api/users', async (req, res) => {
//     try {
//         const { restaurantName,restaurantAddress,phoneNumber,alternateNumber,email,password,ownerName,ownerPhoneNumber,ownerEmail,receiveUpdatesOnWhatsApp } = req.body;
        
//         // Check if the user already exists
//         const existingUser = await User.findOne({ email });
//         if (existingUser) {
//             return res.status(400).json({ message: 'User with this email already exists' });
//         }

//         // Create a new user instance
//         const newUser = new User({restaurantName,restaurantAddress,phoneNumber,alternateNumber,email,password,ownerName,ownerPhoneNumber,ownerEmail,receiveUpdatesOnWhatsApp});

//         // Save the user to the database
//         await newUser.save();
//         res.status(201).json({ message: 'User created successfully', user: newUser });
//     } catch (err) {
//         console.error('Error creating user:', err);
//         res.status(500).json({ message: 'Error creating user', error: err });
//     }
// });

// // Define other API routes here (e.g., read, update, delete users)
// // ...

// // Start the server
// const PORT = process.env.PORT || 6000;
// app.listen(PORT, () => {
//     console.log(`Server is running on port ${PORT}`);
// });

// apis/userapi.js

const express = require('express');
const User = require('../models/userdata'); // Make sure this path is correct
const router = express.Router(); // Create a new router

// API endpoint to create a user
router.post('/', async (req, res) => {
    try {
        const {
            restaurantName,
            restaurantAddress,
            phoneNumber,
            alternateNumber,
            email,
            password,
            ownerName,
            ownerPhoneNumber,
            ownerEmail,
            receiveUpdatesOnWhatsApp
        } = req.body;

        // Check if the user already exists
        const existingUser = await User.findOne({ email });
        if (existingUser) {
            return res.status(400).json({ message: 'User with this email already exists' });
        }

        // Create a new user instance
        const newUser = new User({
            restaurantName,
            restaurantAddress,
            phoneNumber,
            alternateNumber,
            email,
            password,
            ownerName,
            ownerPhoneNumber,
            ownerEmail,
            receiveUpdatesOnWhatsApp
        });

        // Save the user to the database
        await newUser.save();
        res.status(201).json({ message: 'User created successfully', user: newUser });
    } catch (err) {
        console.error('Error creating user:', err);
        res.status(500).json({ message: 'Error creating user', error: err });
    }
});

module.exports = router; // Export the router
