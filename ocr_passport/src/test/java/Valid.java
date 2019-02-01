import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @ClassName Valid
 * @Description
 * @Author shijie.lyu
 * @Date 2019-01-12 23:07
 * @Version 1.0
 */
public class Valid {

    public static void vvvvv () {
        //String info = "P0CHNGUAN<<GU0JUN<<<<<<<<<<<<<<<<<<<<<<<<<";
        String info = "P<ESPSANTAMARIA<CALVO<<GUILLERMO<<<<<<<<<<<<";
        System.out.println(info);
        String[] split = info.split("<<");
        String a = split[0];
        String type = a.substring(0, 1);
        System.out.println(type);
        String code = a.substring(2, 5);
        System.out.println(code);
        String surname = a.substring(5, a.length());
        if(surname.contains("<")){
            surname = surname.replaceAll("<"," ");
        }
        System.out.println(surname);
        String givenname = split[1];
        System.out.println(givenname);

        //String second = "E994340625cHN9506044M2704162LJNMLJPKL0PLA902";
        String second = "PAH6577511EsP0903206M2308146A7279908700<<<94";
        String pid = second.substring(0, 9);
        System.out.println(pid);
        String code2 = second.substring(10, 13).toUpperCase();
        System.out.println(code2);
        String birth = second.substring(13, 19);
        System.out.println(formatter(birth));
        String sex = second.substring(20, 21);
        System.out.println(sex);
        String expiry = second.substring(21, 27);
        System.out.println(formatter(expiry));
    }

    public static  String formatter(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
        LocalDate dt = LocalDate.parse(date, formatter);
        String result = dt.format(DateTimeFormatter.ofPattern("ddMMMyy",Locale.US));
        return result.toUpperCase();
    }

    public static void main(String[] args) {
//        String a ="POCHNMOU<<YUFEI<<<<<<《<<<<<<<<<<<<<<<<<<";
//        a = a.replaceAll("《","<");
//        System.out.println(a.length());
//        System.out.println(a);

//        String b ="<<<<<<<<<";
//        if(b.matches("^[A-Za-z0-9\\\\<]+$")){
//            System.out.println("ok");
//        }else {
//            System.out.println("error");
//        }

//        String  c ="ssd425<<<";
//        String d =c;
//        c ="aaaaaaaa";
//        System.out.println(d.toUpperCase());words.matches("^[A-Za-z0-9\\\\<]+$")

//        String dd = "LYU, SHIJIE";
//        dd = dd.replaceAll(",",".");
//        dd = dd.replaceAll("，",".");
//        dd = dd.replaceAll(" ",".");
//        if(dd.contains("..")){^\w+$
//            dd = dd.replaceAll("\\.\\.",".");
//        }
//        String[] split = dd.split("\\.");
//        String aa = split[0];
//        String bb = split[1];
//        System.out.println(dd);

        String aa = "G544364585cHN7410046M210922819205000<<<<<<16F";
        if (aa.matches("[A-Za-z].*[0-9]")) {
            System.out.println("****");
        }
        System.out.println(aa.length());

    }
}
