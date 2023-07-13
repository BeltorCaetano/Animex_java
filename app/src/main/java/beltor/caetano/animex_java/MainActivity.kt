package beltor.caetano.animex_java

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat.startActivity
import beltor.caetano.animex_java.ui.theme.Animex_javaTheme

class MainActivity : ComponentActivity() {
    //views
    var mRegisterBtn: Button? = null;
    var mLoginBtn: Button? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Animex_javaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
                //init views
                mRegisterBtn = findViewById(R.id.register_btn)
                mLoginBtn = findViewById<Button>(R.id.login_btn)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    //handle register button click

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Animex_javaTheme {
        loginUI()
    }
}