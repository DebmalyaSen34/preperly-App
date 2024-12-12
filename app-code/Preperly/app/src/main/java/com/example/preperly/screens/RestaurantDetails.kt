package com.example.preperly.screens


import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.preperly.ui.theme.myRed
import com.example.preperly.viewmodels.RestaurantDetailsViewModel

@Composable
fun RestaurantRegistrationForm(
    viewModel: RestaurantDetailsViewModel,
    onNext: () -> Unit) {

    val restaurantName by viewModel.restaurantName
    val restaurantNameError by viewModel.restaurantNameError

    val restaurantAddress by viewModel.restaurantAddress
    val restaurantAddressError by viewModel.restaurantAddressError

    val phoneNumber by viewModel.phoneNumber
    val phoneNumberError by viewModel.phoneNumberError

    var phoneOtp by rememberSaveable { mutableStateOf("") }
    val isRequestingPhoneOtp by viewModel.isRequestingPhoneOtp
    val phoneOtpStatus by viewModel.phoneOtpStatus

    val alternateNumber by viewModel.alternateNumber
    val alternateNumberError by viewModel.alternateNumberError

    val email by viewModel.email
    val emailError by viewModel.emailError

    var emailOtp by rememberSaveable { mutableStateOf("") }

    val password by viewModel.password
    val passwordError by viewModel.passwordError

    val confirmPassword by viewModel.confirmPassword
    val confirmPasswordError by viewModel.confirmPasswordError

    val ownerName by viewModel.ownerName
    val ownerNameError by viewModel.ownerNameError

    val ownerPhoneNumber by viewModel.ownerPhoneNumber
    val ownerPhoneError by viewModel.ownerPhoneError

    val ownerEmail by viewModel.ownerEmail
    val ownerEmailError by viewModel.ownerEmailError

    val receiveUpdatesOnWhatsApp by viewModel.receiveUpdatesOnWhatsApp

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 50.dp, start = 20.dp, end = 20.dp, bottom = 20.dp)
            ) {
                ProgressIndicator(viewModel.currentStep)

                Text(
                    "Restaurant details",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                OutlinedTextField(
                    value = restaurantName,
                    onValueChange = {viewModel.updateRestaurantName(it)},
                    label = { Text("Name*") },
                    placeholder = { Text("Restaurant name") },
                    isError = restaurantNameError != null,
                    modifier = Modifier.fillMaxWidth()
                )

                restaurantNameError?.let {
                    Text(text = it, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = restaurantAddress,
                    onValueChange = {viewModel.updateRestaurantAddress(it)},
                    label = { Text("Address*") },
                    placeholder = { Text("Restaurant address") },
                    isError = restaurantAddressError != null,
                    modifier = Modifier.fillMaxWidth()
                )
                restaurantAddressError?.let {
                    Text(text = it, color = Color.Red, fontSize = 12.sp)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                Text(
                    "Restaurant contact details*",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { viewModel.updatePhoneNumber(it) },
                            label = { Text("Phone number*") },
                            placeholder = { Text("Phone number") },
                            isError = phoneNumberError != null,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )

                        phoneNumberError?.let {
                            Text(
                                text = it,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick =
                        {
                            viewModel.sendOtp()
                            Toast.makeText(context,"requesting...",Toast.LENGTH_SHORT).show()

                        },
                        enabled = !isRequestingPhoneOtp,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = myRed),
                        modifier = Modifier.align(Alignment.Bottom)
                    ) {
                        Text("Verify")
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    OutlinedTextField(
                        value = phoneOtp,
                        onValueChange = {phoneOtp = it},
                        label = { Text("OTP") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            viewModel.verifyOtp(phoneOtp)
                            Toast.makeText(context,"Verifying.....",Toast.LENGTH_SHORT).show()
                        },

                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = myRed),
                        modifier = Modifier.align(Alignment.Bottom)
                    ) {
                        if(phoneOtpStatus){
                            Toast.makeText(context,"Verified",Toast.LENGTH_SHORT).show()
                            Row(horizontalArrangement = Arrangement.SpaceBetween){
                                Text(
                                    "Success",
                                    fontSize = 14.sp,
                                    fontStyle = FontStyle.Italic
                                )
                                Image(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "completed",
                                    colorFilter = ColorFilter.tint(Color.Green))
                            }
                        }else{
                            Text("Confirm")
                        }

                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween){
                    Column(modifier = Modifier.weight(1f)){
                        OutlinedTextField(
                            value = alternateNumber,
                            onValueChange = {viewModel.updateAlternateNumber(it)},
                            label = { Text("Alternate number") },
                            placeholder = { Text("Alternate Phone number") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = alternateNumberError != null,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )

                        if(!viewModel.validateAlternatePhone()){
                            phoneNumberError?.let {
                                Text(
                                    text = it,
                                    color = Color.Red,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { viewModel.updateEmail(it) },
                            label = { Text("Email*") },
                            placeholder = { Text("Email") },
                            isError = emailError != null,
                            modifier = Modifier.fillMaxWidth()
                        )

                        emailError?.let {
                            Text(
                                text = it,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { /* Your email verification logic */ },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = myRed),
                        modifier = Modifier.align(Alignment.Bottom)
                    ) {
                        Text("Verify")
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextField(
                        value = emailOtp,
                        onValueChange = {emailOtp = it},
                        label = { Text("OTP") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = myRed),
                        modifier = Modifier.align(Alignment.Bottom)
                    ) {
                        Text("Confirm")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = {viewModel.updatePassword(it)},
                    label = { Text("Set Password*") },
                    placeholder = { Text("Enter password") },
                    isError = passwordError != null,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                passwordError?.let {
                    Text(text = it, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {viewModel.updateConfirmPassword(it)},
                    label = { Text("Confirm Password*") },
                    placeholder = { Text("Enter password") },
                    visualTransformation = PasswordVisualTransformation(),
                    isError = confirmPasswordError != null,
                    modifier = Modifier.fillMaxWidth()
                )

                confirmPasswordError?.let {
                    Text(text = it, color = Color.Red, fontSize = 12.sp)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                Text(
                    "Restaurant owner details",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = ownerName,
                    onValueChange = {viewModel.updateOwnerName(it)},
                    label = { Text("Name*") },
                    placeholder = { Text("Name") },
                    isError = ownerNameError !=null,
                    modifier = Modifier.fillMaxWidth()
                )
                ownerNameError?.let {
                    Text(text = it, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = ownerPhoneNumber,
                    onValueChange = {viewModel.updateOwnerPhoneNumber(it)},
                    label = { Text("Phone number*") },
                    placeholder = { Text("Phone number") },
                    isError = ownerPhoneError != null,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                ownerPhoneError?.let {
                    Text(text = it, color = Color.Red, fontSize = 12.sp)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Checkbox(
                        checked = receiveUpdatesOnWhatsApp,
                        onCheckedChange = {viewModel.updateReceiveUpdatesOnWhatsApp(it)}
                    )
                    Text("I would like to receive important updates in WhatsApp")
                }

                OutlinedTextField(
                    value = ownerEmail,
                    onValueChange = {viewModel.updateOwnerEmail(it)},
                    label = { Text("Email") },
                    placeholder = { Text("Email") },
                    isError = ownerEmailError !=null,
                    modifier = Modifier.fillMaxWidth()
                )

                if(!viewModel.validateOwnerEmail()){
                    ownerEmailError?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
//                        if(viewModel.validateForm() && viewModel.validateAlternatePhone() && viewModel.validateOwnerEmail()){
//                            viewModel.registerUser()
//                            onNext()
//                        }
                        onNext()
                        Toast.makeText(context,"Clicked",Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = myRed),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp)
                ) {
                    Text("Next")
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun RestaurantRegistrationFormPreview() {
    val navController = rememberNavController()
    MaterialTheme {
        RestaurantRegistrationForm(
            viewModel = RestaurantDetailsViewModel(),
            onNext = {navController.navigate("step2")}
        )
    }
}