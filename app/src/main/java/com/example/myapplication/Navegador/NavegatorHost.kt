import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.screens.Menu
import com.example.myapplication.screens.
import com.example.myapplication.PantallaConfigHorarios
import com.example.myapplication.PantallaMonitor

@Composable
fun NavegacionApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "menu") {


        composable("menu") {
            Menu(navController)
        }

        composable("config_leds") {
            PantallaConfigurarLeds(navController)
        }

        composable("config_horarios") {
            PantallaConfigHorarios(navController)
        }

        composable("monitor") {
            PantallaMonitor(navController)
        }
    }
}