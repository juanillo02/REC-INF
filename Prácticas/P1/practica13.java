import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class practica13
{
    public static void main(String[] args)
    {
        Pattern pat = Pattern.compile("v(i|!|1)(@|a)gr(a|@)");
        Matcher mat = pat.matcher("holav1@gra@gmail.com");
        if (mat.matches())
        {
            System.out.println("SI");
        }else{
            System.out.println("NO");
        }
    }
}
