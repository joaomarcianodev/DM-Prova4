package com.jams.mediadecombustivel

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import com.jams.mediadecombustivel.ui.theme.FuelCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FuelCalculatorTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FuelCalculatorScreen()
                }
            }
        }
    }
}

// --- Tela Principal da Calculadora ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelCalculatorScreen(modifier: Modifier = Modifier) {

    var isAnaliseCompleta by remember { mutableStateOf(true) }
    var kmInicial by remember { mutableStateOf("") }
    var kmFinal by remember { mutableStateOf("") }
    var litrosAbastecidos by remember { mutableStateOf("") }
    var valorPorLitroManual by remember { mutableStateOf("") }

    // Tipo de Combustível
    val tiposCombustivel = listOf("Gasolina", "Diesel")
    var expandedCombustivel by remember { mutableStateOf(false) }
    var tipoSelecionado by remember { mutableStateOf(tiposCombustivel[0]) }

    // UF
    val ufs = listOf(
        "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA",
        "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN",
        "RS", "RO", "RR", "SC", "SP", "SE", "TO"
    )
    var expandedUf by remember { mutableStateOf(false) }
    var ufSelecionada by remember { mutableStateOf("MG") }



    // Mensagens de Validação
    var kmInicialError by remember { mutableStateOf<String?>(null) }
    var kmFinalError by remember { mutableStateOf<String?>(null) }
    var litrosError by remember { mutableStateOf<String?>(null) }
    var valorLitroError by remember { mutableStateOf<String?>(null) }

    var statusMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current


    // --- Layout da UI ---
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título
        Text(
            text = "Calcular Média de Consumo de Combustível",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(all = 16.dp,),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Seletor de Tipo de Análise (Radio Button)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Análise Simples
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    isAnaliseCompleta = false
                    statusMessage = ""
                }
            ) {
                RadioButton(
                    selected = !isAnaliseCompleta,
                    onClick = null
                )
                Text("Análise Simples", modifier = Modifier.padding(end = 16.dp))
            }

            // Análise Completa
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    isAnaliseCompleta = true
                    statusMessage = ""
                }
            ) {
                RadioButton(
                    selected = isAnaliseCompleta,
                    onClick = null
                )
                Text("Análise Completa", modifier = Modifier.padding(end = 16.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Linha para KM Inicial e Final (Sempre visível)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = kmInicial,
                onValueChange = { kmInicial = it },
                label = { Text("KM Inicial") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                isError = kmInicialError != null,
                supportingText = {
                    if (kmInicialError != null) {
                        Text(text = kmInicialError!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            )
            OutlinedTextField(
                value = kmFinal,
                onValueChange = { kmFinal = it },
                label = { Text("KM Final") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                isError = kmFinalError != null,
                supportingText = {
                    if (kmFinalError != null) {
                        Text(text = kmFinalError!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }

        // Campo para Quantidade de Litros Abastecidod (Sempre visível)
        OutlinedTextField(
            value = litrosAbastecidos,
            onValueChange = { newValue ->
                val regex = "^\\d*([.,]?\\d{0,3})?$".toRegex()
                if (newValue.isEmpty() || newValue.matches(regex)) {
                    litrosAbastecidos = newValue
                }
            },
            label = { Text("Total Abastecido (Litros)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            isError = litrosError != null,
            supportingText = {
                if (litrosError != null) {
                    Text(text = litrosError!!, color = MaterialTheme.colorScheme.error)
                }
            }
        )

        // --- LÓGICA CONDICIONAL PARA O FORMULÁRIO ---
        if (isAnaliseCompleta) {

            // Campo para Valor por Litro (Manual)
            OutlinedTextField(
                value = valorPorLitroManual,
                onValueChange = { newValue ->
                    val regex = "^\\d*([.,]?\\d{0,3})?$".toRegex()
                    if (newValue.isEmpty() || newValue.matches(regex)) {
                        valorPorLitroManual = newValue
                    }
                },
                label = { Text("Valor por Litro (R$)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                isError = valorLitroError != null,
                supportingText = {
                    if (valorLitroError != null) {
                        Text(text = valorLitroError!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            // Linha para Seletores (Combustível e UF)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Dropdown (Select) para Tipo de Combustível
                ExposedDropdownMenuBox(
                    expanded = expandedCombustivel,
                    onExpandedChange = { expandedCombustivel = !expandedCombustivel },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = tipoSelecionado,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Combustível") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCombustivel)
                        },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCombustivel,
                        onDismissRequest = { expandedCombustivel = false }
                    ) {
                        tiposCombustivel.forEach { tipo ->
                            DropdownMenuItem(
                                text = { Text(tipo) },
                                onClick = {
                                    tipoSelecionado = tipo
                                    expandedCombustivel = false
                                }
                            )
                        }
                    }
                }

                // Dropdown (Select) para UF
                ExposedDropdownMenuBox(
                    expanded = expandedUf,
                    onExpandedChange = { expandedUf = !expandedUf },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = ufSelecionada,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("UF") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUf)
                        },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedUf,
                        onDismissRequest = { expandedUf = false }
                    ) {
                        ufs.forEach { uf ->
                            DropdownMenuItem(
                                text = { Text(uf) },
                                onClick = {
                                    ufSelecionada = uf
                                    expandedUf = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botão para enviar os dados
        Button(
            onClick = {

                // Resetar todos os erros
                kmInicialError = null
                kmFinalError = null
                litrosError = null
                valorLitroError = null
                statusMessage = ""
                var isValid = true

                // VALIDAÇÃO INDIVIDUAL DOS CAMPOS

                // KM inicial
                val kmInicialNum = kmInicial.replace(",", ".").toDoubleOrNull()
                if (kmInicial.isBlank()) {
                    kmInicialError = "Campo obrigatório"
                    isValid = false
                } else if (kmInicialNum == null) {
                    kmInicialError = "Valor numérico inválido"
                    isValid = false
                }

                // KM final
                val kmFinalNum = kmFinal.replace(",", ".").toDoubleOrNull()
                if (kmFinal.isBlank()) {
                    kmFinalError = "Campo obrigatório"
                    isValid = false
                } else if (kmFinalNum == null) {
                    kmFinalError = "Valor numérico inválido"
                    isValid = false
                }

                // Validação cruzada (só executa se os campos individuais forem válidos)
                if (kmInicialNum != null && kmFinalNum != null && kmFinalNum <= kmInicialNum) {
                    kmInicialError = "Deve ser menor que o KM Final"
                    kmFinalError = "Deve ser maior que o KM Inicial"
                    isValid = false
                }

                //Qtd de Litros
                val litrosNum = litrosAbastecidos.replace(",", ".").toDoubleOrNull()
                if (litrosAbastecidos.isBlank()) {
                    litrosError = "Campo obrigatório"
                    isValid = false
                } else if (litrosNum == null) {
                    litrosError = "Valor numérico inválido"
                    isValid = false
                } else if (litrosNum <= 0) {
                    litrosError = "Deve ser maior que zero"
                    isValid = false
                }

                // Valor do litro
                var valorLitroNum: Double? = null
                if (isAnaliseCompleta) {
                    valorLitroNum = valorPorLitroManual.replace(",", ".").toDoubleOrNull()
                    if (valorPorLitroManual.isBlank()) {
                        valorLitroError = "Campo obrigatório"
                        isValid = false
                    } else if (valorLitroNum == null) {
                        valorLitroError = "Valor numérico inválido"
                        isValid = false
                    } else if (valorLitroNum <= 0) {
                        valorLitroError = "Deve ser maior que zero"
                        isValid = false
                    }
                }

                // Parar execução se houver qualquer erro
                if (!isValid) {
                    return@Button
                }

                // Cálculo Simples
                val kmPercorrido = kmFinalNum!! - kmInicialNum!!
                val media = kmPercorrido / litrosNum!!

                // Cálculo Completo
                if (isAnaliseCompleta) {
                    val valorGastoExato = valorLitroNum!! * litrosNum!!

                    statusMessage = "Buscando dados da API..."
                    scope.launch(Dispatchers.IO) {
                        var precoApiNum: Double? = null
                        var valorGastoEstimado: Double? = null
                        var apiError: String? = null

                        try {
                            // --- MUDANÇA: fetchApiData() agora retorna o JSON COMPLETO ---
                            val fullResponse = fetchApiData()

                            // --- MUDANÇA: Extraímos o "precos" aqui ---
                            val precos = fullResponse.getJSONObject("precos")

                            val tipoApi = tipoSelecionado.lowercase()
                            val ufApi = ufSelecionada.lowercase()

                            if (precos.has(tipoApi)) {
                                val precosCombustivel = precos.getJSONObject(tipoApi)
                                if (precosCombustivel.has(ufApi)) {
                                    val precoApiString = precosCombustivel.getString(ufApi)
                                    precoApiNum = precoApiString.replace(",", ".").toDoubleOrNull()
                                    if (precoApiNum != null) {
                                        valorGastoEstimado = precoApiNum * litrosNum
                                    }
                                }
                            }
                            Log.i("FuelCalculator", "Dados da API recebidos: $precos")
                        } catch (e: Exception) {
                            Log.e("FuelCalculator", "Erro ao buscar API: ${e.message}", e)
                            apiError = e.message ?: "Falha desconhecida."
                        }

                        // Navegação para a tela de resultados
                        launch(Dispatchers.Main) {
                            statusMessage = apiError ?: "Cálculo completo realizado."
                            val intent = Intent(context, ResultActivity::class.java).apply {
                                putExtra("KM_PERCORRIDO", kmPercorrido)
                                putExtra("MEDIA_CONSUMO", media)
                                putExtra("ANALISE_TIPO", "Completa")
                                putExtra("VALOR_GASTO_EXATO", valorGastoExato)
                                putExtra("VALOR_GASTO_ESTIMADO", valorGastoEstimado ?: Double.NaN)
                                putExtra("PRECO_API", precoApiNum ?: Double.NaN)
                                putExtra("TIPO_COMBUSTIVEL", tipoSelecionado)
                                putExtra("UF_SELECIONADA", ufSelecionada)
                                putExtra("API_ERROR", apiError)
                            }
                            context.startActivity(intent)
                            statusMessage = ""
                        }
                    }
                } else {

                    // Análise Simples
                    statusMessage = "Cálculo simples realizado."
                    val intent = Intent(context, ResultActivity::class.java).apply {
                        putExtra("KM_PERCORRIDO", kmPercorrido)
                        putExtra("MEDIA_CONSUMO", media)
                        putExtra("ANALISE_TIPO", "Simples")
                    }
                    context.startActivity(intent)
                    statusMessage = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Enviar Dados")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                statusMessage = "Buscando informações da API..."
                scope.launch(Dispatchers.IO) {
                    try {
                        // Chama a mesma função refatorada
                        val fullResponse = fetchApiData()

                        // Sucesso, abre a nova tela
                        launch(Dispatchers.Main) {
                            statusMessage = "Informações recebidas!"
                            val intent = Intent(context, ApiInfoActivity::class.java).apply {
                                putExtra("API_DATA_STRING", fullResponse.toString())
                            }
                            context.startActivity(intent)
                            statusMessage = ""
                        }
                    } catch (e: Exception) {
                        // Falha
                        launch(Dispatchers.Main) {
                            statusMessage = e.message ?: "Falha desconhecida."
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Informações da API")
        }

        // Texto de Status (Validação ou API)
        if (statusMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = statusMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = if (statusMessage.startsWith("Falha") || statusMessage.startsWith("Erro")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

// --- Função de Busca na API ---
@Throws(Exception::class)
private fun fetchApiData(): JSONObject {
    val url = URL("https://combustivelapi.com.br/api/precos/")
    var connection: HttpsURLConnection? = null
    var responseJson: JSONObject? = null

    try {
        connection = url.openConnection() as HttpsURLConnection
        connection.requestMethod = "GET"
        connection.instanceFollowRedirects = true
        connection.connect()

        val responseCode = connection.responseCode
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            val inputStream = connection.inputStream
            val reader = InputStreamReader(inputStream, "UTF-8")
            val responseString = reader.readText()
            reader.close()
            inputStream.close()
            responseJson = JSONObject(responseString)
        } else {
            val errorString = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "Falha desconhecida."
            Log.e("FuelCalculator", "API Erro $responseCode: $errorString")
            throw Exception("Falha de comunicação com a API.")
        }
    } catch (e: Exception) {
        throw Exception(e.message ?: "Falha de rede desconhecida.")
    } finally {
        connection?.disconnect()
    }
    return responseJson ?: throw Exception("Falha ao obter dados da API.")
}

// --- Tema Básico ---
@Composable
fun FuelCalculatorTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = lightColorScheme(),
        typography = Typography(),
        content = content
    )
}