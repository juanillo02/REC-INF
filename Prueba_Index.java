//import org.tartarus.snowball.ext.PorterStemmer;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

import java.io.*;
import java.nio.file.*;
import java.io.File;
import java.util.*;
import java.lang.*;
import java.util.stream.Collectors;

import org.json.*;

public class Prueba_Index {
    public static void main(String[] args) throws IOException {
        File carpeta = new File("corpus");
        List<String> rutasTxt = Rutas(carpeta); //Llamamos a la funcion que obtendra las rutas de los ficheros
        int cont = 0;
        Map<String, Map<Double, Map<String, Double>>> Indice_Invertido = new HashMap<>();
        List<String> texto_procesado = null;

        for (String ruta : rutasTxt) //Recorremos todos los archivos y los procesamos
        {
            String texto = new String(Files.readAllBytes(Paths.get(ruta))); //Leemos el texto del fichero
            texto_procesado = Procesar(texto); //Procesamos el texto
            //System.out.println("Escribiendo fichero " + (cont+1) + " ...");
            cont++;
            Map<String, Integer> termsFrecuencia = CalcularTF(texto_procesado);
            //Para eliminar corpus/00... del nombre del documento
            String DocNombre = ruta.replace("corpus\\", "");
            Map<String, Map<Double, Map<String, Double>>> terminoDocumentoPeso = CalcularTF_Paso2(termsFrecuencia, DocNombre);

            // Actualizar el índice invertido acumulando la información de cada documento
            for (Map.Entry<String, Map<Double, Map<String, Double>>> entry : terminoDocumentoPeso.entrySet()) {
                String termino = entry.getKey();
                Map<Double, Map<String, Double>> documentoPeso = entry.getValue();

                //Dentro del bucle interno, para cada término, verificamos si ya existe en Indice_Invertido. Si no existe, lo creamos como un nuevo HashMap.
                Indice_Invertido.computeIfAbsent(termino, k -> new HashMap<>());

                for (Map.Entry<Double, Map<String, Double>> subEntry : documentoPeso.entrySet()) {
                    Double idf = subEntry.getKey();
                    Map<String, Double> docInfo = subEntry.getValue();

                    // Ensure the structure is initialized
                    Indice_Invertido.computeIfAbsent(termino, k -> new HashMap<>());
                    Indice_Invertido.get(termino).computeIfAbsent(idf, k -> new HashMap<>());

                    // Add the information to the nested map
                    Indice_Invertido.get(termino).get(idf).putAll(docInfo);
                }


            }
        }
        CalcularIDF(Indice_Invertido, cont);
        EscribirEnFicheroIndiceInvertidoJSON(Indice_Invertido);
        EscribirEnFicheroIndiceInvertido(Indice_Invertido);

        Map<String, Double> longDocumentos;
        longDocumentos = calcularLongitudDocumentos(Indice_Invertido);
        Escribir_fichero(longDocumentos);
        List<String> docRecuperados = recuperarDocumentos(texto_procesado);
    }

    //////////////////////////////////////////////////////// RUTAS //////////////////////////////////////////////////////////////////////////////////
    public static List<String> Rutas(File carpeta) //Funcion para guardar las rutas de todos los ficheros
    {
        File[] archivos = carpeta.listFiles(); //Este método devuelve un array de objetos File que representan los archivos de la carpeta.

        List<String> rutas_archivos = new ArrayList<>(); //Creamos una lista de cadenas para guardar las rutas de cada archivo

        assert archivos != null;
        for (File archivo : archivos) //Recorremos toda la carpeta
        {
            rutas_archivos.add(String.valueOf(archivo)); //Convertimos a cadena la ruta del archivo y lo añadimos a la lista
        }

        return rutas_archivos;
    }

    ///////////////////////////////////////////////////// PROCESAR TEXTO /////////////////////////////////////////////////////////////////////////////////////
    public static List<String> Procesar(String texto) throws IOException {
        // ------------------------------- ELIMINAR SIMBOLOS ------------------------------
        String texto_procesado = texto.replaceAll("[.,¿?¡!=0-9:()]", " "); //Sustituimos ciertos caracteres por espacios


        // -------------------------------- PALABRAS VACIAS ----------------------------------
        //Hemos guardado todas las palabras vacias inglesas en un txt por lo que las recorremos y guardamos en una lista
        File archivo = new File("stop_words_english.txt");
        BufferedReader br = new BufferedReader(new FileReader(archivo)); //Usaremos este archivo de lectura
        List<String> palabras_vacias = new ArrayList<>();

        String linea;
        while ((linea = br.readLine()) != null) {
            palabras_vacias.add(linea);
        }

        // --------------------------------  MINIMO DE LETRAS --------------------------------
        int tamano_minimo = 3; //Usaremos 3 letras como tamaño minimo
        String[] palabras = texto_procesado.split("\\s+"); //Separamos el texto en palabras (separando por espacios)

        // ----------------------------------- ELIMINAR PALABRAS------------------------------------

        List<String> palabras_fin = new ArrayList<>(); //Creamos una lista donde almacenar todas las palabras que no borramos

        for (String palabra : palabras) //Recorremos las palabras que hemos separado antes
        {
            if (palabra.length() >= tamano_minimo) //Si la palabra >= 3 entonces puede introducirse en el texto, comprobamos que no sea vacia:
            {
                if (!palabras_vacias.contains(palabra.toLowerCase())) //Si la palabra no pertenece a la lista de palabras vacias, la guardamos
                {
                    //Las palabras vacias están escritas en minuscula por lo que las comparamos pasando la palabra actual del txt a minuscula

                    // ----------------------------------- STEMMING ---------------------------------------------
                    /*PorterStemmer stemmer = new PorterStemmer();
                    stemmer.setCurrent(palabra); //Seleccionamos la palabra que vamos a stemmizar
                    stemmer.stem();  //La stemmizamos
                    palabra = stemmer.getCurrent();//Nos devuelve la palabra stemmizada*/

                    SnowballStemmer stemmer = new englishStemmer();
                    stemmer.setCurrent(palabra); //Establecemos la palabra a la que queremos hacerle stemming
                    if (stemmer.stem()) //Si el stem es correcto
                    {
                        palabra = stemmer.getCurrent(); //Modificacmos la palabra*/
                        // ----------------------------------- FIN --------------------------------------------
                        palabra = palabra.toLowerCase();
                        palabras_fin.add(palabra);
                    }
                }

            }

        }

        return palabras_fin;
    }

    ///////////////////////////////////////////////////////////////// CALCULAR_TF //////////////////////////////////////////////////////////////////////////
    public static Map<String, Integer> CalcularTF(List<String> texto_procesado)
    {
        Map<String, Integer> Palabras_TF = new HashMap<>();
        for(String termino: texto_procesado)
        {
            if(Palabras_TF.containsKey(termino))
            {
                int frecuencia = Palabras_TF.get(termino); //Devuelve el valor del termino
                frecuencia += 1;
                Palabras_TF.put(termino, frecuencia);
            }
            else
            {
                Palabras_TF.put(termino, 1);
            }
        }
        //System.out.println(Palabras_TF);
        //Palabras_TF.clear(); //Borramos el hashmap cuando acabe la funcion
        return Palabras_TF;
    }

    //////////////////////////////////////////////////////////// CALCULAR_TF_PASO_2////////////////////////////////////////////////////////////////////////////////////
    public static  Map<String, Map<Double, Map<String, Double>>> CalcularTF_Paso2(Map<String, Integer> termsFrecuencia, String DocID)
    {
        Map<String, Map<Double, Map<String, Double>>> Indice_Invertido = new HashMap<>();

        for (Map.Entry<String, Integer> termino : termsFrecuencia.entrySet()) {
            double tf = 1 + (Math.log(termino.getValue()) / Math.log(2));

            Map<Double, Map<String, Double>> segundaCapa;
            Map<String, Double> parejaDocPeso;

            // Se verifica si el término actual ya existe en la primera capa del mapa. Si existe, se obtiene el mapa asociado; de lo contrario, se crea uno nuevo.
            if (Indice_Invertido.containsKey(termino.getKey())) {
                segundaCapa = Indice_Invertido.get(termino.getKey());
            } else {
                segundaCapa = new HashMap<>();
            }

            // Se verifica si el valor de tf ya existe en la segunda capa del mapa. Si existe, se obtiene el mapa asociado; de lo contrario, se crea uno nuevo.
            if (segundaCapa.containsKey(tf)) {
                parejaDocPeso = segundaCapa.get(tf);
            } else {
                parejaDocPeso = new HashMap<>();
            }

            // Se almacena la información en la tercera capa del mapa, asociada al DocID y al valor de tf. Luego, se actualiza la segunda capa y finalmente la primera capa del mapa tridimensional.
            parejaDocPeso.put(DocID, tf);
            segundaCapa.put(0.0, parejaDocPeso);
            Indice_Invertido.put(termino.getKey(), segundaCapa);
        }
        //System.out.println(Indice_Invertido);
        return Indice_Invertido;
    }

    //////////////////////////////////////////////////////////// CALCULAR_IDF ////////////////////////////////////////////////////////////////////////////////////
    public static void CalcularIDF(Map<String, Map<Double, Map<String, Double>>> Indice_Invertido, int cont)
    {
        double denominador = 0;
        for(Map.Entry<String, Map<Double, Map<String, Double>>> entry : Indice_Invertido.entrySet())
        {
            double IDF;
            String termino = entry.getKey();
            Map<Double, Map<String, Double>> valor = entry.getValue();
            //Obtener en cuantos documentos aparece cada término
            for (Map.Entry<Double, Map<String, Double>> entryExterior : valor.entrySet())
            {
                Map<String, Double> mapaInterior = entryExterior.getValue();
                // Obtener el tamaño del segundo mapa
                denominador = mapaInterior.size();
            }
            if(cont < denominador)
                IDF = 0;
            else
                IDF = (Math.log((double) cont / denominador) / Math.log(2));
            // Actualizar la estructura con el IDF
            Map<Double, Map<String, Double>> segundaCapaConIDF = new HashMap<>();
            for (Map.Entry<Double, Map<String, Double>> parejaDocPeso : valor.entrySet())
            {
                segundaCapaConIDF.put(IDF, parejaDocPeso.getValue());
            }

            //System.out.println(termino + segundaCapaConIDF);
            // Almacenar el resultado en la nueva estructura
            Indice_Invertido.put(termino, segundaCapaConIDF);
        }
    }
    /////////////////////////////////////////////////////////////
    private static void EscribirEnFicheroIndiceInvertidoJSON(Map<String, Map<Double, Map<String, Double>>> mapa) {
        String nombreArchivo = "IndiceInvertido.json";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
            JSONObject resultObject = new JSONObject();

            Set<Map.Entry<String, Map<Double, Map<String, Double>>>> entrySet = mapa.entrySet();

            for (Map.Entry<String, Map<Double, Map<String, Double>>> entry : entrySet) {
                String termino = entry.getKey();
                Map<Double, Map<String, Double>> info = entry.getValue();

                JSONObject termObject = new JSONObject();
                JSONArray docsArray = new JSONArray();

                for (Map.Entry<Double, Map<String, Double>> subEntry : info.entrySet()) {
                    Double idf = subEntry.getKey();
                    Map<String, Double> docInfo = subEntry.getValue();

                    JSONObject docObject = new JSONObject();
                    docObject.put("IDF", idf);

                    JSONArray weightsArray = new JSONArray();

                    for (Map.Entry<String, Double> docEntry : docInfo.entrySet()) {
                        JSONObject weightObject = new JSONObject();
                        weightObject.put("Documento", docEntry.getKey());
                        weightObject.put("Peso", docEntry.getValue());
                        weightsArray.put(weightObject);
                    }

                    docObject.put("Pesos", weightsArray);
                    docsArray.put(docObject);
                }

                termObject.put("Término", termino);
                termObject.put("Documentos", docsArray);
                resultObject.put(termino, termObject);
            }

            // Escribir el JSON en el archivo
            writer.write(resultObject.toString(2)); // El segundo argumento indica el número de espacios de indentación
            System.out.println("Índice invertido escrito en el archivo: " + nombreArchivo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    private static void EscribirEnFicheroIndiceInvertido(Map<String, Map<Double, Map<String, Double>>> mapa) {
        String nombreArchivo = "IndiceInvertido.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
            Set<Map.Entry<String, Map<Double, Map<String, Double>>>> entrySet = mapa.entrySet();

            for (Map.Entry<String, Map<Double, Map<String, Double>>> entry : entrySet) {
                String termino = entry.getKey();
                Map<Double, Map<String, Double>> info = entry.getValue();

                writer.write(termino + " → |");

                for (Map.Entry<Double, Map<String, Double>> subEntry : info.entrySet()) {
                    Double idf = subEntry.getKey();
                    Map<String, Double> docInfo = subEntry.getValue();

                    writer.write(" IDF: " + idf + " | (");

                    for (Map.Entry<String, Double> docEntry : docInfo.entrySet()) {
                        writer.write(docEntry.getKey() + "-" + docEntry.getValue() + " ");
                    }
                }

                writer.write(")\n");
            }

            System.out.println("Índice invertido escrito en el archivo: " + nombreArchivo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static Map<String, Double> calcularLongitudDocumentos(Map<String, Map<Double, Map<String, Double>>> Indice_Invertido) {
        Map<String, Double> longitudesDocumentos = new HashMap<>();

        // Iterar sobre cada término
        for (Map.Entry<String, Map<Double, Map<String, Double>>> terminoEntry : Indice_Invertido.entrySet()) {
            //String termino = terminoEntry.getKey();
            Map<Double, Map<String, Double>> idfInfo = terminoEntry.getValue();

            // Iterar sobre cada IDF para el término actual
            for (Map.Entry<Double, Map<String, Double>> idfEntry : idfInfo.entrySet()) {
                double idfTerm = idfEntry.getKey();

                // Iterar sobre cada documento en el IDF actual
                for (Map.Entry<String, Double> pesoEntry : idfEntry.getValue().entrySet()) {
                    String nombreDocumento = pesoEntry.getKey();
                    double tfIdf = pesoEntry.getValue() * idfTerm; // Multiplicar por IDF

                    // Calcular la suma de los cuadrados de (TF-IDF)
                    double sumaCuadrados = longitudesDocumentos.getOrDefault(nombreDocumento, 0.0);
                    sumaCuadrados += tfIdf * tfIdf;

                    // Actualizar la suma de cuadrados en el mapa
                    longitudesDocumentos.put(nombreDocumento, sumaCuadrados);
                }
            }
        }

        // Calcular la raíz cuadrada para obtener la longitud final
        for (Map.Entry<String, Double> entry : longitudesDocumentos.entrySet()) {
            double longitudDocumento = Math.sqrt(entry.getValue());
            longitudesDocumentos.put(entry.getKey(), longitudDocumento);
        }

        //System.out.println(longitudesDocumentos);
        return longitudesDocumentos;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static void Escribir_fichero(Map<String, Double> longitudesDocumentos) {
        String nombreArchivo = "LongDocuments.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
            for (Map.Entry<String, Double> entry : longitudesDocumentos.entrySet()) {
                writer.write(entry.getKey() + " → " + entry.getValue() + "\n");
            }
            System.out.println("Longitudes de documentos escritas en el archivo: " + nombreArchivo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static String obtenerLongitudDocumento(String longitudContent, String documentoId) {
        // Separa las líneas del archivo de longitud
        String[] lines = longitudContent.split("\\r?\\n");

        // Busca la línea que contiene la información del documento
        for (String line : lines) {
            if (line.contains(documentoId)) {
                // Extrae la longitud de la línea
                String[] parts = line.split(" → ");
                if (parts.length > 1) {
                    return parts[1].trim();
                }
            }
        }

        return "No se encontró información de longitud para el documento";
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static String leerContenidoDocumento(String documentoId) throws IOException {
        // Construye la ruta del archivo del documento
        String rutaDocumento = "corpus/" + documentoId;

        // Lee el contenido del archivo y lo devuelve como cadena
        return new String(Files.readAllBytes(Paths.get(rutaDocumento)));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Numerador: tf * IDF^2
    //Denominador: longDocumento * sqrt(IDF^2 del termino)
    private static double CalcularRanking (double pesoTer, double IDFTer, String longitudInfo)
    {
        double ranking;
        double numerador = pesoTer * Math.pow(IDFTer, 2);
        Double longDocumento = Double.parseDouble(longitudInfo);
        double denominador = longDocumento * Math.sqrt(Math.pow(IDFTer, 2));

        if(numerador == 0 && denominador == 0)
            ranking = 0;
        else
            ranking = numerador / denominador;
        return ranking;
    }
    //////////////////////////////////////////////////////// RECUPERAR DOCUMENTOS //////////////////////////////////////////////////////////////////////////////////
    public static List<String> recuperarDocumentos(List<String> texto_procesado)
    {
        System.out.println("¿Qué tipo de consulta deseas hacer? (Introduce el número de la consulta)");
        System.out.println("1.- Consulta simple");
        System.out.println("2.- Consulta AND");
        System.out.println("3.- Consulta OR");
        System.out.println("4.- Consulta de frases");
        Scanner scanner = new Scanner(System.in);
        Integer consulta = Integer.valueOf(scanner.nextLine());
        switch (consulta)
        {
            case 1:
                System.out.print("Introduce tu consulta a recuperar: ");
                Scanner scanner2 = new Scanner(System.in);
                String consulta2 = scanner2.nextLine();
                String[] terminos2 = consulta2.split("\\s+");
                busquedas(consulta, terminos2);
                break;
            //---------------------------------------------------------------------------------------------------------------------------------------
            case 2:
                System.out.print("Introduce tu consulta a recuperar (termino1 AND termino2): ");
                scanner2 = new Scanner(System.in);
                consulta2 = scanner2.nextLine();
                terminos2 = consulta2.split(" AND ");
                busquedas(consulta, terminos2);
                break;

            // --------------------------------------------------------------------------------------------------------------
            case 3:
                System.out.print("Introduce tu consulta a recuperar (termino1 OR termino2): ");
                scanner2 = new Scanner(System.in);
                consulta2 = scanner2.nextLine();
                terminos2 = consulta2.split(" OR ");
                busquedas(consulta, terminos2);
                break;
            //---------------------------------------------------------------------------------------------------------------------------------------
            case 4:
                System.out.print("Introduce la frase a buscar: ");
                scanner2 = new Scanner(System.in);
                consulta2 = scanner2.nextLine();
                terminos2 = consulta2.split("\\s+");
                busquedas(consulta, terminos2);
                break;

                //---------------------------------------------------------------------------------------------------------------------------------------
            default:
                System.out.println("No existe la consulta pedida.");
        }
        return texto_procesado;
    }

    public static void busquedas (int opcion, String[] terminos2)
    {

        System.out.println("Aplicando los filtros");

        // Realiza el stemming de los términos antes de buscarlos
        SnowballStemmer stemmer2 = new englishStemmer();
        List<String> palabras = new ArrayList<>();

        for (String termino : terminos2) {
            String terminoSinEspacios = termino.trim();
            stemmer2.setCurrent(terminoSinEspacios.toLowerCase());
            if (stemmer2.stem()) {
                palabras.add(stemmer2.getCurrent());
            }
        }

        // Crear un mapa para almacenar el documento ID y su ranking
        List<Map.Entry<String, Double>> rankingEntries2 = new ArrayList<>();
        try {
            // Lee el contenido del archivo JSON una sola vez
            String jsonContent = new String(Files.readAllBytes(Paths.get("IndiceInvertido.json")));
            String longitudContent = new String(Files.readAllBytes(Paths.get("LongDocuments.txt")));

            // Parsea el contenido del JSON a un objeto JSONObject
            JSONObject jsonObject = new JSONObject(jsonContent);

            // Iterar sobre los documentos asociados al primer término
            boolean terminoEncontrado = false;  // Agrega una variable de control
            for (String termino : palabras) {
                if (jsonObject.has(termino)) {
                    // El término se encuentra en el JSON
                    terminoEncontrado = true;  // Actualiza la variable de control
                    JSONObject termObject = jsonObject.getJSONObject(termino);
                    JSONArray docsArray = termObject.getJSONArray("Documentos");
                    // Muestra la información asociada al término
                    for (int i = 0; i < docsArray.length(); i++) {
                        JSONObject docObject = docsArray.getJSONObject(i);
                        JSONArray pesosArray = docObject.getJSONArray("Pesos");
                        for (int j = 0; j < pesosArray.length(); j++) {
                            JSONObject pesoObject = pesosArray.getJSONObject(j);
                            String documentoId = pesoObject.getString("Documento");
                            // Lee la longitud del documento desde el archivo "LongDocuments.txt"
                            String longitudInfo = obtenerLongitudDocumento(longitudContent, documentoId);
                            double pesoTer = pesoObject.getDouble("Peso");
                            double IDFTer = docObject.getDouble("IDF");
                            double ranking = CalcularRanking(pesoTer, IDFTer, longitudInfo);
                            // Almacena la entrada del ranking en la lista
                            rankingEntries2.add(new AbstractMap.SimpleEntry<>(documentoId, ranking));

                        }
                    }
                }
            }
            // Mueve la impresión del mensaje fuera del bucle
            if (!terminoEncontrado) {
                System.out.println("Ninguno de los términos se encuentra en el índice invertido.");
            }

            switch(opcion)
            {
                case 1:
                    // Ordena la lista de entradas del ranking de mayor a menor
                    rankingEntries2.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
                    // Imprime el contenido ordenado del documento
                    for (Map.Entry<String, Double> entry : rankingEntries2) {
                        System.out.println("Documento ID: " + entry.getKey() + " (Ranking :" + entry.getValue() + ")");
                        String contenidoDocumento = leerContenidoDocumento(entry.getKey());
                        System.out.println("Contenido del documento: " + contenidoDocumento);
                    }
                    break;
                //---------------------------------------------------------------------------------------------------------------------------------------
                case 2:
                    List<Map.Entry<String, Double>> resultado = conservarDuplicados(rankingEntries2);
                    if(resultado.isEmpty())
                    {
                        System.out.println("No coinciden ambos terminos en un documento");
                    }
                    else
                    {
                        // Ordena la lista de entradas del ranking de mayor a menor
                        resultado.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
                        // Imprime el contenido ordenado del documento
                        for (Map.Entry<String, Double> entry : resultado) {
                            System.out.println("Documento ID: " + entry.getKey() + " (Ranking :" + entry.getValue() + ")");
                            String contenidoDocumento = leerContenidoDocumento(entry.getKey());
                            System.out.println("Contenido del documento: " + contenidoDocumento);
                        }
                    }
                    break;
                //---------------------------------------------------------------------------------------------------------------------------------------
                case 3:
                    List<Map.Entry<String, Double>> resultado2 = eliminarDuplicados(rankingEntries2);
                    if(resultado2.isEmpty())
                    {
                        System.out.println("No existen o estan en el mismo documento");
                    }
                    else
                    {
                        // Ordena la lista de entradas del ranking de mayor a menor
                        resultado2.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
                        // Imprime el contenido ordenado del documento
                        for (Map.Entry<String, Double> entry : resultado2) {
                            System.out.println("Documento ID: " + entry.getKey() + " (Ranking :" + entry.getValue() + ")");
                            String contenidoDocumento = leerContenidoDocumento(entry.getKey());
                            System.out.println("Contenido del documento: " + contenidoDocumento);
                        }
                    }
                    break;
                //---------------------------------------------------------------------------------------------------------------------------------------
                case 4:
                    // Búsqueda de frases
                    List<Map.Entry<String, Double>> resultadosFrase = buscarFraseEnDocumentos(terminos2, rankingEntries2);
                    // Ordena la lista de entradas del ranking de mayor a menor
                    resultadosFrase.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
                    // Imprime el contenido ordenado del documento
                    for (Map.Entry<String, Double> entry : resultadosFrase) {
                        System.out.println("Documento ID: " + entry.getKey() + " (Ranking :" + entry.getValue() + ")");
                        String contenidoDocumento = leerContenidoDocumento(entry.getKey());
                        System.out.println("Contenido del documento: " + contenidoDocumento);
                    }
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private static List<Map.Entry<String, Double>> conservarDuplicados(List<Map.Entry<String, Double>> rankingEntries) {
        // Conjunto para rastrear los documentos duplicados
        Set<String> documentosDuplicados = new HashSet<>();
        // Filtrar la lista original, conservando solo las instancias duplicadas
        return rankingEntries.stream()
                .filter(entry -> !documentosDuplicados.add(entry.getKey())) // Agrega al conjunto y filtra si ya estaba presente
                .collect(Collectors.toList());
    }

    private static List<Map.Entry<String, Double>> eliminarDuplicados(List<Map.Entry<String, Double>> rankingEntries) {
        // Mapa para realizar un seguimiento de los valores y sus frecuencias
        Map<Double, Integer> frecuencias = new HashMap<>();
        // Lista para almacenar los resultados sin duplicados
        List<Map.Entry<String, Double>> resultado = new ArrayList<>();
        // Iterar sobre la lista original
        for (Map.Entry<String, Double> entry : rankingEntries) {
            // Obtener el valor actual
            Double valor = entry.getValue();
            // Verificar si el valor ya está presente en el mapa
            if (frecuencias.containsKey(valor)) {
                // Incrementar la frecuencia
                frecuencias.put(valor, frecuencias.get(valor) + 1);
            } else {
                // Agregar el valor al mapa con frecuencia 1
                frecuencias.put(valor, 1);
            }
        }
        // Iterar sobre la lista original nuevamente y agregar solo las entradas con frecuencia 1
        for (Map.Entry<String, Double> entry : rankingEntries) {
            if (frecuencias.get(entry.getValue()) == 1) {
                resultado.add(entry);
            }
        }
        return resultado;
    }
    private static List<Map.Entry<String, Double>> buscarFraseEnDocumentos(String[] fraseBuscada, List<Map.Entry<String, Double>> rankingEntries) throws IOException {
        // Conjunto para rastrear los documentos únicos
        Set<String> documentosUnicos = new HashSet<>();

        // Lista para almacenar los resultados de la búsqueda de frases
        List<Map.Entry<String, Double>> resultadosFrase = new ArrayList<>();

        // Iterar sobre los documentos asociados a los términos
        for (Map.Entry<String, Double> entry : rankingEntries) {
            String documentoId = entry.getKey();

            // Verifica si el documento ya se encuentra en el conjunto de documentos únicos
            if (documentosUnicos.contains(documentoId)) {
                continue;  // Si ya existe, salta al siguiente documento
            }

            // Lee el contenido del documento
            String contenidoDocumento = leerContenidoDocumento(documentoId);

            // Verifica si la frase buscada está presente en el contenido del documento
            if (buscarFraseEnDocumento(fraseBuscada, Arrays.asList(contenidoDocumento.split("\\s+")))) {
                // Agrega la entrada del ranking a los resultados de la frase
                resultadosFrase.add(entry);

                // Agrega el documento al conjunto de documentos únicos
                documentosUnicos.add(documentoId);
            }
        }

        return resultadosFrase;
    }
    private static boolean buscarFraseEnDocumento(String[] fraseBuscada, List<String> palabrasDocumento) {
        boolean flag = false;
        int fraseLength = fraseBuscada.length;
        // Iterar sobre las palabras del documento para buscar la frase
        for (int i = 0; i <= palabrasDocumento.size() - fraseLength; i++) {
            boolean coincidencia = true;
            // Verificar si la secuencia de palabras en el documento coincide con la frase buscada
            for (int j = 0; j < fraseLength; j++) {
                if (!fraseBuscada[j].equalsIgnoreCase(palabrasDocumento.get(i + j))) {
                    coincidencia = false;
                    break;
                }
            }
            // Si encontramos una coincidencia, devolvemos true
            if (coincidencia) {
                flag = true;
            }
        }
        // Si no se encuentra ninguna coincidencia, devolvemos false
        return flag;
    }
}
