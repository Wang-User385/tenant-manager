import com.hand.hls.bp.dto.HlsScoreCalculation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author kalvin
 * @version 1.0
 * @Date 2021/3/30 21:26
 * @Description
 **/
public class Test {

    public static void main(String[] args) {

          /*  Calendar curr = Calendar.getInstance();
            curr.set(Calendar.YEAR,curr.get(Calendar.YEAR)+1);
            Date date=curr.getTime();
            System.out.println(date);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        System.out.println(df.format(new Date()));// new Date()为获取当前系统时间

        System.out.println(df.format(date));
*/

        HlsScoreCalculation hlsScoreCalculation = new HlsScoreCalculation();
        hlsScoreCalculation.setGradeStatus("APPROVED");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        Calendar curr = Calendar.getInstance();
        curr.set(Calendar.YEAR,curr.get(Calendar.YEAR)+1);
        Date date=curr.getTime();
        hlsScoreCalculation.setFromDate(new Date());
    }

}
