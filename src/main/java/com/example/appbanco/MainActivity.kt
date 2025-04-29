package com.example.appbanco

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.appbanco.ui.theme.AppBancoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppBancoTheme {
                AppNavegacion()
            }
        }
    }
}

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()
    var saldoRestante by remember { mutableStateOf(1000.0f) }

    NavHost(navController = navController, startDestination = "retiro") {
        composable("retiro") {
            PantallaRetiro(navController, saldoRestante) { saldo ->
                saldoRestante = saldo
            }
        }
        composable(
            "comprobante/{monto}/{saldoRestante}",
            arguments = listOf(
                navArgument("monto") { type = NavType.FloatType },
                navArgument("saldoRestante") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val monto = backStackEntry.arguments?.getFloat("monto") ?: 0f
            val saldo = backStackEntry.arguments?.getFloat("saldoRestante") ?: 0f
            PantallaComprobante(navController, monto, saldo)
        }
    }
}

@Composable
fun PantallaRetiro(
    navController: NavController,
    saldoRestante: Float,
    onSaldoUpdate: (Float) -> Unit
) {
    var montoTexto by remember { mutableStateOf("") }
    var montoIngresoTexto by remember { mutableStateOf("") }
    var mostrarErrorRetiro by remember { mutableStateOf(false) }
    var mostrarErrorIngreso by remember { mutableStateOf(false) }
    var mensajeErrorIngreso by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Saldo actual: $${"%.2f".format(saldoRestante)}",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Retiro
        OutlinedTextField(
            value = montoTexto,
            onValueChange = {
                montoTexto = it
                mostrarErrorRetiro = false
            },
            label = { Text("Monto a retirar") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        if (mostrarErrorRetiro) {
            Text(
                text = "El monto supera el saldo disponible.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            val monto = montoTexto.toFloatOrNull()
            if (monto != null && monto > 0 && monto <= saldoRestante) {
                val nuevoSaldo = saldoRestante - monto
                onSaldoUpdate(nuevoSaldo)
                navController.navigate("comprobante/$monto/$nuevoSaldo")
            } else {
                mostrarErrorRetiro = true
            }
        }) {
            Text("Retirar")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Ingreso
        OutlinedTextField(
            value = montoIngresoTexto,
            onValueChange = {
                montoIngresoTexto = it
                mostrarErrorIngreso = false
                mensajeErrorIngreso = ""
            },
            label = { Text("Monto a ingresar") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        if (mostrarErrorIngreso) {
            Text(
                text = mensajeErrorIngreso,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            val montoIngreso = montoIngresoTexto.toFloatOrNull()
            when {
                montoIngreso == null || montoIngreso <= 0 -> {
                    mostrarErrorIngreso = true
                    mensajeErrorIngreso = "Ingrese un monto válido mayor a 0."
                }
                montoIngreso > 5000 -> {
                    mostrarErrorIngreso = true
                    mensajeErrorIngreso = "No tienes suficiente dinero para ingresar"
                }
                saldoRestante + montoIngreso > 10_000 -> {
                    mostrarErrorIngreso = true
                    mensajeErrorIngreso = "El saldo no puede superar los $10.000"
                }
                else -> {
                    val nuevoSaldo = saldoRestante + montoIngreso
                    onSaldoUpdate(nuevoSaldo)
                    montoIngresoTexto = ""
                }
            }
        }) {
            Text("Ingresar")
        }
    }
}




@Composable
fun PantallaComprobante(navController: NavController, monto: Float, saldoRestante: Float) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Retiro exitoso",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Monto retirado: $${"%.2f".format(monto)}",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Saldo restante: $${"%.2f".format(saldoRestante)}",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {
            navController.popBackStack()
        }) {
            Text("Volver a la pantalla principal")
        }
    }
}





