import androidx.lifecycle.ViewModel
import com.example.ispoke.android.classes.ModuleItem

open class SharedViewModel : ViewModel() {
    open var modules: List<ModuleItem> = emptyList()
}
