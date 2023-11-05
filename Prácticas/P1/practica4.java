import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class practica4
{
    public static void main(String[] args)
    {
        Pattern pat = Pattern.compile(".*\\D$");
        Matcher mat = pat.matcher("9aas");
        if (mat.matches())
        {
            System.out.println("SI");
        }else{
            System.out.println("NO");
        }
    }
}