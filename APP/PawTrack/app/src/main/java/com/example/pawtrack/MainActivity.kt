package com.example.pawtrack

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition{
                !viewModel.isReady.value
            }
            setOnExitAnimationListener{
                    screen -> val zoomX = ObjectAnimator.ofFloat(
                screen.iconView,
                View.SCALE_X,
                0.4f,
                0.0f
            )
                zoomX.interpolator = OvershootInterpolator()
                zoomX.duration = 500L
                zoomX.doOnEnd { screen.remove() }
                val zoomY = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_X,
                    0.4f,
                    0.0f
                )
                zoomY.interpolator = OvershootInterpolator()
                zoomY.duration = 500L
                zoomY.doOnEnd { screen.remove() }

                zoomX.start()
                zoomY.start()
            }
        }
        super.onCreate(savedInstanceState)

        val (token, username) = getToken(applicationContext)
        if (token.isNullOrEmpty() || username.isNullOrEmpty()) {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
            finish()
        } else {
            val homeIntent = Intent(this, HomePageActivity::class.java).apply {
                putExtra("USERNAME", username)
            }
            startActivity(homeIntent)
            finish()
        }
        // Initialize osmdroid configuration
        //Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE))
    }
    fun openMap(view: View) {
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
    }

    fun onSetReminderButtonClick(view: View) {
        // Start the ReminderSettingActivity
        val intent = Intent(this, ReminderSettingActivity::class.java)
        startActivity(intent)
    }

    fun getToken(context: Context): Triple<String?, String?, String?> {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val sharedPreferences = EncryptedSharedPreferences.create(
            "user_preferences",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val token = sharedPreferences.getString("user_token", null)
        val username = sharedPreferences.getString("USERNAME", null)
        val pet_id = sharedPreferences.getString("PET_ID", null)
        return Triple(token, username, pet_id)
    }

}
//Opens the map


//Empty function frames:

//Login function
//Returns success factor
fun LoginUser():Boolean{
    return true
}

//Logout function
//Returns success factor
fun LogoutUser():Boolean{
    return true
}

//Register function
//Returns success factor
fun RegisterUser():Boolean{
    return true
}

//Password recovery function
//Returns success factor
fun ForgotPassword():Boolean{
    return true
}

//Extends premium account status
//Returns success factor
fun ExtendPremiumAccount():Boolean{
    return true
}

//User account deletion function
//Returns success factor
fun DeleteUser():Boolean{
    return true
}

//Profile edit function
//Returns success factor
fun EditProfile():Boolean{
    return true
}

//Profile view function
//Returns success factor
fun ViewProfile(){
    //to implement
}

//Pet Register function
//Returns success factor
fun RegisterPet():Boolean{
    return true
}

//Pet delete function
//Returns success factor
fun DeletePet():Boolean{
    return true
}

//Pet edit function
//Returns success factor
fun EditPet():Boolean{
    return true
}

//Pet view function
fun ViewPet(){
    //to implement
}

//Pet diet view function
fun ViewDietPet(){
    //to implement
}

//Create Pet diet  function
//Returns success factor
fun CreateDietPet():Boolean{
    return true
}

//Edit Pet diet  function
//Returns success factor
fun EditDietPet():Boolean{
    return true
}

//Delete Pet diet  function
//Returns success factor
fun DeleteDietPet():Boolean{
    return true
}

//Start activity function
//Returns success factor
fun StartActivity():Boolean{
    return true
}

//Submit activity function
//Returns success factor
fun SubmitActivity():Boolean{
    return true
}

//View Activity report function
fun ViewActivityReport(){
    //to implement
}

//Get activity data function
//Returns success factor
fun GetActivityData():Boolean{
    return true
}

//Processes data from the controller on the pet
fun ProcessControllerData(){
    //to implement
}

//Lists all users
fun ListAllUsers(){
    //to implement
}

//Views specific user account
fun ViewUserAccount(){
    //to implement
}

//Deletes specific user account
//Returns success factor
fun DeleteUserAccount():Boolean{
    return true
}