package com.example.ispoke.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        MyScreen(navController)
                    }
                    composable(
                        route = "module/{title}/{imageResId}",
                        arguments = listOf(
                            navArgument("title") { type = NavType.StringType },
                            navArgument("imageResId") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        ModuleScreen(
                            navController = navController,
                            title = backStackEntry.arguments?.getString("title") ?: "",
                            imageResId = backStackEntry.arguments?.getInt("imageResId") ?: 0
                        )
                    }
                    composable(
                        route = "gesture/{letter}/{imageResId}/{title}",
                        arguments = listOf(
                            navArgument("letter"){type = NavType.StringType},
                            navArgument("imageResId") { type = NavType.IntType },
                            navArgument("title") { type = NavType.StringType }
                        )
                    ){ backStackEntry ->
                        Gesture(
                            navController = navController,
                            imageResId = backStackEntry.arguments?.getInt("imageResId") ?: 0,
                            title =  backStackEntry.arguments?.getString("title") ?: "",
                            letter = backStackEntry.arguments?.getString("letter") ?: "")
                    }
                    composable(
                        route = "practice"
                    ) {
                        Practice(
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun MyScreen(navController: NavHostController) {

    val modules = listOf(
        ModuleItem("Animais",R.drawable.cao),
        ModuleItem("Alfabeto", R.drawable.alfabeto) ,
        ModuleItem("Veículos", R.drawable.engarrafamento),
        ModuleItem("Cozinha", R.drawable.hamburguer)
    )


    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape( 10.dp)),

                    shape = RoundedCornerShape(
                        topStart =0.dp,
                        topEnd = 0.dp,
                        bottomStart = 15.dp,
                        bottomEnd = 15.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "iSpoke" ,
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(5.dp)

                        )
                        Spacer(modifier = Modifier.weight(1f))


                        IconButton(onClick = { /*  Ir para perfil */ },

                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Perfil",
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .wrapContentHeight()
            ) {
                Surface(
                    modifier = Modifier
                        .width(IntrinsicSize.Min)
                        .align(Alignment.BottomCenter)
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Ícone 1
                        IconButton(onClick = { navController.navigate("home") }) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Home"
                            )
                        }

                        // Ícone 2
                        IconButton(onClick = {
                            navController.navigate("practice")
                        }) {
                            Icon(
                                imageVector = Icons.Default.Forum,
                                contentDescription = "Configurações"
                            )
                        }

                        // Ícone 3
                        IconButton(onClick = { /* Ir pnavControllerara Perfil*/  }) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Perfil"
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        // Conteúdo principal (GesturePanel como coluna)
        GesturePanel(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            modules = modules,
            navController = navController
        )
    }
}



data class ModuleItem(
    val title: String,
    val imageResId: Int
)
@Composable
fun ModulesGrid(modules: List<ModuleItem>, navController: NavController){
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Cria 4 itens no grid
        items((modules).toList()) { module ->
            MiniCard(
                logoResId = module.imageResId,
                text = module.title,
                navController = navController
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiniCard(logoResId: Int, text: String, navController: NavController) {

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        onClick = {
            if (text == "Alfabeto") {
                navController.navigate("module/$text/$logoResId")
            }
        }
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo
            Image(
                painter = painterResource(id = logoResId),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(50.dp)
                    .padding(5.dp)
                ,
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(10.dp))
            // Texto
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 20.sp
            )
        }
    }
}


@Composable
fun GesturePanel(modifier: Modifier = Modifier,
                 modules: List<ModuleItem>,
                 navController: NavController
){
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(25.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Imagem à esquerda
                Image(
                    painter = painterResource(id = R.drawable.ok),
                    contentDescription = "Imagem Principal",
                    modifier = Modifier.size(90.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Coluna com textos: título e descrição
                Column {
                    Text(
                        text = "0",
                        style = MaterialTheme.typography.headlineSmall,
                        fontSize = 80.sp
                    )

                    Text(
                        text = "Gestos aprendidos",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 20.sp
                    )
                }
            }
        }
        ModulesGrid(modules, navController)
}
}