import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Main
{
    public static void main (String[] args) throws IOException {
        String enlace = "https://wikipedia.org";
        File archivo = new File("Crawler.txt");
        if (!archivo.exists())
        {
            if(archivo.createNewFile())
            {
                System.out.println("Archivo creado correctamente.");
            }
        }
        BufferedWriter vaciado = new BufferedWriter((new FileWriter(archivo, false)));
        vaciado.close();
        ArrayList<String> Cerrados = new ArrayList<>();
        ArrayList<String> Abiertos = new ArrayList<>();
        ArrayList<String> Sucesores = new ArrayList<>();
        crawl(archivo, enlace, Cerrados, Abiertos, Sucesores);
    }
    private static int i = 0;
    private static void crawl(File archivo, String enlace, ArrayList<String> Cerrados, ArrayList<String> Abiertos, ArrayList<String> Sucesores) throws IOException
    {
        Abiertos.add(enlace);
        while(!Abiertos.isEmpty())
        {
            String Actual = Abiertos.get(0);
            Abiertos.remove(0);
            if(!Cerrados.contains(Actual))
            {
                Document enlace_sig = Jsoup.connect(enlace).get();
                try
                {
                    for (Element seleccion : enlace_sig.select("a"))
                    {
                        String enlace_abs = seleccion.absUrl("href");
                        if (esURL(enlace_abs) && !Sucesores.contains(enlace_abs))
                        {
                            BufferedWriter escribir = new BufferedWriter(new FileWriter(archivo, true));
                            if (Cerrados.isEmpty())
                            {
                                escribir.write("Link raiz: " + Actual);
                                System.out.println("Link raiz: " + Actual);
                                for (Element foto : enlace_sig.select("img"))
                                {
                                    String imagen = foto.absUrl("src");
                                    try
                                    {
                                        BufferedImage imagen_descarga = ImageIO.read(new URI(imagen).toURL());
                                        ImageIO.write(imagen_descarga, "png", new File("fotodescarga.png"));
                                    } catch (IOException | URISyntaxException f) {
                                        System.out.println("Error de imagen, " + f);
                                    }
                                }
                            }
                            else
                            {

                                i++;
                                escribir.write("El link numero " + i + " : " + Actual);
                                System.out.println("El link numero " + i + " : " + Actual);
                            }
                            escribir.newLine();
                            escribir.close();
                            Sucesores.add(enlace_abs);
                            Cerrados.add(Actual);
                            crawl(archivo, enlace_abs, Cerrados, Abiertos, Sucesores);
                        }
                    }
                }
                catch (Exception en)
                {
                    System.out.println("ERROR. " + en.getMessage());
                }
            }
        }
    }
    private static boolean esURL(String enlace) throws IOException
    {
        boolean objetivo = false;
        try(BufferedReader comprueba = new BufferedReader( new StringReader(enlace)))
        {
            Pattern buscar = Pattern.compile("^https://es\\.wikipedia\\.org.*");
            String comprobar;
            while((comprobar = comprueba.readLine()) != null)
            {
                if(buscar.matcher(comprobar).matches())
                {
                    try(BufferedReader comprueba1 = new BufferedReader( new StringReader(comprobar)))
                    {
                        Pattern buscar1 = Pattern.compile(".*#.*");
                        Pattern index = Pattern.compile(".*index\\.php.*");
                        String comprobar1;
                        while((comprobar1 = comprueba1.readLine()) != null)
                        {
                            if(!(buscar1.matcher(comprobar1).matches()))// || (!(numeros.matcher(comprobar1).matches())))
                            {
                                if(!(index.matcher(comprobar1).matches()))
                                {
                                    objetivo = true;

                                }
                            }
                            else
                            {
                                objetivo = false;
                            }
                        }
                    }
                }
                else
                {
                    try(BufferedReader comprueba2 = new BufferedReader( new StringReader(comprobar)))
                    {
                        Pattern buscar2 = Pattern.compile(".*country=ES.*");
                        String comprobar2;
                        while((comprobar2 = comprueba2.readLine()) != null)
                        {
                            if(buscar2.matcher(comprobar2).matches())
                            {
                                try(BufferedReader comprueba3 = new BufferedReader( new StringReader(comprobar2)))
                                {
                                    Pattern buscar3 = Pattern.compile(".*(language\\|uselang)=es.*");
                                    String comprobar3;
                                    while((comprobar3 = comprueba3.readLine()) != null)
                                    {
                                        if(buscar3.matcher(comprobar3).matches())
                                        {
                                            objetivo = true;
                                        }
                                        else
                                        {
                                            objetivo = false;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return objetivo;
    }
}
