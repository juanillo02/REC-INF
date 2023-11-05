import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class practica10
{
    public static void main(String[] args)
    {
        Pattern pat = Pattern.compile(".*\\b(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[0-9][0-9]|[0-9])\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[0-9][0-9]|[0-9])\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[0-9][0-9]|[0-9])\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[0-9][0-9]|[0-9])\\b.*");
        Matcher mat = pat.matcher("La direccion IP es: 192.168.1.1");
        if (mat.matches())
        {
            System.out.println("SI");
        }else{
            System.out.println("NO");
        }
    }
}