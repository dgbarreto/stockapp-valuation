import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.danilobarreto.stockapp.valuation.sample.SampleApp

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Valuation Sample") {
        SampleApp()
    }
}
