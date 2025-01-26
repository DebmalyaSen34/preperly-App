# Preperly APIs

This is server which hosts all the essential **APIS** required by the Vendor to access the services of **Preperly**.

## Table of Contents

- [Registeration and Login APIs](#registration-and-login-apis)
- [Authentication APIs](#authentication-apis)
- [Update/Edit APIs](#updateedit-apis)

## Authentication APIs

### OTP Generation and Sending Endpoint

#### Endpoint

`POST /api/user/verification/sendOtp`

#### Request Body

The request body should be a `JSON` object containing following field:

- `mobileNumber` (string): The mobile number to which the otp is to be sent and only contain `10` digits.

Example:

```json
{
  "mobileNumber": "1234567890"
}
```

#### Response

The response will be a `JSON` object indication the success or failure of the OTP sending process.

##### Success Response

- `success` (boolean): Indicates whether the OTP was sent successfully.
- `message` (string): A message indication the result.
- `data` (object): The response data from `Fast2SMS API`.

Example:

```json
{
    "success": true,
    "message": "OTP send successfully",
    "data": {...}
}
```

##### Error response

- `success` (boolean): Indicates whether the OTP sending process failed.
- `message` (string): A message indication the error.
- `data` (object): The response data from `Fast2SMS API`, if availabe.

Example:

```json
{
    "success": false,
    "message": "Failed to send OTP",
    "data": {...}
}
```

#### Environment Variables

The following environment variables must be set for the API to function correct:

- `FAST2SMS_API_KEY`: Your Fast2SMS API key.
- `FAST2SMS_API_URL`: The FAST2SMS API URL.

#### Implementation Details

1. The API extracts the `mobileNumber` from the request body.
2. It generates a 6-digit OTP using the `otp-generator` library.
3. It sends the OTP to the specified mobile number using the Fast2SMS service.
4. If the OTP is sent successfully, it is stored in the database along with the mobile number and the number of attempts.
5. The API return a JSON reponse indicating the success or failure of the OTP sending process.

### OTP Verification Endpoint

#### Endpoint

`POST /api/user/verification/verifyOtp`

#### Request Body

The request body should be a `JSON` object containing the following fields:

- `mobileNumber` (string): The mobile number to which the OTP was sent.
- `userOtp` (string): The OTP entered by the user.

Example:

```json
{
  "mobileNumber": "1234567890",
  "userOtp": "123456"
}
```

#### Response

The response will be a `JSON` object indicating the success or failure of the OTP verification process.

##### Success Response

- `success` (boolean): Indicates whether the OTP was verified successfully.
- `message` (string): A message indication the result.

Example:

```json
{
  "success": true,
  "message": "OTP verified successfully"
}
```

##### Error response

- `success` (boolean): Indicates whether the OTP sent was correct.
- `message` (string): A message indication the error.

Example:

```json
{
  "success": false,
  "message": "Invalid OTP"
}
```

#### Implementation Details

1. The API extracts the `mobileNumber` and `userOtp` from the request body.
2. It validates the input format for the phone number and OTP.
3. It connects to the database and fetches the OTP record for the given phone number.
4. It checks if the OTP is expired (valid for 5 minutes).
5. If the OTP is valid and matches the user input, it deletes the OTP record and returns a success response.
6. If the OTP is invalid or expired, it returns an error response and increments the failed attempts count.

## Registration and Login APIs

### User Registration Endpoint

#### Endpoint

`POST /api/user/register`

#### Request Body

The request body should be a JSON object containing the following fields:

- `restaurantName` (string): The name of the restaurant.
- `restaurantAddress` (string): The address of the restaurant.
- `phoneNumber` (string): The phone number of the restaurant.
- `alternateNumber` (string, optional): An alternate phone number for the restaurant.
- `email` (string): The email address of the restaurant.
- `password` (string): The password for the user account.
- `ownerName` (string): The name of the restaurant owner.
- `ownerPhoneNumber` (string): The phone number of the restaurant owner.
- `ownerEmail` (string, optional): The email address of the restaurant owner.
- `receiveUpdatesOnWhatsApp` (boolean, optional): Whether the user wants to receive updates on WhatsApp.

Example:

```json
{
  "restaurantName": "My Restaurant",
  "restaurantAddress": "123 Main St",
  "phoneNumber": "1234567890",
  "alternateNumber": "0987654321",
  "email": "restaurant@example.com",
  "password": "securepassword",
  "ownerName": "John Doe",
  "ownerPhoneNumber": "1234567890",
  "ownerEmail": "owner@example.com",
  "receiveUpdatesOnWhatsApp": true
}
```

#### Response

The response will be a JSON object indicating the success or failure of the registration process.

##### Success Response
- `message` (string): A message indicating the result.

Example:

```json
{
    "message": "User is successfully registered!"
}
```
##### Error Response
- `message` (string): A message indicating the error.

Example:

```json
{
    "message": "User already exists!"
}
```

#### Implementation Details

1. The API extracts the user details from the request body.
2. It validates that all required fields are provided.
3. It connects to the database and checks if a user with the given phone number already exists.
4. If the user does not exist, it creates a new user record in the database.
5. The API returns a JSON response indicating the success or failure of the registration process.


## Update/Edit APIs

(To be written soon...)
